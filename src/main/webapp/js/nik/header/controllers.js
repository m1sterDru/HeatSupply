var heatSupply = Object.create(null);
heatSupply.headerControllers = angular.module('headerControllers', [
	'ngRoute',
	'headerFactory']);

heatSupply.headerControllers.config(function ($routeProvider){
	$routeProvider.
		when('/profile', {
			templateUrl: function(){
				return 'html/templates/profileTemplate.html';
			},
			controller: 'profileController'
		}).
		when('/account', {
			templateUrl: function(){
				return 'html/templates/accountTemplate.html';
			},
			controller: 'accountController'
		}).
		when('/addOwner', {
			templateUrl: function(){
				return 'html/templates/addOwnerAccountTemplate.html';
			},
			controller: 'ownerAccountController'
		}).
		when('/delOwner', {
			templateUrl: function(){
				return 'html/templates/delOwnerAccountTemplate.html';
			},
			controller: 'ownerAccountController'
		})
})
.run(function ($rootScope, $location, hsFactory){
	$rootScope.$on("$routeChangeStart", function (event, next, current){
		var isValid = false;

		switch($location.path()){
			case '':
			case '/':
			case '/login':
			case '/registration': isValid = true; break;
			default: isValid = false;
		}

		if(!isValid){
			hsFactory.getUserProfile(function(){
				hsFactory.translator.translateAll();
			});
		}
	});
});

heatSupply.headerControllers.controller('headerController', 
	function ($scope, translate, hsFactory){
		$scope.login_href = '#login';
		$scope.login_spanID = '${kLogin}';
		$scope.longin_class = 'fa fa-sign-in';
		$scope.login_userIconClass = 'isHide';

		// if(location.href.indexOf('login') > 0)
		// 	$scope.login_userSpanClass = 'isHide';

		translate.langFiles(function(files){
			var locales = [], index = 1;
			files.forEach(function(file){
				var locale = Object.create(null);
				locale.id = file;
				locales.push(locale);
				hsFactory.translator.translateValueByKey(file, 
					['kFlagLocale','kLangName'],function(value){
						if(value.indexOf('http') != -1)
							locale.img = value;
						else{
							locale.langName = value;
							if((index++) == files.length){
								$scope.$apply();
								$scope.changeLocale(hsFactory.language);
							}
						}
					});
			});
			$scope.locales = locales;
		});

		$scope.changeLocale = function (langId){
			var btn = $('button[data-btn="curLangButton"]:first')[0],
					lis, li, img, span;

			if(btn){
				lis = btn.parentNode.getElementsByTagName('ul')[0]
									.getElementsByTagName('li');

				li = Array.prototype.filter.call(lis, function(li){
					return li.id === langId;
				})[0];
				if(!li){
					console.log('null'); 
					return;
				}

				img = li.getElementsByTagName('img')[0];
				span = li.getElementsByTagName('span')[0];

				$scope.langId = langId;
				$scope.langImg = img.src;
				$scope.langDesc = span.innerHTML;

				hsFactory.language = langId;
				translate.run(function(t){
					t.translateAllByLocaleName(langId);
					localStorage.setItem('heatSupply', JSON.stringify(hsFactory));
				});
			}
		}
	})
	.controller('profileController', 
		function ($scope, hsFactory, $http){
			var error = $('.comment:first'),
					error2 = $('.comment:last'),
					userId;

			hsFactory.getUserProfile(function(data){
				var message = Object.create(null);

				message.type = 'CommandMessage';
				message.command = 'profileInfo';
				message.parameters = [{'userId': data.userId}];
				heatSupply.socket.send(JSON.stringify(message));
				$scope.isDisabled = true;
				$scope.formStyle = {opacity: 0.5};
				userId = data.userId;
			});

			$scope.submitProfile = function(isButton){
				var isValid = true;
				$('#profileInfo form input').each(function(){
					if(!this.checkValidity()){
						isValid = false;
						error.html('Set correct form\'s data');
						if(isButton){
							setTimeout(function(){
								$('#btnHide').click();
							},100);
						}
						return;
					}
				});
				if(isValid){
					var isUpdate = true,
							pwd1 = $('#profileInfo input[name="pwd1"]').val(),
							pwd2 = $('#profileInfo input[name="pwd2"]').val();

					if(pwd1 != pwd2){
						error.html('New passwords are different ...');
					} else {
						updateProfile(function(data){
							if(data.messageId != 0)
								error.html(data.message);
							else
								window.location.href = hsFactory.url + 'main.html';
						});
					}
				}
			}

			$scope.removeProfile = function(){
				hsFactory.translator.translateValueByKey(hsFactory.language, 'keySure',
				function(value){
					if(confirm(value)){
						hsFactory.getUserProfile(function(data){
							var message = Object.create(null);

							message.type = 'CommandMessage';
							message.command = 'removeProfile';
							message.parameters = [{'userId': data.userId}];
							heatSupply.socket.send(JSON.stringify(message));
						});
					}
				});
			}

			function updateProfile(callback){
				$http({
					method: 'POST',
					url: '/HeatSupply/ProfileServlet',
					params: {
						idUser: userId,
						name: $('#profileInfo input[name="name"]').val(),
						middleName: $('#profileInfo input[name="middleName"]').val(),
						surName: $('#profileInfo input[name="surName"]').val(),
						email: $('#profileInfo input[name="email"]').val(),
						phone: $('#profileInfo input[name="phone"]').val(),
						password: $('#profileInfo input[name="pwd"]').val(),
						password1: $('#profileInfo input[name="pwd1"]').val(),
						languageId: hsFactory.language
					},
					cache: false
				})
				.success(function(data){
					if(callback != null) callback(data);
				})
				.error(function(data, status, headers, config){
					console.log(status)
				});
			}
		})
	.controller('accountController', function ($scope, hsFactory, $http){
		$scope.visibleClass = 'isHide';
		$scope.additionalClass = 'isHide';
		$scope.additionalIcon = 'fa-plus-square';

		hsFactory.getUserProfile(function(data){
			var message = Object.create(null);

			message.type = 'CommandMessage';
			message.command = 'ownerList';
			message.parameters = [
				{'userId': data.userId},
				{'elementId': 'accountTemplate'}
			];
			heatSupply.socket.send(JSON.stringify(message));
			$scope.login = data.user;
		});

		$scope.additional = function(){
			$scope.additionalClass = $scope.additionalClass === '' ?
				'isHide' : '';
			$scope.additionalIcon = $scope.additionalClass === '' ?
				'fa-minus-square' : 'fa-plus-square';
		}
	})
	.controller('ownerAccountController', function ($scope, hsFactory, $http){
		var error = $('.error');

		if($('#delAccountTemplate').length){
			$scope.isDisabled = true;
			hsFactory.getUserProfile(function(data){
				var message = Object.create(null);

				message.type = 'CommandMessage';
				message.command = 'ownerList';
				message.parameters = [
					{'userId': data.userId},
					{'elementId': 'delAccountTemplate'}
				];
				heatSupply.socket.send(JSON.stringify(message));
				$scope.login = data.user;
			});
		}

		function disable(isOn){
			var ico = $('.btn-danger span:first-child');
			$('#delAccountTemplate select').disabled = isOn;
			$('#delAccountTemplate button').disabled = isOn;
			$('#delAccountTemplate').css('opacity', isOn ? '0.5' : '1');
			if(isOn){
				ico.removeClass('fa-remove');
				ico.addClass('fa-refresh fa-spin');
			} else {
				ico.removeClass('fa-refresh fa-spin');
				ico.addClass('fa-remove');
			}
		}

		$scope.change4delete = function($event){
			var li = $event.target;
			while(li.tagName != 'LI') li = li.parentNode;
			$scope.account4delete2.name = li.getAttribute('ownerName');
			$scope.account4delete = li.getAttribute('idMeter') + '_' +
				li.getAttribute('ownerAccount');
			$scope.account4delete2.sn = li.getAttribute('snMeter');
			$scope.account4delete2.ownerAccount = li.getAttribute('ownerAccount');
		}

		$scope.deleteOwnerAccount = function(){
			hsFactory.translator.translateValueByKey(hsFactory.language, 'keySure',
				function(value){
					if(confirm(value)){
						disable(true);
						hsFactory.getUserProfile(function(data){
							var owneraccount = $scope.account4delete
										.slice($scope.account4delete.indexOf('_') + 1),
									idMeter = $scope.account4delete
										.slice(0, $scope.account4delete.indexOf('_')),
									message = Object.create(null);

							message.type = 'CommandMessage';
							message.command = 'deleteOwner';
							message.parameters = [
								{'userId': data.userId},
								{'idMeter': idMeter}
							];
							heatSupply.socket.send(JSON.stringify(message));
							disable(false);
						});
					}
				});
		}

		$scope.submitAddOwner = function(isButton){
			hsFactory.getUserProfile(function(data){
				var isValid = true;
				error.html('');
				$('.regDiv input').each(function(){
					if(!this.checkValidity()){
						isValid = false;
						$('.comment').html('Set correct form\'s data');
						if(isButton){
							setTimeout(function(){
								$('#btnHide').click();
							},100);
						}
						return;
					}
				});

				if(isValid){
					var message = Object.create(null);

					message.type = 'CommandMessage';
					message.command = 'addOwner';
					message.parameters = [
						{'account': $('.regDiv input[name="owneraccount"').val()},
						{'number': $('.regDiv input[name="meterNumber"').val()},
						{'userId': data.userId}
					];
					heatSupply.socket.send(JSON.stringify(message));
				}
			});
		}
	});
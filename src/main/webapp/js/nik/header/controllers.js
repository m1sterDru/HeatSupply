var heatSupply = Object.create(null);
heatSupply.headerControllers = angular.module('headerControllers', [
	'ngRoute',
	'headerFactory']);

heatSupply.headerControllers.config(function ($routeProvider){
	$routeProvider
		.when('/registration', {
			templateUrl: 'html/templates/addOwnerAccountTemplate.html',
			controller: 'ownerAccountController'
		})
		.when('/registration2', {
			templateUrl: 'html/login/loginRegistration.html',
			controller:'ownerAccountController'
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
		var hideSpan = location.href.indexOf('login') > 0 ||
									 location.href.indexOf('registration') > 0;

		$scope.longin_visClass = 'isHide';
		$scope.login_userSpanClass = location.href.indexOf('/main.') > 0 ?
			'isHide' : '';
		if(hideSpan) $scope.login_userSpanClass = 'isHide';

		translate.langFiles(function(files){
			var locales = [], index = 1;
			files.forEach(function(file){
				var locale = Object.create(null);
				locale.id = file;
				locales.push(locale);
				translate.run(function(t){
					t.translateValueByKey(file, ['kFlagLocale','kLangName'],
						function(value){
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
.controller('ownerAccountController', function ($scope, $http, hsFactory){
	if(location.href.indexOf('registration2') > 0 &&
		 hsFactory.regAccount == undefined){
		location.href = '#/registration';
	} else if(location.href.indexOf('registration') > 0){
		$('[pr-lang="kApply"]').attr('pr-lang', 'kNext');
	}

	$scope.isDisabled = true;
	$scope.afterProducer = 'isHide';
	$('form').parent().parent().css('opacity', '0.5');

	$scope.selectProvider = function(type){
		$scope.producerType = type;
		$scope.isDisabled = type == 0 ? true : false;
		$('form').parent().parent().css(
			'opacity', type == 0 ? '0.5' : '1'
		);
		$scope.afterProducer = type == 0 ? 'isHide' : '';
		$scope.beforProducer = type == 0 ? '' : 'isHide';
		if(type != 0){
			$scope.provider1 = type == 1 ? '' : 'isHide';
			$scope.provider2 = type == 2 ? 'col-sm-offset-1' : 'isHide';
		} else {
			$scope.provider1 = '';
			$scope.provider2 = '';
		}
	}

	$scope.submitRegistrationLast = function(isButton){
		var isValid = true,
				password = $('#regDiv2 input[name="password"]').val(),
				password2 = $('#regDiv2 input[name="password2"]').val();

		if(password != password2){
			hsFactory.updateError('keyNewPasswordsWrong');
		} else {
			$('#regDiv2 input').each(function(){
				if(!this.checkValidity()){
					isValid = false;
					hsFactory.updateError('keyNewPasswordsWrong');
					if(isButton){
						setTimeout(function(){
							$('#btnHide').click();
						},100);
					}
					return;
				}
			});
			if(isValid){
				registration(function(data){
					if(data.messageId != 0){
						console.log(data.array[0])
						hsFactory.updateErrorWithText(data.message, data.array[0]);
					}
					else
						location.href = hsFactory.url + 'main.html';
				});
			}
		}
	}

	function registration(callback){
		$http({
			method: 'POST',
			url: '/HeatSupply/RegisterServlet',
			params: {
				owneraccount: hsFactory.regAccount,
				meterNumber: hsFactory.regMeter,
				password: $('#regDiv2 input[name="password"]').val(),
				login: $('#regDiv2 input[name="login"]').val(),
				phone: $('#regDiv2 input[name="phone"]').val(),
				email: $('#regDiv2 input[name="email"]').val(),
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

	$scope.submitAddOwner = function(isButton){
		if(location.href.indexOf('registration') > 0){
			hsFactory.regAccount = $('.regDiv input[name="owneraccount"').val();
			hsFactory.regMeter = $('.regDiv input[name="meterNumber"').val();

			location.href = '#/registration2';
		} else {
			hsFactory.getUserProfile(function(data){
				var isValid = true;

				$('.regDiv input').each(function(){
					if(!this.checkValidity()){
						isValid = false;
						hsFactory.updateError('keyOwnerAccountError');
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

					$scope.actowners = [];
					message.type = 'CommandMessage';
					message.command = 'addOwner';
					message.parameters = [
						{'account': $('.regDiv input[name="owneraccount"').val()},
						{'number': $('.regDiv input[name="meterNumber"').val()},
						{'userId': data.userId},
						{'typeAccount': $scope.producerType + ''}
					];
					heatSupply.socket.send(JSON.stringify(message));
				}
			});
		}
	}
});
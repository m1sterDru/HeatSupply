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
		})
})
.run(function ($rootScope, $location, hsFactory){
	$rootScope.$on("$routeChangeStart", function (event, next, current){
		if($location.path() === '/profile'){
			hsFactory.getUserProfile(function(data){
				if(data.isLogin === 'false') location.href = hsFactory.url;
			});
		}
	});
});

heatSupply.headerControllers.controller('headerController', 
	function ($scope, translate, hsFactory){
		hsFactory.getUserProfile(function(){
			checkIsLogin();
		});
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
									changeLocale(hsFactory.language);
								}
							}
						});
				});
			});
			$scope.locales = locales;
		});

		$scope.test123 = function(){
			console.log('test123');
		}

		$scope.click = function($event){
			var btn = document.getElementById('curLangButton'),
					el = $event.target;

			if(el.tagName.toLowerCase() !== 'li') el = el.parentNode;
			changeLocale(el.id);
		}

		function changeLocale(langId){
			var btn = document.getElementById('curLangButton'),
					lis, li, img, span;

			if(btn){
				lis = btn.parentNode.getElementsByTagName('ul')[0]
									.getElementsByTagName('li');

				li = Array.prototype.filter.call(lis, function(li){
					return li.id === langId;
				})[0];
				if(!li) {
					console.log('null'); 
					return;
				}

				img = li.getElementsByTagName('img')[0];
				span = li.getElementsByTagName('span')[0];

				$scope.langId = langId;
				$scope.langImg = img.src;
				$scope.langDesc = span.innerHTML;
				// $scope.$apply();

				hsFactory.language = langId;
				translate.run(function(t){
					t.translateAllByLocaleName(langId);
					localStorage.setItem('heatSupply', JSON.stringify(hsFactory));
				});
			}
		}

		function checkIsLogin(){
			var isLogin = hsFactory.isLogin === 'true',
					aLogin = $('#aLogin');

			if(isLogin){
				document.getElementById('currentUser').innerHTML = hsFactory.user;
				$('#currentUserIcon').removeClass('isHide');
				aLogin[0].href = 'LogoutServlet';
				aLogin[0].getElementsByTagName('span')[0].id = '${kLogout}';
				aLogin.removeClass('fa-sign-in');
				aLogin.addClass('fa-sign-out');
			}
		}
	})
	.controller('profileController', 
		function ($scope, translate, hsFactory, $http){
			translate.run(function(t){t.translateAll();});

			hsFactory.getUserProfileInfo(function(data){
				if(data.loginBad == undefined){
					$('input[name="name"').val(data.name);
					$('input[name="middleName"').val(data.middleName);
					$('input[name="surName"').val(data.surName);
					$('input[name="email"').val(data.email);
					$('input[name="phone"').val(data.phone);
					if(data.owners){
						var listOwners = [];
						$('.listContent').removeClass('isHide');
						data.owners.forEach(function(meter){
								var id = meter.slice(0, meter.indexOf('_')),
										name = meter.slice(meter.indexOf('_') + 1),
										listObject = Object.create(null);
								listObject.id = Number(id);
								listObject.idMeter = name.slice(0, name.indexOf('_'));
								listObject.name = name.slice(name.indexOf('_') + 1);
								if(data.actowners.indexOf(listObject.id) > -1){
									listObject.selected = true;
								}
								listOwners.push(listObject);
							});
							$scope.owners = listOwners;
					}
				}
			});

			$scope.submitProfile = function(isButton){
				var isValid = true;
				$('#profileInfo form input').each(function(){
					if(!this.checkValidity()){
						isValid = false;
						if(isButton){
							setTimeout(function(){
								$('#btnHide').click()
								$('#error').html('Set correct form\'s data');
							},100);
						}
						return;
					}
				});
				if(isValid){
					var countOwners = $scope.owners.filter(function(owner){
						return owner.selected;
					}).length,
					pwd1 = $('#profileInfo input[name="pwd1"').val(),
					pwd2 = $('#profileInfo input[name="pwd2"').val();

					if(countOwners == 0){
						$('#error').html('Not selected owners ...');
					} else if(pwd1 != pwd2){
						$('#error').html('New passwords are different!');
					} else {
						updateProfile(function(data){
							if(data.messageId != 3)
								$('#error').html(data.message);
							else
								window.location.href = hsFactory.url + 'main.html';
						});
					}
				}
			}

			$scope.removeProfile = function(){
				var deleteProfile = true;
				updateProfile(function(data){
					if(data.messageId != 4)
						$('#error').html(data.message);
					else
						window.location.href = hsFactory.url + 'index.html';
				}, deleteProfile);
			}

			function updateProfile(callback, remove){
				var ownersId = '',
						deleteProfile = remove == undefined ? false : remove;
				$scope.owners.forEach(function(owner){
					if(owner.selected){
						ownersId += owner.id + ';';
					}
				});
				$http({
					method: 'POST',
					url: '/HeatSupply/ProfileServlet',
					params: {
						remove: deleteProfile,
						idUser: hsFactory.userId,
						owners: ownersId,
						name: $('#profileInfo input[name="name"').val(),
						middleName: $('#profileInfo input[name="middleName"').val(),
						surName: $('#profileInfo input[name="surName"').val(),
						email: $('#profileInfo input[name="email"').val(),
						phone: $('#profileInfo input[name="phone"').val(),
						password: $('#profileInfo input[name="pwd"').val(),
						password1: $('#profileInfo input[name="pwd1"').val(),
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
		});
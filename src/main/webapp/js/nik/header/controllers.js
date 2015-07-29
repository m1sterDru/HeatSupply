var heatSupply = Object.create(null);
heatSupply.headerControllers = angular.module('headerControllers', [
	'ngRoute',
	'headerFactory']);

heatSupply.headerControllers.config(function ($routeProvider){
	$routeProvider.
		when('/profile', {
			templateUrl: 'html/templates/profileTemplate.html',
			controller: 'profileController'
		}).
		otherwise({
			redirectTo: '/'
		})
}).
run(function ($rootScope, $location, hsFactory){
	$rootScope.$on("$routeChangeStart", function (event, next, current){
		if($rootScope.loggedInUser == null){
			if(next.templateUrl === "html/templates/profileTemplate.html"){
				hsFactory.getUserProfileInfo(function(data){
					if(data.user !== 'undefined' && data.user.length == 0){
						location.href = hsFactory.url + 'main.html';
					}
				});
			}
		}
	});
});

heatSupply.headerControllers.controller('headerController', 
	function ($scope, translate, hsFactory){
		hsFactory.getUserProfile(function(){
			checkIsLogin();
		});
		changeLocale(hsFactory.language);

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
		function ($scope, translate, hsFactory){
			translate.run(function(t){t.translateAll();});

			hsFactory.getUserProfileInfo(function(data){
				if(data.user !== 'undefined' && data.user.length > 0){
					$('input[name="user"').val(
						data.user.slice(0, data.user.indexOf('_')));
					$('input[name="email"').val(data.email);
				}
			});
		});
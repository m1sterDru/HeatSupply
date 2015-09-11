heatSupply.indexModule = angular.module('indexModule', [
	'ngRoute',
	'headerControllers',
	'indexControllers']);

heatSupply.indexModule.config(function ($routeProvider){
	$routeProvider.
		when('/', {
			templateUrl: 'html/index/mainForm.html',
			controller:'mainIndexController'
		}).
		when('/login', {
			templateUrl: 'html/login/loginForm.html'
		}).
		when('/registration', {
			templateUrl: 'html/login/loginRegistration.html',
			controller:'registrationController'
		}).
		otherwise({
			redirectTo: '/'
		})
});

heatSupply.indexModule
	.directive('loginForm', function (translate){
		return {
			templateUrl:'./html/directives/loginTemplate.html',
			link: function(scope, elm, attrs, ctrl){
				var ngElement = $('div[data-template="langDirective"]'), scope;
					if(ngElement.length > 0){
						scope = angular.element(ngElement).scope();
						scope.login_userSpanClass = 'isHide';
					}

				translate.run(function(t){
					t.translateAll();
				});
			}
		}
	})
	.directive('registrationForm', function (translate){
		return {
			templateUrl:'./html/directives/registrationTemplate.html',
			link: function(scope, elm, attrs, ctrl){
				var ngElement = $('div[data-template="langDirective"]'), scope;
					if(ngElement.length > 0){
						scope = angular.element(ngElement).scope();
						scope.login_userSpanClass = 'isHide';
					}

				translate.run(function(t){t.translateAll();});
			}
		}
	});
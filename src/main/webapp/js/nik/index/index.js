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
		// when('/registration', {
		// 	templateUrl: 'html/login/loginRegistration.html',
		// 	controller:'registrationController'
		// }).
		otherwise({
			redirectTo: '/'
		})
});

heatSupply.indexModule
	.directive('loginForm', function (translate, hsFactory){
		return {
			templateUrl:'./html/directives/loginTemplate.html',
			link: function(scope, elm, attrs, ctrl){
				var ngElement = $('div[data-template="langDirective"]'), span;

				span = ngElement.children('span').eq(0);
				span.addClass('isHide');
				$('input[name="user"]').val(hsFactory.login);
				$('input[name="pwd"]')[0].focus();

				translate.run(function(t){
					t.translateAll();
				});
			},
			controller: 'loginController'
		}
	})
	.directive('registrationForm', function (translate){
		return {
			templateUrl:'./html/directives/registrationTemplate.html',
			link: function(scope, elm, attrs, ctrl){
				translate.run(function(t){t.translateAll();});
			}
		}
	});
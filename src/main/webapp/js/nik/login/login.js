heatSupply.loginModule = angular.module('loginModule', [
	'ngRoute',
	'headerControllers',
	'loginControllers'
	]);

heatSupply.loginModule.config(function ($routeProvider){
	$routeProvider.
		when('/', {
			templateUrl: 'html/login/loginForm.html',
			controller:'loginFormController'
		}).
		when('/registration', {
			templateUrl: 'html/login/loginRegistration.html',
			controller:'loginRegistrationController'
		}).
		otherwise({
			redirectTo: '/'
		})
});
var heatsupply = angular.module('heatsupply', [
	'ngRoute',
	'heatsupplyControllers'
	]);

heatsupply.config(function ($routeProvider){
	$routeProvider.
		when('/', {
			templateUrl: 'html/login/loginForm.html',
			controller:'LoginCtrl'
		}).
		when('/registration', {
			templateUrl: 'html/login/loginRegistration.html',
			controller:'RegisterCtrl'
		}).
		otherwise({
			redirectTo: '/'
		})
});

heatsupply.nik = Object.create(null);
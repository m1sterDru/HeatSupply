heatSupply.mainModule = angular.module('main', [
	'ngRoute',
	'headerControllers',
	'mainFactory',
	'mainControllers'
	]);

heatSupply.mainModule.config(function ($routeProvider){
	$routeProvider.
		when('/', {
			templateUrl: 'html/main/mainForm.html',
			controller:'mainController'
		}).
		when('/profile', {
			templateUrl: 'html/templates/profileTemplate.html',
			controller:'profileController'
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
}).run(function ($rootScope, $location, hsFactory){
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
});;
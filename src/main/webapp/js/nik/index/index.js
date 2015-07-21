heatSupply.indexModule = angular.module('indexModule', [
	'ngRoute',
	'headerControllers',
	'indexControllers']);

heatSupply.indexModule.config(function ($routeProvider){
	$routeProvider.
		when('/', {
			templateUrl: 'html/index/mainForm.html',
			controller:'mainFormController'
		}).
		otherwise({
			redirectTo: '/'
		})
});
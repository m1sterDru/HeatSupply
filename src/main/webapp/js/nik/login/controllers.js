heatSupply.loginControllers = angular.module('loginControllers', []);

heatSupply.loginControllers
	.controller('LoginController', function ($scope, translate){
		$scope.translate = function(){
			translate.run(function(t){t.translateAll();});
		}
	});
heatSupply.mainControllers = angular.module('mainControllers', []);

heatSupply.mainControllers.controller('mainController', 
	function ($scope){
		$scope.onHeaderLoad = function(){
			heatSupply.initWebSocket();
		}
	});
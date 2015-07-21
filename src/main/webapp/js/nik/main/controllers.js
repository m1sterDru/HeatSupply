heatSupply.mainControllers = angular.module('mainControllers', []);

heatSupply.mainControllers.controller('mainController', 
	function ($scope){
		// heatSupply.initWebSocket();
		$scope.onHeaderLoad = function(){
			heatSupply.initWebSocket();
		}
	});
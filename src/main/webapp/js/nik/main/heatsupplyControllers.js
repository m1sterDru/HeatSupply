var heatsupplyControllers = angular.module('heatsupplyControllers', []);

heatsupplyControllers.controller('HeatSupplyCtrl', 
	function ($scope){
		$scope.onHeaderLoad = function(){
			heatSupply.initWebSocket();
		}
	});

heatsupplyControllers.controller('LoginCtrl', 
	function ($scope, priorities){
		heatsupplyControllers.Translator.translateAll();

		priorities.list(function (data){
			$scope.priorities = data;
		});
	});

heatsupplyControllers.controller('RegisterCtrl', 
	function ($scope, $routeParams, priorities){
		heatsupplyControllers.Translator.translateAll();

		$scope.change = function(){
			var noPay = document.getElementById('noPay').checked;
			console.log(noPay + ' = ' + heatsupply.nik.url)
		};

		priorities.find($routeParams.value, function (priority){
			$scope.priority = priority;
		});
	});
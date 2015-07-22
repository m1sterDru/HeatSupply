heatSupply.mainControllers = angular.module('mainControllers', [
	'headerFactory']);

heatSupply.mainControllers
	.controller('mainController', function ($scope, hsFactory){
		heatSupply.initWebSocket(hsFactory.url);
	})
	.controller('mainFormController', function ($scope){
		$scope.clickTab = function($event){
			var curLi = $event.target.parentNode, ref = curLi.id;

			$($event.target).tab('show');
			if(ref){
				ref = ref.slice(ref.indexOf('_') + 1);
				$('#tabsExample .tab-pane.active').first().removeClass('active');
				$('#' + ref).addClass('active');
			}
		}
	});
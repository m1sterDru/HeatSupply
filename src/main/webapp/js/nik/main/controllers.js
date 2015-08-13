heatSupply.mainControllers = angular.module('mainControllers', [
	'headerFactory']);

heatSupply.mainControllers
	.controller('mainController', 
		function ($scope, translate, hsFactory, $location){
		$scope.$on('$viewContentLoaded', function(){
			translate.run(function(t){
				t.translateAllByLocaleName(hsFactory.language);
			});
		});
		$location.path('/');

		$scope.menuClick = function($event){
			var li = $event.target;
			while(li.tagName !== 'LI') li = li.parentNode;
			$('#reportContent').css({
				'max-height': ($(window).height() - 115) + 'px',
				'overflow': 'auto'
			});
			heatSupply.currentReport = li.id;
			heatSupply.socket.send(JSON.stringify({
				'type': 'CommandMessage', 'command': 'getReport',
				'parameters': [{'reportName' : li.id}]
			}));
		}

		$scope.saveAs = function($event){
			var btn = $event.target;
			if(btn){
				heatSupply.currentReportExt = btn.id.slice(1);
				heatSupply.socket.send(JSON.stringify({
					'type': 'CommandMessage', 'command': 'saveReport',
					'parameters': [
						{'reportName': heatSupply.currentReport},
						{'ext': btn.id.slice(1)}]
				}));
			}
		}

		heatSupply.initWebSocket(hsFactory.url);
	});
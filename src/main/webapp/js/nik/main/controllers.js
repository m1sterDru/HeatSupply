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
		$scope.clickTab = function($event){
			var curLi = $event.target, ref;

			while(curLi.tagName !== 'LI') curLi = curLi.parentNode;
			ref = curLi.id
			$($event.target).tab('show');
			if(ref){
				ref = ref.slice(ref.indexOf('_') + 1);
				$('#tabsExample .tab-pane.active').first().removeClass('active');
				$('#' + ref).addClass('active');
			}
			$('#tabsExample .tab-pane.active ul li').each(function(){
				$(this).removeClass('active');
			});
		}

		$scope.setActive = function($event){
			var curLi = $event.target, ref;

			while(curLi.tagName !== 'LI') curLi = curLi.parentNode;
			$(curLi.parentNode).children('li').each(function(){
				$(this).removeClass('active');
			});
			$(curLi).addClass('active');
		}

		$scope.mTovarClick = function($event){
			var id = $event.target.parentNode.parentNode.id;
			$scope.menu = $event.target.innerHTML;
		}

		heatSupply.initWebSocket(hsFactory.url);
	});
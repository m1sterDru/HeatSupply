heatSupply.indexCtrls = angular.module('indexControllers',[]);

heatSupply.indexCtrls.controller('indexController', 
	function ($scope, translate){
		translate.run(function(t){t.translateAll();});
	});

heatSupply.indexCtrls.controller('mainFormController', 
	function ($scope, translate){
		translate.run(function(t){t.translateAll();});

		$scope.clickTab = function($event){
			var curLi = $event.target.parentNode,
					ref = curLi.id;

			$('li.active').removeClass('active');
			$(curLi).addClass('active');

			if(ref){
				ref = ref.slice(ref.indexOf('_') + 1);
				$('.tab-pane.active').first().removeClass('active');
				$('#' + ref).addClass('active');
			}
		}
	});
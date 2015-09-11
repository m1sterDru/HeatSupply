heatSupply.headerControllers
	.directive('langDirective', function (){
		return {
			templateUrl: './html/directives/langDirective.html',
			controller: 'translateController'
		}
	});

heatSupply.headerControllers.controller('translateController', 
	function ($scope, translate, hsFactory){
		$scope.changeLanguage = function($event){
			var btn = $('button[data-btn="curLangButton"]:first')[0],
					el = $event.target;

			if(el.tagName.toLowerCase() !== 'li') el = el.parentNode;
			$scope.changeLocale(el.id);
		}
	});
heatSupply.headerControllers
	.directive('langDirective', function (){
		return {
			templateUrl: './html/directives/langDirective.html',
			controller: 'translateController',
			link: function(scope, elm, attrs, ctrl){
				if(location.href.indexOf('/main.') < 0){
					var room = elm.find('span').find('a').eq(1);
					room.append('<span id="${keyRoom}"></span>');
				}
			}
		}
	})
	.directive('errorDirective', function (){
		return {
			templateUrl: './html/directives/errorDirective.html',
			link: function(scope, elm, attrs, ctrl){
				var aClose = elm.find('a')[0];
				aClose.addEventListener("click", function(){
					elm.find('div').eq(0).addClass('isHide');
				});
			}
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
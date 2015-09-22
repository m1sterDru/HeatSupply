heatSupply.headerControllers
	.directive('langDirective', function (){
		return {
			templateUrl: './html/directives/langDirective.html',
			controller: 'langController',
			link: function(scope, elm, attrs, ctrl){
				if(location.href.indexOf('/main.') < 0){
					var room = $(elm).find('.room');
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

heatSupply.headerControllers.controller('langController', 
	function ($scope, $element, translate, hsFactory){
		$scope.changeLanguage = function($event){
			var el = $event.target;

			if(el.tagName.toLowerCase() !== 'li') el = el.parentNode;
			$scope.changeLocale(el.id);
		}
		$scope.login_userLink = 'main.html';
	});
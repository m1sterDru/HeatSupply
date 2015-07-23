heatSupply.indexControllers = angular.module('indexControllers',[]);

heatSupply.indexControllers
	.controller('indexController', 
		function ($scope, translate){
			$scope.$on('$viewContentLoaded', function(){
				translate.run(function(t){t.translateAll();});
			});
		})
	.controller('mainFormController', 
		function ($scope, $controller){
			var headerController = $scope.$new();
			$controller('headerController',{$scope : headerController});
			headerController.test123();

			$('#carouselTest').carousel({interval: 3000})

			$scope.clickTab = function($event){
				var curLi = $event.target.parentNode,
						ref = curLi.id;

				$($event.target).tab('show');

				if(ref){
					ref = ref.slice(ref.indexOf('_') + 1);
					$('#tabsExample .tab-pane.active').first().removeClass('active');
					$('#' + ref).addClass('active');
				}
			}

			$scope.prevCarousel = function(){
				$('#carouselTest').carousel('prev');
			}

			$scope.nextCarousel = function(){
				$('#carouselTest').carousel('next');
			}
		});
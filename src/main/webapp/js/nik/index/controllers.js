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

			$('#tabsExample li.active').removeClass('active');
			$(curLi).addClass('active');

			if(ref){
				ref = ref.slice(ref.indexOf('_') + 1);
				$('#tabsExample .tab-pane.active').first().removeClass('active');
				$('#' + ref).addClass('active');
			}
		}

		$scope.prevCarousel = function(){
			carousel(true);
		}

		$scope.nextCarousel = function(){
			carousel(false);
		}

		function carousel(isPrev){
			var actInd = $('#carouselTest ol li.active'),
					actId = Number(actInd.attr('data-slide-to')),
					count = $('#carouselTest ol li').length,
					inner = $('#carouselTest .carousel-inner .item'),
					actInner = $('#carouselTest .carousel-inner .active'),
					id;

			if(isPrev)
				id = actId - 1 < 0 ? count - 1 : actId - 1;
			else
				id = actId + 1 == count ? 0 : actId + 1;

			actInd.removeClass('active');
			$('#carouselTest ol li[data-slide-to="' + id + '"]').
				addClass('active');

			actInner.removeClass('active');
			$(inner.get(id)).addClass('active');
		}
	});
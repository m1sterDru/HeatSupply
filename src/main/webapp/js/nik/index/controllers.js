heatSupply.indexControllers = angular.module('indexControllers',[]);

heatSupply.indexControllers
	.controller('indexController', function ($scope, translate){
		$scope.$on('$viewContentLoaded', function(){
			translate.run(function(t){t.translateAll();});
		});
	})
	.controller('mainIndexController', function ($scope){
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
	})
	.controller('loginController', function ($scope, $http, hsFactory){
		$scope.sendLogin = function(isButton){
			var isValid = true;

			$('.loginDiv form input').each(function(){
				if(!this.checkValidity()){
					isValid = false;
					hsFactory.updateError('keyBadAuthentication');
					if(isButton){
						setTimeout(function(){
							$('#btnHide').click();
						},100);
					}
					return;
				}
			});
			if(isValid){
				$http({
					method: 'POST',
					url: '/HeatSupply/LoginServlet',
					params: {
						user: $('.loginDiv input[name="user"]').val(),
						pwd: $('.loginDiv input[name="pwd"]').val()
					},
					cache: false
				})
				.success(function(data){
					if(data.messageId != 0){
						hsFactory.updateError(data.message);
					} else location.href = hsFactory.url + 'main.html';
				})
				.error(function(data, status, headers, config){
					console.log(status)
				});
			}
		}
	})
	.controller('mainRoomController', function ($scope){
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
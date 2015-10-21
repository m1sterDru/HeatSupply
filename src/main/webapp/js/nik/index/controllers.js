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
	})
	.controller('recoverController', function ($scope, $http, hsFactory){
		$scope.userData = hsFactory.login;
		$('#recover input[name="userOrEmail"]')[0].focus();

		$scope.sendMail = function($event){
			var email = $('#recover input[name="userOrEmail"]'),
					spanIcon = $('#recover button span:first');

			if(email[0].checkValidity()){
				spanIcon.removeClass('glyphicon glyphicon-send');
				spanIcon.addClass('fa fa-refresh fa-spin');
				$http({
					method: 'GET',
					url: '/HeatSupply/dataServer/db/recover?params=' +
								$scope.userData,
					cache: false
				})
				.success(function(data){
					var isOK = data.result === 'success';
					console.log(data)
					console.log(data.result)
					BootstrapDialog.confirm({
						title: isOK ? 'SUCCESS' : 'WARNING',
						message: isOK ? 'Check your email' : 'Something wrong',
						type: isOK ? BootstrapDialog.TYPE_SUCCESS :
												 BootstrapDialog.TYPE_DANGER,
						closable: false,
						draggable: false,
						btnCancelLabel: 'Cancel',
						btnOKLabel: 'OK',
						btnOKClass: isOK ? 'btn-primary' : 'btn-danger',
						callback: function(result){
							if(result && isOK){
								location.href = hsFactory.url + '#login'
							} else {
								spanIcon.addClass('glyphicon glyphicon-send');
								spanIcon.removeClass('fa fa-refresh fa-spin');
							}
						}
					})
				})
				.error(function(data, status, headers, config){
					console.log(status)
				});
			}
		}
	});
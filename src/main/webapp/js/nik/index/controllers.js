heatSupply.indexControllers = angular.module('indexControllers',[]);

heatSupply.indexControllers
	.controller('indexController', 
		function ($scope, translate){
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
	.controller('registrationController', function ($scope, $http, hsFactory){
		$scope.noPay = true;

		$scope.regFocus = function($event){
			var item = $($event.target),
					error = $('.comment'),
					contentError, key;
			error.removeClass('isHide');
			contentError = $('<span id="' + item.attr('comment') +
				'">sss</span>');
			key = item.attr('comment').slice('2');
			key = key.slice(0, key.length - 1);

			hsFactory.translator.translateValueByKey(hsFactory.language, key,
				function(value){
					contentError.html(value);
					error.html(contentError);
					error.css('margin-top','15px');
				});
		}

		$scope.submitRegistration = function(isButton){
			var isValid = true;
			$('.comment').html('');
			$('#regDiv1 input').each(function(){
				if(!this.checkValidity()){
					isValid = false;
					$('.comment').html('Set correct form\'s data');
					if(isButton){
						setTimeout(function(){
							$('#btnHide1').click();
						},100);
					}
					return;
				}
			});

			if(isValid){
				var step = 1;
				registration(step, function(data){
					console.log(data);
					console.log(step);
					if(data.messageId == 0){
						$('#regDiv1').addClass('isHide');
						$('#regDiv2').removeClass('isHide');
						$('.comment').addClass('isHide');
						data.array.forEach(function(data){
							var key = data.slice(0, data.indexOf('_')),
									value = data.slice(data.indexOf('_') + 1);
							switch(key){
								case 'id': $scope.meterId = value; break;
								case 'name':
									$('#regDiv2 input[name="name"]').val(value);
									break;
							}
						});
					} else {
						$('.comment').html(data.message);
					}
				});
			}
		}

		$scope.submitRegistrationLast = function(isButton){
			var isValid = true, error = $('.comment');
					password = $('#regDiv2 input[name="password"]').val(),
					password2 = $('#regDiv2 input[name="password2"]').val();

			error.html('');
			if(password != password2){
				error.html('Input passwords are different.');
			} else {
				$('#regDiv2 input').each(function(){
					if(!this.checkValidity()){
						isValid = false;
						$('.comment').html('Set correct form\'s data');
						if(isButton){
							setTimeout(function(){
								$('#btnHide2').click();
							},100);
						}
						return;
					}
				});
				if(isValid){
					registration(2, function(data){
						if(data.messageId != 0)
							error.html(data.message);
						else
							window.location.href = hsFactory.url + 'main.html';
					});
				}
			}
		}

		function registration(step, callback){
			$http({
				method: 'POST',
				url: '/HeatSupply/RegisterServlet',
				params: {
					step: step,
					noPay: $scope.noPay,
					owneraccount: $('#regDiv1 input[name="owneraccount"').val(),
					meterNumber: $('#regDiv1 input[name="meterNumber"').val(),
					meterId: $scope.meterId,
					password: $('#regDiv2 input[name="password"]').val(),
					login: $('#regDiv2 input[name="login"]').val(),
					name: $('#regDiv2 input[name="name"]').val(),
					middleName: $('#regDiv2 input[name="middleName"]').val(),
					surName: $('#regDiv2 input[name="surName"]').val(),
					phone: $('#regDiv2 input[name="phone"]').val(),
					email: $('#regDiv2 input[name="email"]').val(),
					languageId: hsFactory.language
				},
				cache: false
			})
			.success(function(data){
				if(callback != null) callback(data);
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		}
	});
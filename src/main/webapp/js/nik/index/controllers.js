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
		$scope.owners = [];

		$scope.submitRegistration = function(isButton){
			var isValid = true;
			$('#error').html('');
			$('#regDiv1 input').each(function(){
				if(!this.checkValidity()){
					isValid = false;
					if(isButton){
						setTimeout(function(){
							$('#btnHide1').click()
							$('#error').html('Set correct form\'s data');
						},100);
					}
					return;
				}
			});

			if(isValid){
				var step = $('.listContent').hasClass('isHide') ? 1 : 2;
				registration(step, function(data){
					console.log(data);
					if(data.messageId == 6){
						$('#regDiv1').addClass('isHide');
						$('#regDiv2').removeClass('isHide');
					} else {
						if(data.array){
							var listOwners = [];
							data.array.forEach(function(meter){
								var id = meter.slice(0, meter.indexOf('_')),
										name = meter.slice(meter.indexOf('_') + 1),
										listObject = Object.create(null);
								listObject.id = id;
								listObject.idMeter = name.slice(0, name.indexOf('_'));
								listObject.name = name.slice(name.indexOf('_') + 1);
								listOwners.push(listObject);
							});
							$scope.owners = listOwners;
							$('.listContent').removeClass('isHide');

						} else {
							$('#error').html(data.message);
						}
					}
				});
			}
		}

		$scope.submitRegistrationLast = function(isButton){
			var isValid = true;
			$('#error').html('');
			if(isValid){
				$('#regDiv2 input:first').focus();
				$('#regDiv2 input').each(function(){
					if(!this.checkValidity()){
						isValid = false;
						if(isButton){
							setTimeout(function(){
								$('#btnHide2').click()
								$('#error').html('Set correct form\'s data');
							},100);
						}
						return;
					}
				});
			}
			if(isValid){
				registration(3, function(data){
					if(data.messageId != 3)
						$('#error').html(data.message);
					else
						window.location.href = hsFactory.url + 'main.html';
				});
			}
		}

		function registration(step, callback){
			var ownersId = '';
			$scope.owners.forEach(function(owner){
				if(owner.selected){
					ownersId += owner.id + ';';
				}
			});
			$http({
				method: 'POST',
				url: '/HeatSupply/RegisterServlet',
				params: {
					step: step,
					noPay: $scope.noPay,
					owners: ownersId,
					owneraccount: $('#regDiv1 input[name="owneraccount"').val(),
					lastcash: $('#regDiv1 input[name="lastcash"').val(),
					password: $('#regDiv2 input[name="password"').val(),
					login: $('#regDiv2 input[name="login"').val(),
					name: $('#regDiv2 input[name="name"').val(),
					middleName: $('#regDiv2 input[name="middleName"').val(),
					surName: $('#regDiv2 input[name="surName"').val(),
					phone: $('#regDiv2 input[name="phone"').val(),
					email: $('#regDiv2 input[name="email"').val(),
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
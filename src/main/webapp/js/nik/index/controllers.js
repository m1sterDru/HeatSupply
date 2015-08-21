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
		$scope.submitRegistration = function (isButton){
			var isValid = true;
			$('#error').html('');
			$('#regDiv1 input').each(function(){
				if(!this.checkValidity()){
					isValid = false;
					if(isButton){
						$('#regDiv1 input:first').focus();
						$('#error').html('Set correct form data');
					}
					return;
				}
			});
			if(isValid){
				$('#regDiv2 input:first').focus();
				$('#regDiv2 input').each(function(){
					if(!this.checkValidity()){
						isValid = false;
						if(isButton){
							$('#regDiv2 input:first').focus();
							$('#error').html('Set correct form data');
						}
						return;
					}
				});
			}
			if(isValid){
				console.log('VALID');
				registration(function(data){
					console.log(data);
				});
				// hsFactory.updateProfile(function(data){
				// 	if(data.messageId != 3)
				// 		$('#error').html(data.message);
				// 	else
				// 		window.location.href = hsFactory.url + 'main.html';
				// });
			}
		}

		function registration(callback){
			$http({
				method: 'POST',
				url: '/HeatSupply/RegisterServlet',
				params: {
					accountNumber: $('#regDiv1 input[name="accountNumber"').val(),
					lastSum: $('#regDiv1 input[name="lastSum"').val(),
					meterNumber: $('#regDiv1 input[name="meterNumber"').val(),
					password: $('#regDiv1 input[name="password"').val(),
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
		};
	});
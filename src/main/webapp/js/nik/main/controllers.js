heatSupply.mainControllers = angular.module('mainControllers', [
	'ngRoute',
	'headerFactory']);

heatSupply.mainControllers
	.controller('menuController', 
		function ($scope, hsFactory, $location){
		$scope.$on('$viewContentLoaded', function(){
			hsFactory.translator.translateAll();
		});
		// $location.path('/');

		$scope.login_userLink = '#/profile';

		hsFactory.getUserProfile(function(data){
			var message = Object.create(null);

			$scope.login = data.user;
			message.type = 'CommandMessage';
			message.command = 'ownerList';
			message.parameters = [
				{'userId': data.userId},
				{'selector': "div[data-template='profileDirective']"}
			];
			heatSupply.socket.send(JSON.stringify(message));
		});

		$scope.menuClick = function($event){
			var li = $event.target;
			while(li.tagName !== 'LI') li = li.parentNode;
			heatSupply.currentReport = li.id;
			heatSupply.socket.send(JSON.stringify({
				'type': 'CommandMessage', 'command': 'getReport',
				'parameters': [{'reportName' : li.id}]
			}));
		}

		$scope.saveAs = function($event){
			var btn = $event.target;
			if(btn){
				heatSupply.currentReportExt = btn.id.slice(1);
				heatSupply.socket.send(JSON.stringify({
					'type': 'CommandMessage', 'command': 'saveReport',
					'parameters': [
						{'reportName': heatSupply.currentReport},
						{'ext': btn.id.slice(1)}]
				}));
			}
		}
	})
	.controller('profileController', function ($scope, hsFactory, $http){
		var userId;

		hsFactory.getUserProfile(function(data){
			var message = Object.create(null);

			message.type = 'CommandMessage';
			message.command = 'profileInfo';
			message.parameters = [{'userId': data.userId}];
			heatSupply.socket.send(JSON.stringify(message));
			$scope.isDisabled = true;
			$scope.formStyle = {opacity: 0.5};
			userId = data.userId;
			hsFactory.translator.translateAll();
		});

		$scope.valid = function($event){
			if($event.target.value.length == 0){
				$event.target.value = '+';
				setTimeout(function(){
					$('#btnHide').click();
					$event.target.value = '';
				}, 10);
			}
		}

		$scope.submitProfile = function(isButton){
			var isValid = true;

			$('#profileInfo form input').each(function(){
				if(!this.checkValidity()){
					isValid = false;
					if($(this)[0].name === 'email'){
						hsFactory.updateError('keyWrongEmail');
					} else if($(this)[0].name === 'pwd'){
						hsFactory.updateError('keyWrongPassword');
					}
					if(isButton){
						setTimeout(function(){
							$('#btnHide').click();
						},100);
					}
					return false;
				}
			});
			if(isValid){
				var isUpdate = true,
						pwd1 = $('#profileInfo input[name="pwd1"]').val(),
						pwd2 = $('#profileInfo input[name="pwd2"]').val();

				if(pwd1 != pwd2){
					hsFactory.updateError('keyNewPasswordsWrong');
				} else {
					if(pwd1.length > 0 && pwd1.length < 6){
						hsFactory.updateError('keyNewPasswordNotValid');
					} else {
						updateProfile(function(data){
							if(data.messageId != 0)
								hsFactory.updateError(data.message);
							else
								window.location.href = hsFactory.url + 'main.html';
						});
					}
				}
			}
		}

		function updateProfile(callback){
			$http({
				method: 'POST',
				url: '/HeatSupply/ProfileServlet',
				params: {
					idUser: userId,
					email: $('#profileInfo input[name="email"]').val(),
					phone: $('#profileInfo input[name="phone"]').val(),
					password: $('#profileInfo input[name="pwd"]').val(),
					password1: $('#profileInfo input[name="pwd1"]').val(),
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
	})
.controller('accountController', function ($scope,hsFactory){
	setTimeout(function(){
		hsFactory.translator.translateAll();
	}, 1000);
})
.controller('ownerAccountDeleteController', function ($scope, hsFactory){
	$scope.logoClass = 'isHide';
	setTimeout(function(){$scope.logoClass = '';}, 500);

	function disable(isOn){
		var ico = $('.btn-danger span:first-child');
		$('#delAccountTemplate select').disabled = isOn;
		$('#delAccountTemplate button').disabled = isOn;
		$('#delAccountTemplate').css('opacity', isOn ? '0.5' : '1');
		if(isOn){
			ico.removeClass('fa-remove');
			ico.addClass('fa-refresh fa-spin');
		} else {
			ico.removeClass('fa-refresh fa-spin');
			ico.addClass('fa-remove');
		}
	}

	$scope.change4delete = function($event){
		var li = $event.target;
		while(li.tagName != 'LI') li = li.parentNode;
		$scope.account4delete.name = li.getAttribute('ownerName');
		$scope.account4delete.idMeter = li.getAttribute('idMeter');
		$scope.account4delete.sn = li.getAttribute('snMeter');
		$scope.account4delete.ownerAccount = li.getAttribute('ownerAccount');
	}

	$scope.deleteOwnerAccount = function(){
		hsFactory.bootstrapConfirm(function(data){
			var owneraccount = $scope.account4delete.ownerAccount,
					idMeter = $scope.account4delete.idMeter,
					message = Object.create(null);

			$scope.actowners = [];
			message.type = 'CommandMessage';
			message.command = 'deleteOwner';
			message.parameters = [
				{'userId': data.userId},
				{'idMeter': idMeter}
			];
			heatSupply.socket.send(JSON.stringify(message));
			disable(false);
		});
	}
})
.controller('deactivationController', function ($scope, hsFactory, translate){
	var index = 0;

	$scope.deactives = [];
	translate.run(function(t){
		t.translateValueByKey(hsFactory.language,
			['kProfileDeleteLi1','kProfileDeleteLi2','kProfileDeleteLi3',
			 'kProfileDeleteLi4','kProfileDeleteLi5'],
			function(value, key){
				$scope.deactives.push({id: index++,
															 text: value,
															 key: '${' + key + '}'});
				if(index == 5){
					$scope.deactActive = Object.create(null);
					$scope.deactActive.id = $scope.deactives[0].id;
					$scope.deactActive.text = $scope.deactives[0].text;
					$scope.deactActive.key = $scope.deactives[0].key;
					setTimeout(function(){$scope.$apply();},50);
				}
			});
		});

	$scope.changeDeactive = function($event){
		var li = $event.target, span;
		while(li.tagName !== 'LI') li = li.parentNode;
		span = li.getElementsByTagName('span')[0];
		$scope.deactActive.id = li.id;
		$scope.deactActive.key = span.id;
		$scope.deactActive.text = span.innerHTML;
	}

	$scope.removeProfile = function(isButton){
		var isValid = true;
		$('#deactivation form input').each(function(){
			if(!this.checkValidity()){
				isValid = false;
				hsFactory.updateError('keyWrongPassword');
				if(isButton){
					setTimeout(function(){
						$('#btnHide').click();
					},100);
				}
				return false;
			}
		});
		if(isValid){
			hsFactory.getUserProfile(function(data){
				var message = Object.create(null);

				message.type = 'CommandMessage';
				message.command = 'removeProfile';
				message.parameters = [
					{'userId': data.userId},
					{'password': $('#deactivation form input[type="password"]').val()}];
				heatSupply.socket.send(JSON.stringify(message));
			});
		}
	}
});
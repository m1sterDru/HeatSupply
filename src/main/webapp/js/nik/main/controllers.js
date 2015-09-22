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

		$scope.submitProfile = function(isButton){
			var error = $('.comment:first'), isValid = true;

			error.parent().addClass('isHide');
			$('#profileInfo form input').each(function(){
				if(!this.checkValidity()){
					isValid = false;
					// updateError('keyWrongPassword', error);
					if($(this)[0].name === 'email'){
						updateError('keyWrongEmail', error);
					} else if($(this)[0].name === 'pwd'){
						updateError('keyWrongPassword', error);
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
					updateError('keyNewPasswordsWrong', error);
				} else {
					if(pwd1.length > 0 && pwd1.length < 6){
						updateError('keyNewPasswordNotValid', error);
					} else {
						updateProfile(function(data){
							if(data.messageId != 0)
								updateError(data.message, error);
							else
								window.location.href = hsFactory.url + 'main.html';
						});
					}
				}
			}
		}

		function updateError(key, error){
			hsFactory.translator.translateValueByKey(
				hsFactory.language, key, function(value){
					error.html(value);
					error[0].id = '${' + key + '}';
					error.parent().removeClass('isHide');
				});
		}

		$scope.removeProfile = function(){
			hsFactory.translator.translateValueByKey(hsFactory.language, 'keySure',
			function(value){
				BootstrapDialog.confirm({
					title: 'WARNING',
					message: value,
					type: BootstrapDialog.TYPE_DANGER,
					closable: false,
					draggable: false,
					btnCancelLabel: 'Cancel',
					btnOKLabel: 'OK',
					btnOKClass: 'btn-danger',
					callback: function(result){
						if(result) {
							hsFactory.getUserProfile(function (data){
								var message = Object.create(null);

								message.type = 'CommandMessage';
								message.command = 'removeProfile';
								message.parameters = [{'userId': data.userId}];
								heatSupply.socket.send(JSON.stringify(message));
							});
						}
					}
				});
			});
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
	.controller('ownerAccountController', function ($scope, hsFactory){
		var error = $('div[error-directive] .comment');

		if(!$('#delAccountTemplate').length){
			$scope.isDisabled = true;
			$scope.afterProducer = 'isHide';
			$('form').parent().parent().css('opacity','0.5');
		}

		$scope.selectProvider = function(type){
			$scope.producerType = type;
			$scope.isDisabled = type == 0 ? true : false;
			$('form').parent().parent().css(
				'opacity', type == 0 ? '0.5' : '1'
			);
			$scope.afterProducer = type == 0 ? 'isHide' : '';
			$scope.beforProducer = type == 0 ? '' : 'isHide';
			if(type != 0){
				$scope.provider1 = type == 1 ? '' : 'isHide';
				$scope.provider2 = type == 2 ? '' : 'isHide';
			} else {
				$scope.provider1 = '';
				$scope.provider2 = '';
			}
		}

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
			hsFactory.translator.translateValueByKey(hsFactory.language, 'keySure',
				function(value){
					if(confirm(value)){
						disable(true);
						hsFactory.getUserProfile(function(data){
							var owneraccount = $scope.account4delete.ownerAccount,
									idMeter = $scope.account4delete.idMeter,
									message = Object.create(null);

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
				});
		}

		$scope.submitAddOwner = function(isButton){
			hsFactory.getUserProfile(function(data){
				var isValid = true;
				error.html('');
				$('.regDiv input').each(function(){
					if(!this.checkValidity()){
						isValid = false;
						$('.comment').html('Set correct form\'s data');
						if(isButton){
							setTimeout(function(){
								$('#btnHide').click();
							},100);
						}
						return;
					}
				});

				if(isValid){
					var message = Object.create(null);
console.log($scope.producerType)
					message.type = 'CommandMessage';
					message.command = 'addOwner';
					message.parameters = [
						{'account': $('.regDiv input[name="owneraccount"').val()},
						{'number': $('.regDiv input[name="meterNumber"').val()},
						{'userId': data.userId},
						{'typeAccount': $scope.producerType + ''}
					];
					heatSupply.socket.send(JSON.stringify(message));
				}
			});
		}
	});
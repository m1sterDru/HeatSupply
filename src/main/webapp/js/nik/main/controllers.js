heatSupply.mainControllers = angular.module('mainControllers', [
	'ngRoute',
	'headerFactory']);

heatSupply.mainControllers.config(function ($routeProvider){
	$routeProvider.
		when('/profile', {
			templateUrl: function(){
				return 'html/templates/profileTemplate.html';
			},
			controller: 'profileController'
		}).
		when('/account', {
			templateUrl: function(){
				return 'html/templates/accountTemplate.html';
			},
			controller: 'accountController'
		}).
		when('/addOwner', {
			templateUrl: function(){
				return 'html/templates/addOwnerAccountTemplate.html';
			},
			controller: 'ownerAccountController'
		}).
		when('/delOwner', {
			templateUrl: function(){
				return 'html/templates/delOwnerAccountTemplate.html';
			},
			controller: 'ownerAccountController'
		})
});

heatSupply.mainControllers
	.controller('mainController', 
		function ($scope, hsFactory, $location){
		$scope.$on('$viewContentLoaded', function(){
			hsFactory.translator.translateAll();
		});
		$location.path('/');

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

		// heatSupply.initWebSocket(hsFactory.url);
	})
	.controller('profileController', function ($scope, hsFactory, $http){
		var error = $('.comment:first'),
				error2 = $('.comment:last'),
				userId;

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
			var isValid = true;
			$('#profileInfo form input').each(function(){
				if(!this.checkValidity()){
					isValid = false;
					error.html('Set correct form\'s data');
					if(isButton){
						setTimeout(function(){
							$('#btnHide').click();
						},100);
					}
					return;
				}
			});
			if(isValid){
				var isUpdate = true,
						pwd1 = $('#profileInfo input[name="pwd1"]').val(),
						pwd2 = $('#profileInfo input[name="pwd2"]').val();

				if(pwd1 != pwd2){
					error.html('New passwords are different ...');
				} else {
					updateProfile(function(data){
						if(data.messageId != 0)
							error.html(data.message);
						else
							window.location.href = hsFactory.url + 'main.html';
					});
				}
			}
		}

		$scope.removeProfile = function(){
			hsFactory.translator.translateValueByKey(hsFactory.language, 'keySure',
			function(value){
				if(confirm(value)){
					hsFactory.getUserProfile(function(data){
						var message = Object.create(null);

						message.type = 'CommandMessage';
						message.command = 'removeProfile';
						message.parameters = [{'userId': data.userId}];
						heatSupply.socket.send(JSON.stringify(message));
					});
				}
			});
		}

		function updateProfile(callback){
			$http({
				method: 'POST',
				url: '/HeatSupply/ProfileServlet',
				params: {
					idUser: userId,
					name: $('#profileInfo input[name="name"]').val(),
					middleName: $('#profileInfo input[name="middleName"]').val(),
					surName: $('#profileInfo input[name="surName"]').val(),
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
	.controller('accountController', function ($scope, hsFactory, $http){
		$scope.visibleClass = 'isHide';
		$scope.additionalClass = 'isHide';
		$scope.additionalIcon = 'fa-plus-square';

		hsFactory.getUserProfile(function(data){
			var message = Object.create(null);

			message.type = 'CommandMessage';
			message.command = 'ownerList';
			message.parameters = [
				{'userId': data.userId},
				{'elementId': 'accountTemplate'}
			];
			heatSupply.socket.send(JSON.stringify(message));
			$scope.login = data.user;
		});

		$scope.additional = function(){
			$scope.additionalClass = $scope.additionalClass === '' ?
				'isHide' : '';
			$scope.additionalIcon = $scope.additionalClass === '' ?
				'fa-minus-square' : 'fa-plus-square';
		}
	})
	.controller('ownerAccountController', function ($scope, hsFactory, $http){
		var error = $('.error');

		if($('#delAccountTemplate').length){
			$scope.isDisabled = true;
			hsFactory.getUserProfile(function(data){
				var message = Object.create(null);

				message.type = 'CommandMessage';
				message.command = 'ownerList';
				message.parameters = [
					{'userId': data.userId},
					{'elementId': 'delAccountTemplate'}
				];
				heatSupply.socket.send(JSON.stringify(message));
				$scope.login = data.user;
			});
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
			$scope.account4delete2.name = li.getAttribute('ownerName');
			$scope.account4delete = li.getAttribute('idMeter') + '_' +
				li.getAttribute('ownerAccount');
			$scope.account4delete2.sn = li.getAttribute('snMeter');
			$scope.account4delete2.ownerAccount = li.getAttribute('ownerAccount');
		}

		$scope.deleteOwnerAccount = function(){
			hsFactory.translator.translateValueByKey(hsFactory.language, 'keySure',
				function(value){
					if(confirm(value)){
						disable(true);
						hsFactory.getUserProfile(function(data){
							var owneraccount = $scope.account4delete
										.slice($scope.account4delete.indexOf('_') + 1),
									idMeter = $scope.account4delete
										.slice(0, $scope.account4delete.indexOf('_')),
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

					message.type = 'CommandMessage';
					message.command = 'addOwner';
					message.parameters = [
						{'account': $('.regDiv input[name="owneraccount"').val()},
						{'number': $('.regDiv input[name="meterNumber"').val()},
						{'userId': data.userId}
					];
					heatSupply.socket.send(JSON.stringify(message));
				}
			});
		}
	});
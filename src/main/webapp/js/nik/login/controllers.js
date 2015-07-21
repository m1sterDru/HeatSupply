heatSupply.loginCtrls = angular.module('loginControllers', []);

heatSupply.loginCtrls.controller('LoginController', 
	function ($scope, translate){
		$scope.$on('$viewContentLoaded', function(){
			translate.run(function(t){t.translateAll();});
		});
	});

heatSupply.loginCtrls.controller('loginFormController', 
	function ($scope, translate){
	});

heatSupply.loginCtrls.controller('loginRegistrationController', 
	function ($scope, translate){
		$scope.nik = Object.create(null);
		$scope.noPayClass = 'isHide';
		$scope.showNumberComment = 'isHide';
		$scope.showLastSumComment = 'isHide';

		$scope.changeNoPay = function(){
			$scope.noPayClass = $scope.noPay ? '' : 'isHide';
		};

		$scope.validInput = function($event){
			var ind = $event.target.id.slice($event.target.id.indexOf('_') + 1),
					count = document.getElementsByTagName('form')[0]
						.getElementsByTagName('input').length - 1;

			for(var i = 1; i < count; i++){
				$(document.getElementById('rfC_' + i)).removeClass('isHide');
				if(document.getElementById('rfI_' + i).value.length > 0 || i>=ind){
					$(document.getElementById('rfC_' + i)).addClass('isHide');
				}
			}
		}
	});
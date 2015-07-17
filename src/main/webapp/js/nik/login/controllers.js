heatSupply.loginCtrls = angular.module('loginControllers', []);

heatSupply.loginCtrls.controller('LoginController', 
	function ($scope){
		
	});

heatSupply.loginCtrls.controller('LoginCtrl', 
	function ($scope){
		// heatsupplyControllers.Translator.translateAll();
	});

heatSupply.loginCtrls.controller('RegisterCtrl', 
	function ($scope, $routeParams){
		$scope.nik = Object.create(null);
		// heatsupplyControllers.Translator.translateAll();
		$scope.noPayClass = 'isHide';
		$scope.showNumberComment = 'isHide';
		$scope.showLastSumComment = 'isHide';

		$scope.changeNoPay = function(){
			$scope.noPayClass = $scope.noPay ? '' : 'isHide';
		};

		$scope.validInput = function($event){
			var elName = $event.target.name.toLowerCase(),
					numberShow, lastSumShow;
			numberShow = elName !== 'number' && 
									($scope.nik.Number === undefined ||
									$scope.nik.Number.length == 0);
			$scope.showNumberComment = numberShow ? '' : 'isHide';
			lastSumShow = elName !== 'lastsum' &&
									($scope.nik.LastSum === undefined ||
									$scope.nik.LastSum.length == 0);
			$scope.showLastSumComment = lastSumShow ? '' : 'isHide';
		}
	});
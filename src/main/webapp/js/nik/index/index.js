heatSupply.indexModule = angular.module('indexModule', [
	'ngRoute',
	'headerControllers',
	'indexControllers']);

heatSupply.indexModule.config(function ($routeProvider){
	$routeProvider.
		when('/', {
			templateUrl: 'html/index/mainForm.html',
			controller:'mainIndexController'
		}).
		when('/login', {
			templateUrl: 'html/login/loginForm.html'
		}).
		when('/registration', {
			templateUrl: 'html/login/loginRegistration.html',
			controller:'registrationController'
		}).
		otherwise({
			redirectTo: '/'
		})
});

heatSupply.indexModule
	.directive('loginForm', function (translate){
		return {
			templateUrl:"./html/templates/loginTemplate.html",
			link: function(scope, elm, attrs, ctrl){
				var currentUser = $('#currentUser');
				if(currentUser) currentUser.parent().addClass('isHide');
				translate.run(function(t){t.translateAll();});
			}
		}
	})
	.directive('registrationForm', function (translate){
		return {
			templateUrl:"./html/templates/registrationTemplate.html",
			link: function(scope, elm, attrs, ctrl){
				var currentUser = $('#currentUser');
				if(currentUser) currentUser.parent().addClass('isHide');
				translate.run(function(t){t.translateAll();});

				scope.noPayClass = 'isHide';

				scope.changeNoPay = function(){
					scope.noPayClass = scope.noPay ? '' : 'isHide';
				};

				scope.validInput = function($event){
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
			}
		}
	});
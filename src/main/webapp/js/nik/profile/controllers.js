heatSupply.profileControllers = angular.module('profileControllers',[
	'headerFactory']);

heatSupply.profileControllers
	.controller('profileController', 
		function ($scope, translate, hsFactory){
			$scope.onTemplateLoad = function(){
				translate.run(function(t){t.translateAll();});
				getCurrentUserInfo();
			}

			function getCurrentUserInfo(){
				$.getJSON(hsFactory.url + 
					'dataServer/db/CurrentUser?params=' + hsFactory.userId,
					function(data){
						$('input[name="user"').val(
							data.user.slice(0, data.user.indexOf('_')));
						$('input[name="email"').val(data.email);
						console.log(data);
					});
			}
		});
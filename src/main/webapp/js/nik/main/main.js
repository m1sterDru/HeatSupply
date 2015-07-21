heatSupply.mainModule = angular.module('main', [
	'ngRoute',
	'headerControllers',
	'mainFactory',
	'mainControllers'
	]);

// heatSupply.mainModule.config(function ($routeProvider){
// 	$routeProvider.
// 		when('/', {
// 			templateUrl: 'loginForm.html',
// 			controller:'LoginCtrl'
// 		}).
// 		when('/registration', {
// 			templateUrl: 'loginRegistration.html',
// 			controller:'RegisterCtrl'
// 		}).
// 		otherwise({
// 			redirectTo: '/'
// 		})
// });

heatSupply.initWebSocket = function(){
	var url = heatSupply.url.slice(4),
			ws = new WebSocket('ws' + url + 'socketServer');
	ws.onmessage = function (message){
		var jsonData = JSON.parse(message.data);
		console.log(jsonData)
		// if(jsonData.type === 'CommandMessage') {
		// 	if(jsonData.command === 'lang'){
		// 		var param = jsonData.parameters[0],
		// 				lang = param.value,
		// 				btn = document.getElementById('curLangButton'),
		// 				lis = btn.parentNode.getElementsByTagName('ul')[0]
		// 									.getElementsByTagName('li');

		// 		if(lang){
		// 			var li = Array.prototype.filter.call(lis, function(li){
		// 				return li.id === lang;
		// 			})[0];
		// 			li.click();
		// 		}
		// 	}
		// }
	}
	ws.onerror = function (e){
		console.log(e);
	}
	ws.onclose = function (){
		console.log('session close ');
	}
	ws.onopen = function(){
		console.log('session open');
	}

	heatSupply.webSocket = ws;
}
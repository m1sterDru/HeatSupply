heatSupply.mainModule = angular.module('main', [
	'ngRoute',
	'headerControllers',
	'mainFactory',
	'mainControllers'
	]);

heatSupply.mainModule.config(function ($routeProvider){
	$routeProvider.
		when('/', {
			templateUrl: 'html/main/mainForm.html',
			controller:'mainFormController'
		}).
		otherwise({
			redirectTo: '/'
		})
});

heatSupply.initWebSocket = function(url){
	var ws = new WebSocket('ws' + url.slice(4) + 'socketServer');
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
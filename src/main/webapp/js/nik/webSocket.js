heatSupply.initWebSocket = function(url){
	var ws = new WebSocket('ws' + url.slice(4) + 'socketServer');
	ws.onmessage = function (message){
		var jsonData = JSON.parse(message.data);
		console.log(jsonData)
		if(jsonData.type === 'CommandMessage') {
			if(jsonData.command === 'reportHTML'){
				var param = jsonData.parameters[0],
						reportContent = $('#reportContent');
				if(reportContent) {
					reportContent.html(param.content);
					console.log($('#reportContent').width())
					$('#reportContent table:first').css({
						'zoom': ($('#reportContent').width() - 20)/595
					});
				}
			}
		}
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
	heatSupply.socket = ws;
}
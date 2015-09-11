heatSupply.initWebSocket = function(hs, callback){
	var ws = new WebSocket('ws' + hs.url.slice(4) + 'socketServer');
	ws.onmessage = function (message){
		if(message.data instanceof Blob){
			saveTextAsFile(message.data,
				heatSupply.currentReport, heatSupply.currentReportExt);
			return;
		}
		var jsonData = JSON.parse(message.data);
		console.log(jsonData)
		if(jsonData.type === 'CommandMessage'){
			var params = jsonData.parameters;

			function waitScope(selector, scope, counter, callback){
				if(scope != undefined || counter > 5){
					if(callback != null && scope != undefined){
						callback(scope);
					}
					return;
				} else {
					ngElement = $(selector);
					scope = angular.element(ngElement).scope();
				}
				setTimeout(function(){
					waitScope(selector, scope, ++counter, callback);
				}, 200);
			}

			switch(jsonData.command){
				case 'user':
					var ngElement = $('div[data-template="langDirective"]'), scope;
					if(ngElement.length > 0){
						scope = angular.element(ngElement).scope();
					}

					waitScope('div[data-template="langDirective"]',scope, 0,
						function(scope){
							scope.login_user = params[0].value;
							scope.login_userIconClass = '';
							scope.login_href = 'LogoutServlet';
							hs.translator.translateValueByKey(
								hs.language, 'kLogout', function(value){
									scope.login_spanID = '${kLogout}';
									$('.loginSpanSelectot').html(value);
									scope.login_userSpanClass = '';
								});
							scope.longin_class = 'fa fa-sign-out';
							scope.login_userSpanClass = '';
							scope.$apply();
						});
					break;
				case 'reportHTML':
					var param = params[0],
						reportContent = $('#reportContent');
					if(reportContent){
						reportContent.html(param.content);
						if(window.navigator.appVersion.indexOf('Chrome') > 0){
							$('#reportContent table:first').css({
								'zoom': ($('#reportContent').width() - 20)/595
							});
						}
					}
					break;
				case 'deleteOwner':
					if(params[0].success === 'true'){
						location.href = hs.url + '#/account';
					} else {
						$('.error').html('Error. Try again.');
					}
					break;
				case 'addOwner':
					if(params[0].success === 'true'){
						location.href = hs.url + '#/account';
					} else {
						$('.error').html(params[1].message);
					}
					break;
				case 'profileInfo':
					var ngElement = $('#mainContent'), scope;
					if(ngElement.length > 0){
						scope = angular.element(ngElement).scope();
						params.forEach(function(o){
							var key = Object.keys(o)[0];
							$('input[name="' + key + '"').val(o[key]);
						});

						waitScope('#mainContent', scope, 0, function(scope){
							scope.isDisabled = false;
							scope.formStyle = {opacity: 1};
							scope.$apply();
						});
					}
					break;
				case 'ownerList':
					var elementId = params[0].elementId,
							ngElement = $('#' + elementId),
							scope;

					params = params.slice(1);
					if(ngElement.length > 0){
						scope = angular.element(ngElement).scope();
						waitScope('#' + elementId, scope, 0, function(scope){

						});

						if(scope.actowners == undefined) scope.actowners = [];
						params.forEach(function(o){
							var idMeter = Object.keys(o)[0],
									arr = o[idMeter].split(';'),
									owner = Object.create(null);

							owner.idMeter = idMeter;
							owner.sn = arr[0];
							owner.ownerAccount = arr[1];
							owner.name = arr[2];

							scope.actowners.push(owner);
						});
						scope.visibleClass = '';
						scope.isDisabled = false;
						if(elementId === 'delAccountTemplate'){
							scope.account4delete = scope.actowners[0].idMeter + '_' +
									scope.actowners[0].ownerAccount;
							scope.account4delete2 = Object.create(null);
							scope.account4delete2.name = scope.actowners[0].name;
							scope.account4delete2.sn = scope.actowners[0].sn;
							scope.account4delete2.ownerAccount = 
									scope.actowners[0].ownerAccount;
						}
						scope.$apply();
						heatSupply.translator.translateAll();
					}
					break;
				case 'removeProfile':
					window.location.href = hs.url;
					break;
			}
		}
	}
	ws.onerror = function (e){
		console.log(e);
	}
	ws.onclose = function(){
		console.log('session close ');
	}
	ws.onopen = function(){
		console.log('session open');
		if(callback != null) callback();
		var message = Object.create(null);

		message.type = 'CommandMessage';
		message.command = 'user';
		ws.send(JSON.stringify(message));
	}

	function saveTextAsFile(textToWrite, fileNameToSaveAs, ext){
		var typeBlob = ext === 'pdf' ? 
					'application/pdf' : 
							ext === 'xls' ? 'application/csv' :'text/html';
				textFileAsBlob = new Blob([textToWrite], {type: typeBlob}),
				downloadLink = document.createElement("a");

		downloadLink.download = fileNameToSaveAs + '.' + ext;
		downloadLink.href = window.URL.createObjectURL(textFileAsBlob);
		downloadLink.click();
	}

	heatSupply.socket = ws;
}
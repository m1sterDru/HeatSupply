heatSupply.initWebSocket = function(hs, callback){
	var ws = new WebSocket('ws' + hs.url.slice(4) + 'socketServer');

	function findParam(params, name){
		for(key in params){
			if(params[key][name] != undefined)
				return params[key][name];
		}
	}

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
				if(typeof scope !== 'undefined' || counter > 5){
					if(callback != null && typeof scope !== 'undefined'){
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

			function updateOwners(idUser){
				var message = Object.create(null);

				message.type = 'CommandMessage';
				message.command = 'ownerList';
				message.parameters = [
					{'userId': idUser},
					{'selector': "div[data-template='profileDirective']"}
				];
				heatSupply.socket.send(JSON.stringify(message));
				location.href = hs.url + 'main.html#/account';
			}

			switch(jsonData.command){
				case 'user':
					var ngElement = $('div[menu-directive]'), scope;
					if(ngElement.length > 0){
						scope = angular.element(ngElement).scope();
					}

					waitScope('div[menu-directive]',scope, 0,
						function(scope){
							hs.language = findParam(params, 'lang');
							hs.login = findParam(params, 'login');

							$('button[data-btn="curLangButton"]:first')
								.parent().children('ul')
								.find('li[id="' + hs.language + '"]').click();

							localStorage.setItem('heatSupply', JSON.stringify(hs));

							scope.login_user = hs.login;
							scope.login_userIconClass = '';
							scope.login_href = 'LogoutServlet';
							hs.translator.translateValueByKey(
								hs.language, 'kLogout', function(value){
									scope.login_spanID = '${kLogout}';
									$('.loginSpanSelectot').html(value);
									scope.login_href = 'LogoutServlet';
								});
							scope.longin_class = 'fa fa-sign-out';
							scope.$apply();
						});
					break;
				case 'reportHTML':
					var param = params[0],
						reportContent = $('.panel-body');
					if(reportContent){
						reportContent.html(param.content);
						if(window.navigator.appVersion.indexOf('Chrome') > 0){
							$('.panel-body table:first').css({
								'zoom': ($('.panel-body').width() - 30)/595
							});
						}
						waitScope('div[data-template="langDirective"]',scope, 0,
							function(scope){
								if(scope.login_userLink.indexOf('profile.') < 0)
									scope.login_userLink = '#/profile.';
								else
									scope.login_userLink = '#/profile';
								scope.$apply();
							});
					}
					break;
				case 'deleteOwner':
				case 'addOwner':
					var idUser, success, message;

					success = findParam(params, 'success');
					idUser = findParam(params, 'idUser');
					message = findParam(params, 'message');

					if(success === 'true'){
						updateOwners(idUser);
					} else {
						hs.updateError(message);
					}
					break;
				case 'profileInfo':
					var ngElement = $('#profileInfo'), scope;
					if(ngElement.length > 0){
						scope = angular.element(ngElement).scope();
						params.forEach(function(o){
							var key = Object.keys(o)[0];
							$('input[name="' + key + '"]').val(o[key]);
						});

						waitScope('#profileInfo', scope, 0, function(scope){
							scope.isDisabled = false;
							scope.formStyle = {opacity: 1};
							scope.$apply();
						});
					}
					break;
				case 'ownerList':
					var selector, ngElement, scope;

					for(par in params){
						if(params[par].selector != undefined){
							selector = params[par].selector;
							ngElement = $(selector);
							params.splice(par,1);
						}
					}

					if(ngElement.length > 0){
						scope = angular.element(ngElement).scope();
						waitScope(selector, scope, 0, function(scope){
							scope.actowners = [];
							scope.longin_visClass = '';
							params.forEach(function(o){
								var idMeter = Object.keys(o)[0],
										arr = o[idMeter].split(';'),
										owner = Object.create(null);

								owner.idMeter = idMeter;
								owner.sn = arr[0];
								owner.ownerAccount = arr[1];
								owner.name = arr[2];
								if(owner.name.length > 40)
									owner.name = owner.name.slice(0, 40) + ' ...';

								scope.actowners.push(owner);
							});
							scope.visibleClass = '';
							scope.isDisabled = false;
							scope.account4delete = Object.create(null);
							scope.account4delete.name = scope.actowners[0].name;
							scope.account4delete.idMeter = scope.actowners[0].idMeter;
							scope.account4delete.sn = scope.actowners[0].sn;
							scope.account4delete.ownerAccount = 
									scope.actowners[0].ownerAccount;

							if(scope.actowners.length == 1){
								scope.isDisabled = true;
							}
							scope.$apply();
							heatSupply.translator.translateAll();
						});
					}
					break;
				case 'removeProfile':
					var success = findParam(params, 'success'),
							message = findParam(params, 'message');

					if(success === 'true'){
						location.href = hs.url;
					} else {
						hs.updateError(message);
					}
					break;
			}
		}
	}
	ws.onerror = function (e){
		console.log('qqq q')
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
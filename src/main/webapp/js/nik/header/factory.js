angular.module('headerFactory', [])
	.factory('translate', function ($http){
		function getTranslate(callback){
			$http({
				method: 'GET',
				url: '/HeatSupply/js/nik/translate.js',
				cache: true
			})
			.success(function(data){
				var func = new Function('', data);
				try{callback(func);}catch(e){console.log(e);}
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		function getListOfFiles(callback){
			$http({
				method: 'GET',
				url: '/HeatSupply/dataServer/db/filesInDir',
				cache: true
			})
			.success(function(data){
				var files = [];
				data.forEach(function(file){
					file = file.name;
					file = file.slice(file.indexOf('_') + 1, file.indexOf('.'));
					files.push(file);
				});
				callback(files);
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		return {
			langFiles: getListOfFiles,
			run: function(callback){
				getTranslate(function(func){
					var translator = func().Translator();
					callback(translator);
				});
			}
		}
	})
	.factory('hsFactory', function ($http, translate){
		var main;

		function getUserProfile(callback){
			main.isComplete(0, function(){
				$http({
					method: 'GET',
					url: '/HeatSupply/StartServlet',
					cache: false
				})
				.success(function(data){
					if(data.isLogin === 'true'){
						if(callback != null){
							try{
								callback(data);
							} catch(e) {
								console.log(e);
							}
						}
					} else {
						// var url = document.URL;
						// url = url.slice(0, url.indexOf('HeatSupply') + 11);
						// location.href = url;
					}
				})
				.error(function(data, status, headers, config){
					console.log(status)
				});
			});
		};

		function bootstrapConfirm(callback){
			main.translator.translateValueByKey(main.language, 'keySure',
			function(value){
				BootstrapDialog.confirm({
					title: 'WARNING',
					message: value,
					type: BootstrapDialog.TYPE_DANGER,
					closable: false,
					draggable: false,
					btnCancelLabel: 'Cancel',
					btnOKLabel: 'OK',
					btnOKClass: 'btn-danger',
					callback: function(result){
						if(result){
							getUserProfile(function (data){
								if(callback != undefined) callback(data);
							});
						}
					}
				});
			});
		}

		function updateError(key){
			var error = $('div[error-directive] .comment');
			main.translator.translateValueByKey(
				main.language, key, function(value){
					error.html(value);
					error[0].id = '${' + key + '}';
					error.parent().removeClass('isHide');
				});
		}

		function updateErrorWithText(key, text){
			var error = $('div[error-directive] .comment');
			main.translator.translateValueByKey(
				main.language, key, function(value){
					error.html(value + ' ' + text);
					error[0].id = '${' + key + '}';
					error.parent().removeClass('isHide');
				});
		}

		function HeatSupply(){
			var hs = Object.create(null),
					url = document.URL,
					cache = localStorage.getItem('heatSupply');

			hs.url = url.slice(0, url.indexOf('HeatSupply') + 11);
			hs.language = cache ? JSON.parse(cache).language : 'uk';
			hs.getUserProfile = getUserProfile;

			translate.run(function(t){
				hs.translator = t;
				heatSupply.translator = t;
				hs.bootstrapConfirm = bootstrapConfirm;
				hs.updateError = updateError;

				if(location.href.indexOf('/main.') > 0){
					heatSupply.initWebSocket(hs, function(){
						hs.complete = true;
					});
				}
			});
			return hs;
		}

		function getMainFactory(){
			if(!main){
				main = new HeatSupply();
				main.isComplete = function(counter, callback){
					if(main.complete != undefined || counter > 5){
						if(callback != null){
							callback();
						}
						return;
					}
					setTimeout(function(){
						main.isComplete(++counter, callback);
					}, 100);
				}
			}
			return main;
		}
		return getMainFactory();
	})
	.directive('ngEnter', function (){
		return function (scope, element, attrs){
			element.bind("keydown keypress", function (event){
				if(event.which === 13){
					scope.$apply(function (){
						scope.$eval(attrs.ngEnter);
					});
					event.preventDefault();
				}
			});
		};
	});
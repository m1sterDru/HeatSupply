var heatSupply = Object.create(null);
heatSupply.headerCtrls = angular.module('headerControllers', []);

heatSupply.headerCtrls.psFunctions = {
		noCache: 'no-cache',
		results: Object.create(null),
		run: function(name, cb){
			if (name in this.results){
				cb(results[name]);
			} else {
				(function(target, file, cb){
					var sName = file.substring(file.lastIndexOf('/') + 1, 
																		 file.lastIndexOf('.'));
					if (!sName) return false;
					var rawFile = new XMLHttpRequest();
					rawFile.open("GET", file, true);
					if(this.noCache)
						rawFile.setRequestHeader('Cache-Control', this.noCache);
					rawFile.addEventListener("load", function(){
						if(rawFile.readyState === 4 && 
							(rawFile.status === 200 || rawFile.status == 0)){
								var allText = rawFile.responseText,
										func = new Function('', allText);
								target.results[file] = func;
								try{cb(func);}catch(e){console.log(e);console.log(file);}
						}
					});
					rawFile.send(null);
				})(this, name, cb);
			}
		}
	};

heatSupply.headerCtrls.controller('HeaderCtrl', 
	function ($scope){
		var url = document.URL,
				langId = localStorage.getItem('currentLanguage');

		langId = langId ? langId : 'uk';
		url = url.slice(0, url.indexOf('HeatSupply') + 11);
		heatSupply.url = url;
		checkIsLogin();
		heatSupply.headerCtrls.psFunctions.run('./js/nik/translate.js',
			function(func){
				heatSupply.headerCtrls.Translator = func().Translator();
				changeLocale(langId);
			});

		$scope.click = function($event){
			var btn = document.getElementById('curLangButton'),
					el = $event.target;

			if(el.tagName.toLowerCase() !== 'li') el = el.parentNode;
			changeLocale(el.id);
		}

		function changeLocale(langId){
			var btn = document.getElementById('curLangButton'),
					lis = btn.parentNode.getElementsByTagName('ul')[0]
									.getElementsByTagName('li'),
					img, span;

			li = Array.prototype.filter.call(lis, function(li){
				return li.id === langId;
			})[0];
			if(!li) {
				console.log('null'); 
				return;
			}

			img = li.getElementsByTagName('img')[0];
			span = li.getElementsByTagName('span')[0];

			$scope.langId = langId;
			$scope.langImg = img.src;
			$scope.langDesc = span.innerHTML;
			console.log(langId + ' = ' + span.innerHTML)
			if(heatSupply.headerCtrls.Translator)
				heatSupply.headerCtrls.Translator.translateAllByLocaleName(langId);
		}

		function checkIsLogin(){
			$.getJSON(heatSupply.url + 'StartServlet', function(data){
				var isLogin = data.isLogin === 'true',
						aLogin = $('#aLogin');

				if(isLogin){
					document.getElementById('currentUser').innerHTML = data.user;
					aLogin[0].href = 'LogoutServlet';
					aLogin[0].getElementsByTagName('span')[0].id = '${kLogout}';
					aLogin.removeClass('fa-sign-in');
					aLogin.addClass('fa-sign-out');
				}
			});
		};
	});
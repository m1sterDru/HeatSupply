var heatsupplyControllers = angular.module('heatsupplyControllers', []);

heatsupplyControllers.psFunctions = {
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
								try {cb(func);} catch(e){console.log(e);console.log(file);}
						}
					});
					rawFile.send(null);
				})(this, name, cb);
			}
		}
	};

(function(){
		heatsupplyControllers.psFunctions.run('./js/nik/translate.js', 
				function(func){
						heatsupplyControllers.Translator = func().Translator();
						heatsupplyControllers.Translator.translateAll();
				});
})();

heatsupplyControllers.controller('HeatSupplyCtrl', 
	function ($scope){
		var url = document.URL,
				langId = localStorage.getItem('currentLanguage');

		langId = langId ? langId : 'uk';
		url = url.slice(0, url.indexOf('HeatSupply') + 11);
		heatsupply.nik.url = url;
		changeLocale(langId);

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
			if(!li) return;
			
			img = li.getElementsByTagName('img')[0];
			span = li.getElementsByTagName('span')[0];

			$scope.langId = langId;
			$scope.langImg = img.src;
			$scope.langDesc = span.innerHTML;
			if(heatsupplyControllers.Translator)
				heatsupplyControllers.Translator.translateAllByLocaleName(langId);
		}
	});

heatsupplyControllers.controller('LoginCtrl', 
	function ($scope){
		heatsupplyControllers.Translator.translateAll();
	});

heatsupplyControllers.controller('RegisterCtrl', 
	function ($scope, $routeParams){
		heatsupplyControllers.Translator.translateAll();
		$scope.noPayClass = 'isHide';

		$scope.change = function(){
			var noPay = document.getElementById('noPay').checked;
			$scope.noPayClass = noPay ? '' : 'isHide';
		};
	});
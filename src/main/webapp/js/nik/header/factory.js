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
				try{callback(func);}catch(e){console.log(e);console.log(file);}
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		return {
			run: function(callback){
				getTranslate(function(func){
					var translator = func().Translator();
					callback(translator);
				});
			}
		}
	})
	.factory('hsFactory', function(){
		var main;
		function HeatSupply(){
			var hs = Object.create(null),
					url = document.URL,
					cache = localStorage.getItem('heatSupply');

			hs.language = cache ? JSON.parse(cache).language : 'uk';

			hs.url = url.slice(0, url.indexOf('HeatSupply') + 11);
			return hs;
		}

		function getMainFactory(){
			if(!main) {
				main = new HeatSupply();
				console.log('create main')
			}
			return main;
		}
		return getMainFactory();
	});
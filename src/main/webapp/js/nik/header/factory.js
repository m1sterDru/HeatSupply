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
	});
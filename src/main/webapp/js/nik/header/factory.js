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
	.factory('hsFactory', function ($http){
		var main;

		function getUserProfile(callback){
			$http({
				method: 'GET',
				url: '/HeatSupply/StartServlet',
				cache: false
			})
			.success(function(data){
				callback(data);
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		function getUserProfileInfo(callback){
			$http({
				method: 'GET',
				url: '/HeatSupply/ProfileInfoServlet',
				cache: false
			})
			.success(function(data){
				callback(data);
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		function HeatSupply(){
			var hs = Object.create(null),
					url = document.URL,
					cache = localStorage.getItem('heatSupply');

			hs.language = cache ? JSON.parse(cache).language : 'uk';
			getUserProfile(function(data){
				hs.userId = data.userId;
				hs.user = data.user;
				hs.isLogin = data.isLogin;
			});
			hs.getUserProfile = getUserProfile;
			hs.getUserProfileInfo = getUserProfileInfo;
			hs.url = url.slice(0, url.indexOf('HeatSupply') + 11);
			return hs;
		}

		function getMainFactory(){
			if(!main) {
				main = new HeatSupply();
			}
			return main;
		}
		return getMainFactory();
	});
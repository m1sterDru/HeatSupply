angular.module('mainFactory', [])
	.factory('priorities', function ($http){
		function getPriorities(callback){
			$http({
				method: 'GET',
				url: '/PowerSysWeb/dataServer/db/priority',
				cache: true
			})
			.success(callback)
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};
		function getListOfFiles(callback){
			$http({
				method: 'GET',
				url: 'HeatSupply/dataServer/db/filesInDir?params=',
				cache: true
			})
			.success(callback)
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};
		return {
			list: getPriorities,
			find: function(id, callback){
				getPriorities(function(data){
					var priority = data.filter(function(entry){
						return entry.value === id;
					})[0];
					callback(priority);
				})
			}
		};
	});
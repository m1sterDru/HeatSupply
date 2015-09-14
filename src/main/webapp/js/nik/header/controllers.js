var heatSupply = Object.create(null);
heatSupply.headerControllers = angular.module('headerControllers', [
	'ngRoute',
	'headerFactory']);

heatSupply.headerControllers.config(function ($routeProvider){
})
.run(function ($rootScope, $location, hsFactory){
	$rootScope.$on("$routeChangeStart", function (event, next, current){
		var isValid = false;

		switch($location.path()){
			case '':
			case '/':
			case '/login':
			case '/registration': isValid = true; break;
			default: isValid = false;
		}

		if(!isValid){
			hsFactory.getUserProfile(function(){
				hsFactory.translator.translateAll();
			});
		}
	});
});

heatSupply.headerControllers.controller('headerController', 
	function ($scope, $sce, translate, hsFactory){
		var hideSpan = location.href.indexOf('login') > 0 ||
									 location.href.indexOf('registration') > 0;
		$scope.login_href = '#login';
		$scope.login_spanID = '${kLogin}';
		$scope.longin_class = location.href.indexOf('/main.') > 0 ?
			'fa fa-sign-in' : 'isHide';
		$scope.login_userIconClass = '';
		$scope.login_userLink = location.href.indexOf('/main.') > 0 ?
			'#/profile' : 'main.html';

		if(hideSpan) $scope.login_userSpanClass = 'isHide';

		translate.langFiles(function(files){
			var locales = [], index = 1;
			files.forEach(function(file){
				var locale = Object.create(null);
				locale.id = file;
				locales.push(locale);
				hsFactory.translator.translateValueByKey(file, 
					['kFlagLocale','kLangName'],function(value){
						if(value.indexOf('http') != -1)
							locale.img = value;
						else{
							locale.langName = value;
							if((index++) == files.length){
								$scope.$apply();
								$scope.changeLocale(hsFactory.language);
							}
						}
					});
			});
			$scope.locales = locales;
		});

		$scope.changeLocale = function (langId){
			var btn = $('button[data-btn="curLangButton"]:first')[0],
					lis, li, img, span;

			if(btn){
				lis = btn.parentNode.getElementsByTagName('ul')[0]
									.getElementsByTagName('li');

				li = Array.prototype.filter.call(lis, function(li){
					return li.id === langId;
				})[0];
				if(!li){
					console.log('null'); 
					return;
				}

				img = li.getElementsByTagName('img')[0];
				span = li.getElementsByTagName('span')[0];

				$scope.langId = langId;
				$scope.langImg = img.src;
				$scope.langDesc = span.innerHTML;

				hsFactory.language = langId;
				translate.run(function(t){
					t.translateAllByLocaleName(langId);
					localStorage.setItem('heatSupply', JSON.stringify(hsFactory));
				});
			}
		}
	});
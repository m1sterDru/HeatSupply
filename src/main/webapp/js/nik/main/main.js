heatSupply.mainModule = angular.module('main', [
	'ngRoute',
	'headerControllers',
	'mainFactory',
	'mainControllers'
	]);

heatSupply.mainModule.config(function ($routeProvider){
	$routeProvider.
		when('/', {
			templateUrl: 'html/main/mainForm.html'
		}).
		when('/profile', {
			templateUrl: 'html/templates/profileTemplate.html',
			controller:'profileController'
		}).
		when('/profile.', {
			templateUrl: 'html/templates/profileTemplate.html',
			controller:'profileController'
		}).
		when('/account', {
			templateUrl: 'html/templates/accountTemplate.html',
			controller: 'accountController'
		}).
		when('/addOwner', {
			templateUrl: 'html/templates/addOwnerAccountTemplate.html',
			controller: 'ownerAccountController'
		}).
		when('/delOwner', {
			templateUrl: 'html/templates/delOwnerAccountTemplate.html',
			controller: 'ownerAccountDeleteController'
		}).
		when('/deactivation', {
			templateUrl: 'html/templates/deactivationTemplate.html',
			controller: 'deactivationController'
		})
		.otherwise({
			redirectTo: '/'
		});
}).run(function ($rootScope, $location, hsFactory){
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

heatSupply.mainModule
	.directive('menuDirective', function (translate){
		return {
			templateUrl:'./html/directives/menuDirective.html',
			link: function(scope, elm, attrs, ctrl){
				$(elm).find('#menuNavbar .btn-group:first button')
					.each(function(ind, btn){
						var ul = $(btn).parent().children('ul');
						if(ul.length > 0){
							ul.hover(
								function(){
									$(this).parent().children('button')
										.attr('aria-expanded',true);
								},
								function(){
									$(this).parent().removeClass('open selectedMenu');
									$(this).parent().children('button')
										.attr('aria-expanded',false);
									$(this).parent().children('button')
										.removeClass('selectedMenu');
								});

							$(btn).hover(
								function(){
									$(this).parent().addClass('open selectedMenu');
									$(this).addClass('selectedMenu');
								},
								function(){
									var aBtn = $(this);
									setTimeout(function(){
										if(aBtn.attr('aria-expanded') !== 'true'){
											aBtn.parent().removeClass('open selectedMenu');
											aBtn.removeClass('selectedMenu');
										}
									}, 100);
								});
						}
					});
				translate.run(function(t){
					t.translateAll();
				});
			},
			controller: 'menuController'
		}
	})
	.directive('registrationForm', function (translate){
		return {
			templateUrl:'./html/directives/registrationTemplate.html',
			link: function(scope, elm, attrs, ctrl){
				translate.run(function(t){t.translateAll();});
			}
		}
	})
	.directive('profileDirective', function (translate){
		return {
			templateUrl:'./html/directives/profileDirective.html',
			link: function(scope, elm, attrs, ctrl){
				translate.run(function(t){t.translateAll();});
			}
		}
	});
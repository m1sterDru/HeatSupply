var heatsupply = angular.module('heatsupply', ['heatsupplyControllers']);

heatsupply.nik = Object.create(null);

$(document).ready(function(){
	$.getJSON(heatsupply.nik.url + 'StartServlet', function(data){
		var isLogin = data.isLogin === 'true',
				aLogin = $('#aLogin');
		if(isLogin) {
			console.log(isLogin);
			console.log(aLogin);
			aLogin[0].href = 'LogoutServlet';
			aLogin[0].getElementsByTagName('span')[0].id = '${kLogout}';
			aLogin.removeClass('fa-sign-in');
			aLogin.addClass('fa-sign-out');
		}
	});
});
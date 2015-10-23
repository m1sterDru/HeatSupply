return {
	Translator: function(){
		var returnInstance = Object.create(null), filesLocale = '';

		messageResource.init({filePath : 'lang/'});

		function translateARMByLocale(locale){
			var translateAll = function(){
				var all = document.querySelectorAll('[pr-lang]');
				Array.prototype.forEach.call(all, function(el){
					var key = el.hasAttribute("id") ? el.id :
										el.getAttribute("pr-lang");
					if(el.hasAttribute('id'))
						key = key.substring(2, key.length - 1);
					if (el.hasAttribute("title")) {
						el.title = messageResource.get(key, locale);
					} else {
						el.innerHTML = messageResource.get(key, locale);
					}
				});
			}

			if(filesLocale.indexOf(locale) < 0){
				messageResource.load(locale, function(){
					translateAll();
				});
				filesLocale += locale + ';';
			} else {
				translateAll();
			}
		}

		returnInstance.translateValueByKey = function(locale, key, callback){
			locale = 'Language_' + locale;
			if(filesLocale.indexOf(locale) < 0){
				messageResource.load(locale, function(){
					if(typeof key === 'string')
						callback(messageResource.get(key, locale));
					else {
						key.forEach(function(k){
							callback(messageResource.get(k, locale), k);
						});
					}
				});
				filesLocale += locale + ';';
			} else {
				if(typeof key === 'string')
					callback(messageResource.get(key, locale));
				else {
					key.forEach(function(k){
						callback(messageResource.get(k, locale));
					});
				}
			}
		}

		returnInstance.translateAll = function(){
			var btn = $('button[data-btn="curLangButton"]:first');
			var lang = btn ? btn.attr('lang') : null;
			if(!lang) return;
			translateARMByLocale('Language_' + lang);
		}

		returnInstance.translateAllByLocaleName = function(localeName){
			translateARMByLocale('Language_' + localeName);
		}

		return returnInstance;
	}
}
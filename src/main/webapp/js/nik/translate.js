return {
	Translator: function(){
		var returnInstance = Object.create(null), filesLocale = '';

		messageResource.init({filePath : 'lang/'});

		function translateARMByLocale(locale){
			var translateAll = function(){
				var all = document.body.getElementsByTagName("span");
				for (var i = 0; i < all.length; i++){
					var el = all[i],
							begInd = el.id.indexOf("${");
					if (begInd > -1) {
						var key = el.id.substring(begInd + 2);
						key = key.substring(0, key.indexOf('}'));
						if (el.hasAttribute("title")) {
							el.title = messageResource.get(key, locale);
						} else {
							el.innerHTML = messageResource.get(key, locale);
						}
					}
				}
			}

			if (filesLocale.indexOf(locale) < 0){
				messageResource.load(locale, function(){
					translateAll();
				});
				filesLocale += locale + ';';
			} else {
				translateAll();
			}
		}

		returnInstance.translateValueByKey = function(key){
			var langId = localStorage.getItem('heatSupply').language, 
					locale = 'Language_' + langId;
			return messageResource.get(key, locale);
		}

		returnInstance.translateAll = function(){
			var btn = document.getElementById('curLangButton');
			if(!btn) return;
			var lang = btn.getAttribute('lang');
			translateARMByLocale('Language_' + lang);
		}

		returnInstance.translateAllByLocaleName = function(localeName){
			translateARMByLocale('Language_' + localeName);
		}

		return returnInstance;
	}
}
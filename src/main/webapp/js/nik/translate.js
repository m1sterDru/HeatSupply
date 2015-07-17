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
						if (key === 'keyChart'){
							var chart = $('#tabChart').highcharts();
							var selRect = $('#selectableRectangle')[0];
							if(selRect != undefined && selRect.hasAttribute('selectedId')
									&& selRect.getAttribute('selectedId') != undefined) {
								var curItem = $('#' + selRect.getAttribute('selectedId'))[0];
								chart.yAxis[0].setTitle({
									text: messageResource.get('key_pValue', locale) + ', ' +
												(curItem !== undefined ? curItem.getAttribute('unit'): '')
								});
							}
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
			localStorage.setItem('currentLanguage', 
				locale.slice(locale.indexOf('_') + 1));
		}

		returnInstance.translateValueByKey = function(key){
			var langId = localStorage.getItem('currentLanguage');
			return messageResource.get(key, 'Language_' + langId);
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
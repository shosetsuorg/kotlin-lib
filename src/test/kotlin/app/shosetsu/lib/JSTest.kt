package app.shosetsu.lib

import app.shosetsu.lib.js.JSExtension
import org.junit.Test

/**
 * 04 / 08 / 2022
 */
class JSTest {
	companion object {
		const val javascript = """
			var name = "pineapple"
			var baseURL = "blabla"
			var imageURL = "bla"
			var id = 5
			var hasSearch = true
			var isSearchIncrementing = true
			var hasCloudFlare = false
			// listings
			// settingsModel
			// searchFiltersModel
			// chapterType
			var startIndex = 0

			const settings = [
				[1, 1]
			];
			
			function updateSetting(key,value) {
				console.log(settings);
				settings[key] = value;
				console.log(settings);
			}
		"""
	}

	@Test
	fun init() {
		val ext: IExtension = JSExtension(javascript, "Pineapple")
		println(ext.name)
		println(ext.baseURL)
		println(ext.formatterID)
		println(ext.hasSearch)
		println(ext.isSearchIncrementing)
		println(ext.hasCloudFlare)
		//
		println(ext.startIndex)
		ext.updateSetting(1, 2)
	}
}
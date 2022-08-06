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
			var listings = [
				lib.Listing(
					"A",
					false,
					function ignore(data){
						return [lib.NovelListing()]
					}
				)
			];
			var settingsModel = [
				lib.TextFilter(0, "test"),
				lib.TextFilter(0, "test"),
			];
			var searchFiltersModel = [
				lib.TextFilter(0, "test"),
				lib.TextFilter(0, "test"),
			];
			var chapterType = lib.ChapterType(0)
			var startIndex = 0

			const settings = [
				[1, 1]
			];
			
			function updateSetting(key,value) {
				console.log(settings);
				settings[key] = value;
				console.log(settings);
			}
			
			function search(data){
				return [lib.NovelListing()]
			}
			
			function getPassage(chapterURL) {
				return [];
			}
			
			function parseNovel(novelURL, loadchapters) {
				return lib.NovelInfo()
			}
			
			function expandURL(smallURL,type){
				return "big"
			}
			
			function shrinkURL(longURL,type){
				return "small"
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
		println(ext.listings.contentToString())
		//println(ext.settingsModel.contentToString())
		//println(ext.searchFiltersModel.contentToString())
		println(ext.chapterType)
		println(ext.startIndex)
		ext.updateSetting(1, 2)
		println(ext.search(emptyMap<Int, Any>()))
		println(ext.getPassage(""))
		println(ext.parseNovel("...", false))
		println(ext.expandURL("small", 0))
		println(ext.shrinkURL("small", 0))
	}
}
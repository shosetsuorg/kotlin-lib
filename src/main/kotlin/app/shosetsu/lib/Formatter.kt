package app.shosetsu.lib

/*
 * This file is part of shosetsu-services.
 * shosetsu-services is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * shosetsu-services is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with shosetsu-services.  If not, see https://www.gnu.org/licenses/.
 */

/**
 * shosetsu-services
 * 30 / May / 2019
 *
 * @author github.com/doomsdayrs
 * @noinspection unused
 */
@Suppress("unused")
interface Formatter {

	/**
	 * This represents a "Page" that the source might have for listing novels
	 * Many sources have a singular listing, which contains filters and queries
	 * Some sources have extra pages to display sources, such as a separate page for latest updates or most viewed
	 */
	class Listing(
			/** Name of this listing */
			val name: String,

			/** If you can continue scrolling for more data or not */
			val isIncrementing: Boolean,

			/**
			 * This gets data from the listing
			 */
			val getListing: (data: Map<Int, *>, increment: Int?) -> Array<Novel.Listing>
	)

	/** Name of this extension */
	val name: String

	/**
	 * Base URL of the extension
	 * Used to open up in browser
	 */
	val baseURL: String

	/**
	 * Image URL of the extension
	 * Used for user recognition
	 */
	val imageURL: String

	/**
	 * Formatter identification
	 * Separates this from other extensions
	 * Should be as unique as possible
	 */
	val formatterID: Int

	/** If this extension is capable of searching */
	val hasSearch: Boolean

	/** If this extension has cloudflare protection that requires interception */
	val hasCloudFlare: Boolean

	/**
	 * Represents the different listings of this extension
	 * @see [Listing]
	 */
	val listings: Array<Listing>

	/**
	 * Represents the settings model this extension provides
	 */
	val settingsModel: Array<Filter<*>>

	/**
	 * Represents the filters models to be adjusted on the right
	 */
	val searchFiltersModel: Array<Filter<*>>

	/**
	 * Applies a setting a value
	 */
	fun updateSetting(id: Int, value: Any?)

	/**
	 * @param data Data that includes query and other filters
	 * @param reporter Way to print out debug to log
	 */
	fun search(data: Map<Int, *>, reporter: (status: String) -> Unit): Array<Novel.Listing>

	/**
	 * Get the passage of a novel
	 * @param chapterURL will be the unFresh URL
	 */
	fun getPassage(chapterURL: String): String

	/**
	 * Using the unFresh [novelURL], Requests for information on the novel
	 * @param novelURL unFresh URL of the novel
	 * @param loadChapters option to load chapters or not, for minor performance options (and debug)
	 * @param reporter Way to print out debug to log
	 */
	fun parseNovel(novelURL: String, loadChapters: Boolean, reporter: (status: String) -> Unit): Novel.Info

	/**
	 *  @param smallURL [String] URL to make fresh
	 *  @param type [Int] Type of url. 1 for novelURL, 2 for chapterURL
	 *  @return A reconstructed URL; In case the URL is shortened to save space
	 */
	fun freshURL(smallURL: String, type: Int): String
}

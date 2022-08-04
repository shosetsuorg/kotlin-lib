package app.shosetsu.lib.js

import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import app.shosetsu.lib.lua.*
import org.mozilla.javascript.Context
import java.io.InputStream

/**
 * 03 / 08 / 2022
 *
 * Javascript extension, utilizing rhino
 */
class JSExtension : IExtension {
	private val jsContext by lazy { Context.enter() }
	private val scope by lazy { shosetsuJSGlobals(jsContext) }

	constructor(
		content: String,
		name: String
	) {
		jsContext.evaluateString(scope, content, name, 0, null)
	}

	constructor(
		inputStream: InputStream,
		name: String
	) {
		jsContext.evaluateReader(scope, inputStream.reader(), name, 0, null)
	}

	override val exMetaData: IExtension.ExMetaData
		get() = TODO("Not yet implemented")
	override val name: String by lazy {
		Context.toString(scope.get(KEY_NAME, scope))
	}
	override val baseURL: String by lazy {
		Context.toString(scope.get(KEY_BASE_URL, scope))
	}
	override val imageURL: String by lazy {
		Context.toString(scope.get(KEY_IMAGE_URL, scope))
	}
	override val formatterID: Int by lazy {
		Context.toNumber(scope.get(KEY_ID, scope)).toInt()
	}
	override val hasSearch: Boolean
		get() = TODO("Not yet implemented")
	override val isSearchIncrementing: Boolean
		get() = TODO("Not yet implemented")
	override val hasCloudFlare: Boolean
		get() = TODO("Not yet implemented")
	override val listings: Array<IExtension.Listing>
		get() = TODO("Not yet implemented")
	override val settingsModel: Array<Filter<*>>
		get() = TODO("Not yet implemented")
	override val searchFiltersModel: Array<Filter<*>>
		get() = TODO("Not yet implemented")
	override val chapterType: Novel.ChapterType by lazy {
		scope.get(KEY_CHAPTER_TYPE, scope) as Novel.ChapterType
	}
	override val startIndex: Int by lazy {
		Context.toNumber(scope.get(KEY_ID, scope)).toInt()
	}

	override fun updateSetting(id: Int, value: Any?) {
		TODO("Not yet implemented")
	}

	override fun search(data: Map<Int, *>): Array<Novel.Listing> {
		TODO("Not yet implemented")
	}

	override fun getPassage(chapterURL: String): ByteArray {
		TODO("Not yet implemented")
	}

	override fun parseNovel(novelURL: String, loadChapters: Boolean): Novel.Info {
		TODO("Not yet implemented")
	}

	override fun expandURL(smallURL: String, type: Int): String {
		TODO("Not yet implemented")
	}

	override fun shrinkURL(longURL: String, type: Int): String {
		TODO("Not yet implemented")
	}
}
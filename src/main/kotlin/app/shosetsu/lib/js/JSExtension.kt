package app.shosetsu.lib.js

import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import app.shosetsu.lib.lua.*
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
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
		content: String, name: String
	) {
		jsContext.evaluateString(scope, content, name, 0, null)
	}

	constructor(
		inputStream: InputStream, name: String
	) {
		jsContext.evaluateReader(scope, inputStream.reader(), name, 0, null)
	}

	override val exMetaData: IExtension.ExMetaData
		get() = TODO("Not yet implemented")
	override val name: String by lazy {
		Context.toString(scope.getOrThrow(KEY_NAME, scope))
	}
	override val baseURL: String by lazy {
		Context.toString(scope.getOrThrow(KEY_BASE_URL, scope))
	}
	override val imageURL: String by lazy {
		Context.toString(scope.getOrThrow(KEY_IMAGE_URL, scope))
	}
	override val formatterID: Int by lazy {
		Context.toNumber(scope.getOrThrow(KEY_ID, scope)).toInt()
	}
	override val hasSearch: Boolean by lazy {
		Context.toBoolean(scope.getOrThrow(KEY_HAS_SEARCH, scope))
	}
	override val isSearchIncrementing: Boolean by lazy {
		Context.toBoolean(scope.getOrThrow(KEY_IS_SEARCH_INC, scope))
	}
	override val hasCloudFlare: Boolean by lazy {
		Context.toBoolean(scope.getOrThrow(KEY_HAS_CLOUD_FLARE, scope))
	}
	override val listings: Array<IExtension.Listing>
		get() = TODO("Not yet implemented")
	override val settingsModel: Array<Filter<*>>
		get() = TODO("Not yet implemented")
	override val searchFiltersModel: Array<Filter<*>>
		get() = TODO("Not yet implemented")
	override val chapterType: Novel.ChapterType by lazy {
		scope.getOrThrow(KEY_CHAPTER_TYPE, scope) as Novel.ChapterType
	}
	override val startIndex: Int by lazy {
		Context.toNumber(scope.getOrThrow(KEY_START_INDEX, scope)).toInt()
	}

	override fun updateSetting(id: Int, value: Any?) {
		val func = scope.getOrThrow(KEY_UPDATE_SETTING, scope) as Function
		func.call(jsContext, scope, scope, arrayOf(id, value))
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
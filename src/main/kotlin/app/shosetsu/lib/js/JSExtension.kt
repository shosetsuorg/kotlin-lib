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
		scope.getOrThrow(KEY_NAME, scope)
	}
	override val baseURL: String by lazy {
		scope.getOrThrow(KEY_BASE_URL, scope)
	}
	override val imageURL: String by lazy {
		scope.getOrThrow(KEY_IMAGE_URL, scope)
	}
	override val formatterID: Int by lazy {
		scope.getOrThrow(KEY_ID, scope)
	}
	override val hasSearch: Boolean by lazy {
		scope.getOrThrow(KEY_HAS_SEARCH, scope)
	}
	override val isSearchIncrementing: Boolean by lazy {
		scope.getOrThrow(KEY_IS_SEARCH_INC, scope)
	}
	override val hasCloudFlare: Boolean by lazy {
		scope.getOrThrow(KEY_HAS_CLOUD_FLARE, scope)
	}
	override val listings: Array<IExtension.Listing> by lazy {
		scope.getOrThrow(KEY_LISTINGS, scope)
	}
	override val settingsModel: Array<Filter<*>> by lazy {
		scope.getArrayOrThrow(KEY_SETTINGS, scope)
	}
	override val searchFiltersModel: Array<Filter<*>> by lazy {
		scope.getArrayOrThrow(KEY_SEARCH_FILTERS, scope)
	}
	override val chapterType: Novel.ChapterType by lazy {
		scope.getOrThrow(KEY_CHAPTER_TYPE, scope) as Novel.ChapterType
	}
	override val startIndex: Int by lazy {
		scope.getOrThrow<Double>(KEY_START_INDEX, scope).toInt()
	}

	override fun updateSetting(id: Int, value: Any?) {
		scope.callOrThrow(jsContext, KEY_UPDATE_SETTING, scope, id, value)
	}

	override fun search(data: Map<Int, *>): Array<Novel.Listing> {
		return scope.callOrThrowReturn(jsContext, KEY_SEARCH, scope, data) ?: emptyArray()
	}

	override fun getPassage(chapterURL: String): ByteArray {
		return scope.callOrThrowReturn(jsContext, KEY_GET_PASSAGE, scope, chapterURL)
			?: ByteArray(0)
	}

	override fun parseNovel(novelURL: String, loadChapters: Boolean): Novel.Info {
		return scope.callOrThrowReturn(
			jsContext,
			KEY_PARSE_NOVEL,
			scope,
			novelURL,
			loadChapters
		)!!
	}

	override fun expandURL(smallURL: String, type: Int): String {
		return scope.callOrThrowReturn(
			jsContext,
			KEY_EXPAND_URL,
			scope,
			smallURL,
			type
		)!!
	}

	override fun shrinkURL(longURL: String, type: Int): String {
		return scope.callOrThrowReturn(
			jsContext,
			KEY_SHRINK_URL,
			scope,
			longURL,
			type
		)!!
	}
}
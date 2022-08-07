package app.shosetsu.lib.js

import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import app.shosetsu.lib.ShosetsuSharedLib
import app.shosetsu.lib.exceptions.HTTPException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.closeQuietly
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.NodeVisitor
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import java.util.concurrent.TimeUnit

/**
 * 04 / 08 / 2022
 */
class ShosetsuJSLib(
	val context: Context,
	val scope: Scriptable
) {
	val DEFAULT_CACHE_CONTROL: CacheControl =
		CacheControl.Builder().maxAge(10, TimeUnit.MINUTES).build()

	val DEFAULT_HEADERS: Headers = Headers.Builder()
		.apply {
			// Apply default headers
			ShosetsuSharedLib.shosetsuHeaders.forEach { (name, value) ->
				add(name, value)
			}
		}
		.build()

	val DEFAULT_BODY: RequestBody = FormBody.Builder().build()

	@Suppress("DeprecatedCallableAddReplaceWith")
	@Deprecated("No longer using array lists to simplify conversion")
	fun AsList(arr: Any?): Any? = arr
	fun <E> Reverse(arr: Array<E>): Unit = arr.reverse()

	/** Lua Constructor for [IExtension.Listing] */
	@Suppress("UNCHECKED_CAST")
	fun Listing(name: String, increments: Boolean, func: Function) =
		IExtension.Listing(name, increments) { data ->
			Context.jsToJava(
				func.call(context, scope, scope, arrayOf(data)),
				Array<Novel.Listing>::class.java
			) as Array<Novel.Listing>
		}

	/** [Novel.Listing] Constructor */
	fun NovelListing(): Novel.Listing = Novel.Listing()

	/** [Novel.Info] Constructor */
	fun NovelInfo(): Novel.Info = Novel.Info()

	/** [Novel.Chapter] Constructor */
	fun NovelChapter(): Novel.Chapter = Novel.Chapter()

	/** [Novel.Status] Constructor */
	fun NovelStatus(type: Int): Novel.Status = Novel.Status.fromInt(type)

	/** [Novel.ChapterType] Constructor */
	fun ChapterType(type: Int): Novel.ChapterType =
		Novel.ChapterType.valueOf(type)

	// For filters

	/** [app.shosetsu.lib.Filter.Text] Constructor */
	fun TextFilter(id: Int, name: String): Filter.Text =
		Filter.Text(id, name)

	/** [app.shosetsu.lib.Filter.Switch] Constructor */
	fun SwitchFilter(id: Int, name: String): Filter.Switch =
		Filter.Switch(id, name)

	/** [app.shosetsu.lib.Filter.Checkbox] Constructor */
	fun CheckboxFilter(id: Int, name: String): Filter.Checkbox =
		Filter.Checkbox(id, name)

	/** [app.shosetsu.lib.Filter.TriState] Constructor */
	fun TriStateFilter(id: Int, name: String): Filter.TriState =
		Filter.TriState(id, name)

	/** [app.shosetsu.lib.Filter.Dropdown] Constructor */
	fun DropdownFilter(
		id: Int,
		name: String,
		choices: Array<String>
	): Filter.Dropdown =
		Filter.Dropdown(id, name, choices)

	/** [app.shosetsu.lib.Filter.RadioGroup] Constructor */
	fun RadioGroupFilter(
		id: Int,
		name: String,
		choices: Array<String>
	): Filter.RadioGroup =
		Filter.RadioGroup(id, name, choices)

	/** [app.shosetsu.lib.Filter.List] Constructor */
	fun FilterList(name: String, filters: Array<Filter<*>>): Filter.List =
		Filter.List(name, filters)

	/** [app.shosetsu.lib.Filter.Group] Constructor */
	fun <T> FilterGroup(
		name: String,
		filters: Array<Filter<T>>
	): Filter.Group<T> =
		Filter.Group(name, filters)


	fun GET(
		url: String,
		headers: Headers,
		cacheControl: CacheControl
	): Request =
		Request.Builder().url(url).headers(headers)
			.cacheControl(cacheControl).build()

	fun NodeVisitor(head: Function, tail: Function, elems_only: Boolean) =
		object : NodeVisitor {
			override fun head(node: Node, depth: Int) {
				if (!elems_only || node is Element) {

					head.call(
						context,
						scope,
						scope,
						arrayOf(
							Context.javaToJS(node, scope),
							Context.toNumber(depth)
						)
					)
				}
			}

			override fun tail(node: Node, depth: Int) {
				if (!elems_only || node is Element)
					tail.call(
						context,
						scope,
						scope,
						arrayOf(Context.javaToJS(node, scope), Context.toNumber(depth))
					)
			}
		}

	fun POST(
		url: String,
		headers: Headers,
		body: RequestBody,
		cacheControl: CacheControl
	): Request =
		Request.Builder().url(url).post(body).headers(headers)
			.cacheControl(cacheControl).build()

	fun PageOfElem(
		elem: Element,
		remove_style_attr: Boolean,
		custom_style: String,
		keep_scripts: Boolean
	): String {
		val toRemove = mutableListOf<Element>()
		elem.traverse(object : NodeVisitor {
			override fun head(node: Node, depth: Int) {
				if (node !is Element) return

				if (!keep_scripts && node.tagName() == "script" ||
					remove_style_attr && node.tagName() == "style"
				) {
					toRemove.add(node)
					return
				}

				node.attributes().forEach {
					if (remove_style_attr && it.key == "style" || !keep_scripts && it.key.startsWith(
							"on"
						)
					)
						node.removeAttr(it.key)
				}
			}

			override fun tail(node: Node, depth: Int) {}
		})
		toRemove.forEach { it.remove() }

		val head = if (custom_style != "") "<style type=\"text/css\">$custom_style</style>" else ""
		return "<!DOCTYPE html><html><head>$head</head><body>${elem.outerHtml()}</body></html>"
	}

	fun Document(str: String): Document = Jsoup.parse(str)
	fun Request(req: Request): Response = ShosetsuSharedLib.httpClient.newCall(req).execute()

	@Throws(HTTPException::class)
	fun RequestDocument(req: Request): Document = Document(
		Request(req).let { r ->
			r.takeIf { it.code == 200 }?.body?.string() ?: run {
				r.closeQuietly()
				throw HTTPException(r.code)
			}
		}
	)

	@Throws(HTTPException::class)
	fun GETDocument(url: String): Document = RequestDocument(
		GET(
			url,
			DEFAULT_HEADERS,
			DEFAULT_CACHE_CONTROL
		)
	)

	// For advanced users who want to (or need to) do everything themselves.
	fun HttpClient(): OkHttpClient = ShosetsuSharedLib.httpClient

	fun RequestBuilder(): Request.Builder = Request.Builder()
	fun HeadersBuilder(): Headers.Builder = Headers.Builder()
	fun FormBodyBuilder(): FormBody.Builder = FormBody.Builder()
	fun DefaultCacheControl(): CacheControl.Builder = CacheControl.Builder()

	fun MediaType(str: String): MediaType = str.toMediaType()
	fun RequestBody(data: String, type: MediaType): RequestBody =
		data.toRequestBody(type)

	fun Log(name: String, arguments: String) {
		ShosetsuSharedLib.logger(name, arguments)
	}
}
package app.shosetsu.lib.lua

import app.shosetsu.lib.*
import app.shosetsu.lib.exceptions.HTTPException
import app.shosetsu.lib.exceptions.MissingExtensionLibrary
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.closeQuietly
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.select.NodeVisitor
import org.luaj.vm2.*
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.CoerceLuaToJava
import java.util.concurrent.TimeUnit


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
 * 2020-01-20
 *
 * @author github.com/technojo4
 */
class ShosetsuLuaLib : TwoArgFunction() {

	override fun call(modname: LuaValue, env: LuaValue): LuaValue {
		val g: Globals = env.checkglobals()
		__index.load = g["load"] as LuaFunction

		val mt = LuaTable()
		mt["__index"] = __index
		g.setmetatable(mt)
		return g
	}

	@Suppress(
		"unused",
		"PrivatePropertyName",
		"FunctionName",
		"MemberVisibilityCanBePrivate"
	)
	internal object LibFunctions {
		fun DEFAULT_CACHE_CONTROL(): CacheControl =
			CacheControl.Builder().maxAge(10, TimeUnit.MINUTES).build()

		fun DEFAULT_HEADERS(): Headers = Headers.Builder().build()
		fun DEFAULT_BODY(): RequestBody = FormBody.Builder().build()

		fun <E> List(): ArrayList<E> = ArrayList()
		fun <E> AsList(arr: Array<E>): ArrayList<E> = ArrayList(arr.asList())
		fun <E> Reverse(arr: ArrayList<E>): Unit = arr.reverse()

		/** Lua Constructor for [IExtension.Listing] */
		@Suppress("UNCHECKED_CAST")
		fun Listing(name: String, increments: Boolean, func: LuaFunction) =
			IExtension.Listing(name, increments) { data ->
				CoerceLuaToJava.coerce(
					func.call(data.toLua()),
					Array<Novel.Listing>::class.java
				) as Array<Novel.Listing>
			}

		/** [Novel.Listing] Constructor */
		fun _Novel(): Novel.Listing = Novel.Listing()

		/** [Novel.Info] Constructor */
		fun _NovelInfo(): Novel.Info = Novel.Info()

		/** [Novel.Chapter] Constructor */
		fun _NovelChapter(): Novel.Chapter = Novel.Chapter()

		/** [Novel.Status] Constructor */
		fun _NovelStatus(type: Int): Novel.Status = Novel.Status.fromInt(type)

		/** [Novel.ChapterType] Constructor */
		fun _ChapterType(type: Int): Novel.ChapterType =
			Novel.ChapterType.valueOf(type)

		/** Loads libraries from cache or by calling [libLoader] */
		@Throws(LuaError::class)
		fun Require(name: String): LuaValue =
			libLoader(name)
				?: throw MissingExtensionLibrary("Missing Library:\n\t\t$name")


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


		fun _GET(
			url: String,
			headers: Headers,
			cacheControl: CacheControl
		): Request =
			Request.Builder().url(url).headers(headers)
				.cacheControl(cacheControl).build()

		fun _NodeVisitor(head: LuaFunction, tail: LuaFunction, elems_only: Boolean) =
			object : NodeVisitor {
				override fun head(node: Node?, depth: Int) {
					if (!elems_only || node is Element)
						head.call(CoerceJavaToLua.coerce(node), LuaValue.valueOf(depth))
				}

				override fun tail(node: Node?, depth: Int) {
					if (!elems_only || node is Element)
						tail.call(CoerceJavaToLua.coerce(node), LuaValue.valueOf(depth))
				}
			}

		fun _POST(
			url: String,
			headers: Headers,
			body: RequestBody,
			cacheControl: CacheControl
		): Request =
			Request.Builder().url(url).post(body).headers(headers)
				.cacheControl(cacheControl).build()

		fun _PageOfElem(elem: Element, remove_style_attr: Boolean, custom_style: String, keep_scripts: Boolean): String {
			val toRemove = mutableListOf<Element>()
			elem.traverse(object : NodeVisitor {
				override fun head(node: Node?, depth: Int) {
					if (node !is Element) return

					if (!keep_scripts && node.tagName() == "script" ||
						remove_style_attr && node.tagName() == "style") {
						toRemove.add(node)
						return
					}

					node.attributes().forEach {
						if (remove_style_attr && it.key == "style" || !keep_scripts && it.key.startsWith("on"))
							node.removeAttr(it.key)
					}
				}

				override fun tail(node: Node?, depth: Int) {}
			})
			toRemove.forEach { it.remove() }

			val head = if (custom_style != "") "<style type=\"text/css\">$custom_style</style>" else ""
			return "<!DOCTYPE html><html><head>$head</head><body>${elem.outerHtml()}</body></html>"
		}

		fun Document(str: String): Document = Jsoup.parse(str)!!
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
			_GET(
				url,
				DEFAULT_HEADERS(),
				DEFAULT_CACHE_CONTROL()
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
	}

	@Suppress("ClassName")
	internal object __index : TwoArgFunction() {
		lateinit var load: LuaFunction
		private val lib: LuaValue = CoerceJavaToLua.coerce(LibFunctions)

		private val luaFuncs: Map<String, LuaValue> by lazy {
			permaLuaFuncs.map { e ->
				e.key to load.call(
					LuaValue.valueOf(e.value),
					LuaValue.valueOf("luafunc(${e.key})")
				).call()
			}.toMap()
		}

		private val wrap: LuaFunction by lazy { luaFuncs["wrap"] as LuaFunction }

		override fun call(_self: LuaValue, k: LuaValue): LuaValue {
			if (k.isstring()) {
				val f = luaFuncs[k.tojstring()]
				if (f != null) return f

				val o = lib[k.tostring()]
				if (o != null && o != LuaValue.NIL)
					return wrap.call(lib, o)
			}
			return LuaValue.NIL
		}
	}

	companion object {
		/** Loads libraries by name (for [LibFunctions.Require]) */
		lateinit var libLoader: (name: String) -> LuaValue?

		/** okhttp client used by [LibFunctions] */
		@Deprecated(
			"Use ShosetsuSharedLib",
			ReplaceWith("ShosetsuSharedLib.httpClient", "app.shosetsu.lib.ShosetsuSharedLib")
		)
		var httpClient: OkHttpClient
			get() = ShosetsuSharedLib.httpClient
			set(value) {
				ShosetsuSharedLib.httpClient = value
			}
		private val permaLuaFuncs by lazy {
			mapOf(
				"GET" to loadResource("GET.lua"),
				"POST" to loadResource("POST.lua"),
				"map" to loadResource("map.lua"),
				"mapNotNil" to loadResource("mapNotNil.lua"),
				"filter" to loadResource("filter.lua"),
				"map2flat" to loadResource("map2flat.lua"),
				"first" to loadResource("first.lua"),
				"wrap" to loadResource("wrap.lua"),
				"flatten" to loadResource("flatten.lua"),
				"pipeline" to loadResource("pipeline.lua"),
				"pageOfElem" to loadResource("pageOfElem.lua"),
				"NodeVisitor" to loadResource("NodeVisitor.lua"),
				"Novel" to loadResource("Novel.lua"),
				"NovelInfo" to loadResource("NovelInfo.lua"),
				"NovelChapter" to loadResource("NovelChapter.lua"),
				"ChapterType" to loadResource("ChapterType.lua"),
				"NovelStatus" to loadResource("NovelStatus.lua")
			)
		}
	}
}

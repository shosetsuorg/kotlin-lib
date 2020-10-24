package app.shosetsu.lib.lua

import app.shosetsu.lib.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.closeQuietly
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
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

	@Suppress("unused", "PrivatePropertyName", "FunctionName", "MemberVisibilityCanBePrivate")
	internal object LibFunctions {
		fun DEFAULT_CACHE_CONTROL(): CacheControl = CacheControl.Builder().maxAge(10, TimeUnit.MINUTES).build()
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
							Array<Novel.Listing>::class.java) as Array<Novel.Listing>
				}

		/** [Novel.Listing] Constructor */
		fun _Novel(): Novel.Listing = Novel.Listing()

		/** [Novel.Info] Constructor */
		fun _NovelInfo(): Novel.Info = Novel.Info()

		/** [Novel.Chapter] Constructor */
		fun _NovelChapter(): Novel.Chapter = Novel.Chapter()

		/** [Novel.Status] Constructor */
		fun _NovelStatus(type: Int): Novel.Status = when (type) {
			0 -> Novel.Status.PUBLISHING
			1 -> Novel.Status.COMPLETED
			2 -> Novel.Status.PAUSED
			else -> Novel.Status.UNKNOWN
		}

		/** [Novel.ChapterType] Constructor */
		fun _ChapterType(type: Int): Novel.ChapterType = when (type) {
			0 -> Novel.ChapterType.STRING
			1 -> Novel.ChapterType.HTML
			2 -> Novel.ChapterType.EPUB
			3 -> Novel.ChapterType.PDF
			4 -> Novel.ChapterType.MARKDOWN
			else -> Novel.ChapterType.STRING
		}

		/** Loads libraries from cache or by calling [libLoader] */
		fun Require(name: String): LuaValue? = libraries[name] ?: libLoader(name).also {
			libraries[name] = it ?: throw LuaError("Missing Library:\n\t\t$name")
		}

		// For filters

		/** [app.shosetsu.lib.Filter.Text] Constructor */
		fun TextFilter(id: Int, name: String): Filter.Text = Filter.Text(id, name)

		/** [app.shosetsu.lib.Filter.Switch] Constructor */
		fun SwitchFilter(id: Int, name: String): Filter.Switch = Filter.Switch(id, name)

		/** [app.shosetsu.lib.Filter.Checkbox] Constructor */
		fun CheckboxFilter(id: Int, name: String): Filter.Checkbox = Filter.Checkbox(id, name)

		/** [app.shosetsu.lib.Filter.Dropdown] Constructor */
		fun DropdownFilter(id: Int, name: String, choices: Array<String>): Filter.Dropdown =
				Filter.Dropdown(id, name, choices)

		/** [app.shosetsu.lib.Filter.RadioGroup] Constructor */
		fun RadioGroupFilter(id: Int, name: String, choices: Array<String>): Filter.RadioGroup =
				Filter.RadioGroup(id, name, choices)

		/** [app.shosetsu.lib.Filter.List] Constructor */
		fun FilterList(name: String, filters: Array<Filter<*>>): Filter.List =
				Filter.List(name, filters)

		/** [app.shosetsu.lib.Filter.Group] Constructor */
		fun <T> FilterGroup(name: String, filters: Array<Filter<T>>): Filter.Group<T> =
				Filter.Group(name, filters)


		fun _GET(url: String, headers: Headers, cacheControl: CacheControl): Request =
				Request.Builder().url(url).headers(headers).cacheControl(cacheControl).build()

		fun _POST(url: String, headers: Headers, body: RequestBody, cacheControl: CacheControl): Request =
				Request.Builder().url(url).post(body).headers(headers).cacheControl(cacheControl).build()


		fun Document(str: String): Document = Jsoup.parse(str)!!
		fun Request(req: Request): Response = httpClient.newCall(req).execute()
		fun RequestDocument(req: Request): Document = Document(
				Request(req).let { r ->
					r.takeIf { it.code == 200 }?.body?.string() ?: {
						r.closeQuietly()
						throw HTTPException(r.code)
					}()
				}
		)

		fun GETDocument(url: String): Document = RequestDocument(_GET(url, DEFAULT_HEADERS(), DEFAULT_CACHE_CONTROL()))

		// For advanced users who want to (or need to) do everything themselves.
		fun HttpClient(): OkHttpClient = httpClient

		fun RequestBuilder(): Request.Builder = Request.Builder()
		fun HeadersBuilder(): Headers.Builder = Headers.Builder()
		fun FormBodyBuilder(): FormBody.Builder = FormBody.Builder()
		fun DefaultCacheControl(): CacheControl.Builder = CacheControl.Builder()

		fun MediaType(str: String): MediaType = str.toMediaType()
		fun RequestBody(data: String, type: MediaType): RequestBody = data.toRequestBody(type)
	}

	@Suppress("ClassName")
	internal object __index : TwoArgFunction() {
		lateinit var load: LuaFunction
		private val lib: LuaValue = CoerceJavaToLua.coerce(LibFunctions)

		private val luaFuncs: Map<String, LuaValue> by lazy {
			permaLuaFuncs.map { e -> e.key to load.call(e.value).call() }.toMap()
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
		/** Cache of libraries loaded by the [libLoader] via [LibFunctions.Require] */
		val libraries: MutableMap<String, LuaValue> = mutableMapOf()

		/** Loads libraries by name (for [LibFunctions.Require]) */
		lateinit var libLoader: (name: String) -> LuaValue?

		/** okhttp client used by [LibFunctions] */
		lateinit var httpClient: OkHttpClient

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
					"Novel" to loadResource("Novel.lua"),
					"NovelInfo" to loadResource("NovelInfo.lua"),
					"NovelChapter" to loadResource("NovelChapter.lua"),
					"NovelStatus" to loadResource("NovelStatus.lua")
			)
		}
	}
}

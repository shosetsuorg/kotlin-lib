package app.shosetsu.lib

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
class ShosetsuLib : TwoArgFunction() {

	override fun call(modname: LuaValue, env: LuaValue): LuaValue {
		val g: Globals = env.checkglobals()
		g.setmetatable(LuaTable())
		g.getmetatable()["__index"] = __index(g)
		return g
	}

	@Suppress("unused", "PrivatePropertyName", "FunctionName", "MemberVisibilityCanBePrivate")
	internal class LibFunctions {
		fun DEFAULT_CACHE_CONTROL(): CacheControl = CacheControl.Builder().maxAge(10, TimeUnit.MINUTES).build()
		fun DEFAULT_HEADERS(): Headers = Headers.Builder().build()
		fun DEFAULT_BODY(): RequestBody = FormBody.Builder().build()

		fun <E> List(): ArrayList<E> = ArrayList()
		fun <E> AsList(arr: Array<E>): ArrayList<E> = ArrayList(arr.asList())
		fun <E> Reverse(arr: ArrayList<E>): Unit = arr.reverse()

		/** Lua Constructor for [Formatter.Listing] */
		@Suppress("UNCHECKED_CAST")
		fun Listing(name: String, increments: Boolean, func: LuaFunction) =
				Formatter.Listing(name, increments) { data, page ->
					CoerceLuaToJava.coerce(
							func.call(
									data.toLua(),
									if (page == null) LuaValue.NIL else LuaValue.valueOf(page)
							),
							Array<Novel.Listing>::class.java) as Array<Novel.Listing>
				}

		/** Lua Constructor for [Novel.Listing] */
		fun Novel(): Novel.Listing = Novel.Listing()

		/** Lua Constructor for [Novel.Info] */
		fun NovelInfo(): Novel.Info = Novel.Info()

		/** Lua Constructor for [Novel.Chapter] */
		fun NovelChapter(): Novel.Chapter = Novel.Chapter()

		/**
		 * Lua Constructor for [Novel.Status]
		 * @param type maps to a certain enum
		 */
		fun NovelStatus(type: Int): Novel.Status = when (type) {
			0 -> Novel.Status.PUBLISHING
			1 -> Novel.Status.COMPLETED
			2 -> Novel.Status.PAUSED
			else -> Novel.Status.UNKNOWN
		}

		fun ChapterType(type: Int): Novel.ChapterType = when (type) {
			0 -> Novel.ChapterType.STRING
			1 -> Novel.ChapterType.HTML
			2 -> Novel.ChapterType.EPUB
			3 -> Novel.ChapterType.PDF
			4 -> Novel.ChapterType.MD
			else -> Novel.ChapterType.STRING
		}

		/**
		 * Requests for a certain library to be loaded
		 */
		fun Require(name: String): LuaValue? = libraries[name] ?: libLoader(name).also {
			libraries[name] = it ?: throw LuaError("Missing Library:\n\t\t$name")
		}

		// For filters

		/** @see [app.shosetsu.lib.Filter.Text] */
		fun TextFilter(id: Int, name: String): Filter.Text = Filter.Text(id, name)

		/** @see [app.shosetsu.lib.Filter.Switch] */
		fun SwitchFilter(id: Int, name: String): Filter.Switch = Filter.Switch(id, name)

		/** @see [app.shosetsu.lib.Filter.Checkbox] */
		fun CheckboxFilter(id: Int, name: String): Filter.Checkbox = Filter.Checkbox(id, name)

		/** @see [app.shosetsu.lib.Filter.Dropdown] */
		fun DropdownFilter(id: Int, name: String, choices: Array<String>): Filter.Dropdown =
				Filter.Dropdown(id, name, choices)

		/** @see [app.shosetsu.lib.Filter.RadioGroup] */
		fun RadioGroupFilter(id: Int, name: String, choices: Array<String>): Filter.RadioGroup =
				Filter.RadioGroup(id, name, choices)

		/** @see [app.shosetsu.lib.Filter.List] */
		fun FilterList(name: String, filters: Array<Filter<*>>): Filter.List =
				Filter.List(name, filters)

		/** @see [app.shosetsu.lib.Filter.Group] */
		fun <T> FilterGroup(name: String, filters: Array<Filter<T>>): Filter.Group<T> =
				Filter.Group(name, filters)

		// For normal extensions, these simple functions are sufficient.
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
	internal class __index(g: Globals) : TwoArgFunction() {
		private val load: LuaFunction = g["load"] as LuaFunction
		private val lib: LuaValue = CoerceJavaToLua.coerce(LibFunctions())

		private val luaFuncs: Map<String, LuaValue> = permaLuaFuncs
				.map { e -> e.key to load.call(e.value) }.toMap()

		private val wrap: LuaFunction = luaFuncs["wrap"] as LuaFunction

		override fun call(_self: LuaValue, k: LuaValue): LuaValue {
			if (k.isstring()) {
				val o = lib[k.tostring()]
				if (o != null && o != LuaValue.NIL)
					return wrap.call(lib, o)
				val f = luaFuncs[k.tojstring()]
				if (f != null) return f
			}
			return LuaValue.NIL
		}
	}

	companion object {
		/** Libraries loaded in via shosetsu. Mapping from library name to their returned value. */
		val libraries: MutableMap<String, LuaValue> = mutableMapOf()

		/** Loads libraries from their names. */
		lateinit var libLoader: (name: String) -> LuaValue?

		/** okhttp HTTP Client used by lib functions. */
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
					"flatten" to loadResource("flatten.lua")
			)
		}
	}
}

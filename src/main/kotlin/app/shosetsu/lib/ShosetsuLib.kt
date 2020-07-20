package app.shosetsu.lib

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
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
	companion object {
		/** Libraries loaded in via shosetsu. Mapping from library name to their returned value. */
		val libraries: MutableMap<String, LuaValue> = mutableMapOf()

		/** Loads libraries from their names. */
		lateinit var libLoader: (name: String) -> LuaValue?

		/** okhttp HTTP Client used by lib functions. */
		lateinit var httpClient: OkHttpClient
	}


	@Suppress("unused", "PrivatePropertyName", "FunctionName", "MemberVisibilityCanBePrivate")
	internal class LibFunctions {
		fun DEFAULT_CACHE_CONTROL() = CacheControl.Builder().maxAge(10, TimeUnit.MINUTES).build()
		fun DEFAULT_HEADERS() = Headers.Builder().build()
		fun DEFAULT_BODY(): RequestBody = FormBody.Builder().build()

		fun <E> List(): ArrayList<E> = ArrayList()
		fun <E> AsList(arr: Array<E>): ArrayList<E> = ArrayList(arr.asList())
		fun <E> Reverse(arr: ArrayList<E>) = arr.reverse()

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
		fun Novel() = Novel.Listing()

		/** Lua Constructor for [Novel.Info] */
		fun NovelInfo() = Novel.Info()

		/** Lua Constructor for [Novel.Chapter] */
		fun NovelChapter() = Novel.Chapter()

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

		/**
		 * Requests for a certain library to be loaded
		 */
		fun Require(name: String): LuaValue? =
				libraries[name] ?: libLoader(name).also {
					libraries[name] = it ?: throw LuaError("Missing Library:\n\t\t$name")
				}

		// For filters

		/** @see [app.shosetsu.lib.TextFilter] */
		fun TextFilter(id: Int, name: String) = Filter.Text(id, name)

		/** @see [app.shosetsu.lib.SwitchFilter] */
		fun SwitchFilter(id: Int, name: String) = Filter.Switch(id, name)

		/** @see [app.shosetsu.lib.CheckboxFilter] */
		fun CheckboxFilter(id: Int, name: String) = Filter.Checkbox(id, name)

		/** @see [app.shosetsu.lib.DropdownFilter] */
		fun DropdownFilter(id: Int, name: String, choices: Array<String>) =
				Filter.Dropdown(id, name, choices)

		/** @see [app.shosetsu.lib.RadioGroupFilter] */
		fun RadioGroupFilter(id: Int, name: String, choices: Array<String>) =
				Filter.RadioGroup(id, name, choices)

		/** @see [app.shosetsu.lib.FilterList] */
		fun FilterList(name: String, filters: Array<Filter<*>>) =
				Filter.List(name, filters)

		/** @see [app.shosetsu.lib.FilterGroup] */
		fun <I, T> FilterGroup(name: String, filters: Array<I>) where I : Filter<T> =
				Filter.Group(name, filters)

		// For normal extensions, these simple functions are sufficient.
		fun _GET(url: String, headers: Headers, cacheControl: CacheControl): Request =
				Request.Builder().url(url).headers(headers).cacheControl(cacheControl).build()

		fun _POST(url: String, headers: Headers, body: RequestBody, cacheControl: CacheControl): Request =
				Request.Builder().url(url).post(body).headers(headers).cacheControl(cacheControl).build()


		fun Document(str: String) = Jsoup.parse(str)!!
		fun Request(req: Request) = httpClient.newCall(req).execute()
		fun RequestDocument(req: Request) = Document(Request(req).body!!.string())
		fun GETDocument(url: String): Document = RequestDocument(_GET(url, DEFAULT_HEADERS(), DEFAULT_CACHE_CONTROL()))

		// For advanced users who want to (or need to) do everything themselves.
		fun HttpClient() = httpClient

		fun RequestBuilder() = Request.Builder()
		fun HeadersBuilder() = Headers.Builder()
		fun FormBodyBuilder() = FormBody.Builder()
		fun DefaultCacheControl() = CacheControl.Builder()

		fun MediaType(str: String) = str.toMediaType()
		fun RequestBody(data: String, type: MediaType) = data.toRequestBody(type)
	}

	/**
	 * Makes use of lua metatables and its "__index" metamethod, which is called when
	 * trying to index a table and a key is not present, to inject our library into the script's Globals.
	 */
	@Suppress("ClassName")
	internal class __index(g: Globals) : TwoArgFunction() {
		private val load: LuaFunction = g["load"] as LuaFunction
		private val lib: LuaValue = CoerceJavaToLua.coerce(LibFunctions())

		private val luaFuncs: Map<String, LuaValue> = mapOf(
				"GET" to """
                        local url, headers, cctl = ...
                        headers = headers or DEFAULT_HEADERS()
                        cctl = cctl or DEFAULT_CACHE_CONTROL()

                        return _GET(url, headers, cctl)
                 """.trimIndent(),
				"POST" to """
                        local url, headers, body, cctl = ...
                        headers = headers or DEFAULT_HEADERS()
                        cctl = cctl or DEFAULT_CACHE_CONTROL()
                        body = body or DEFAULT_BODY()

                        return _POST(url, headers, body, cctl)
                """.trimIndent(),
				"map" to """
                        local o, f = ...
                        local t = {}
						if type(o) == "table" then
							for k,v in pairs(o) do
								t[k] = f(v,k)
							end
						else
							for i=0, o:size()-1 do
								t[i+1] = f(o:get(i), i)
							end
						end
                        return t
                """.trimIndent(),
				"mapNotNil" to """
                        local o, f = ...
                        local t, j = {}, 1
                        for i=0, o:size()-1 do
                            local v = f(o:get(i))
                            if v then
                                t[j] = v
                                j = j + 1
                            end
                        end
                        return t
                """.trimIndent(),
				"filter" to """
                        local o, f = ...
                        local t, j = {}, 1
                        for i=0, o:size()-1 do
                            local v = o:get(i)
                            if f(v) then
                                t[j] = v
                                j = j + 1
                            end
                        end
                        return t
                """.trimIndent(),
				"map2flat" to """
                        local o1, f1, f2 = ...
                        local t, j = {}, 1
                        for i=0, o1:size()-1 do
                            local o2 = f1(o1:get(i))
                            if o2 then
                                for k=0, o2:size()-1 do
                                    t[j] = f2(o2:get(k))
                                    j = j + 1
                                end
                            end
                        end
                        return t
                """.trimIndent(),
				"first" to """
                        local o, f = ...
                        for i=1, o:size() do
                            local v = o:get(i-1)
                            if f(v) then return v end
                        end
                        return nil
                """.trimIndent(),
				"wrap" to """
                        local o, f = ...
                        return function(...)
                            return f(o, ...)
                        end
                """.trimIndent(),
				"flatten" to """
                        local t = ...
                        local n = {}
                        local i = 1

                        for _,u in pairs(t) do
                            for _,v in pairs(u) do
                                n[i] = v
                                i = i + 1
                            end
                        end

                        return n
                """.trimIndent()
		).map { e -> e.key to load.call(e.value) }.toMap()

		/**
		 * Normally, you'd call functions in java objects with o:F(...), which is actually syntax sugar for o.F(o, ...),
		 * the "wrap" function bypasses this.
		 */
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

	override fun call(modname: LuaValue, env: LuaValue): LuaValue {
		val g: Globals = env.checkglobals()
		g.setmetatable(LuaTable())
		g.getmetatable()["__index"] = __index(g)
		return g
	}
}



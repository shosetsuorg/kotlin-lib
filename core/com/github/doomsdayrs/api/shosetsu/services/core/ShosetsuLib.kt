package com.github.doomsdayrs.api.shosetsu.services.core

import com.github.doomsdayrs.api.shosetsu.services.core.Novel.Status
import okhttp3.*
import org.luaj.vm2.*
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua
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
 * ====================================================================
 */

/**
 * com.github.doomsdayrs.api.shosetsu.extensions.lang.en
 * 20 / January / 2020
 *
 * @author github.com/technojo4
 */
class ShosetsuLib : TwoArgFunction() {
    companion object {
        // TODO Set libraries for this in shosetsu
        // TODO > Request libraries via meta
        // TODO > Retrieve libraries with names
        /**
         * Libraries loaded in via shosetsu.
         * The [String] key is the name of the library
         * If contained, it will return a [LuaValue] of the library
         */
        val libraries: MutableMap<String, LuaValue> = mutableMapOf()
        lateinit var libraryLoaderSync: (name: String) -> LuaValue?
        lateinit var okHttpClient: OkHttpClient
    }


    @Suppress("unused", "PrivatePropertyName", "FunctionName", "MemberVisibilityCanBePrivate")
    internal class LibFunctions {
        fun DEFAULT_CACHE_CONTROL() = CacheControl.Builder().maxAge(10, TimeUnit.MINUTES).build()
        fun DEFAULT_HEADERS() = Headers.Builder().build()
        fun DEFAULT_BODY(): RequestBody = FormBody.Builder().build()

        fun <E> List(): ArrayList<E> = ArrayList()
        fun <E> AsList(arr: Array<E>): ArrayList<E> = ArrayList(arr.asList())
        fun <E> Reverse(arr: ArrayList<E>) = arr.reverse()

        fun Novel() = Novel.Listing()
        fun NovelChapter() = Novel.Chapter()
        fun NovelPage() = Novel.Info()

        /**
         * @param name Name of the library to request
         * @throws LuaError if library not present
         * @return [LuaValue] of the library if it is available, [LuaValue.NIL] otherwise
         */
        fun Require(name: String): LuaValue? = libraries.computeIfAbsent(name) {
            libraryLoaderSync(it).takeIf { value ->
                if (value != LuaValue.NIL || value != null) true else throw LuaError("MISLIB:$it")
            }!!
        }

        /**
         * @param type specified by an ID.[Status.PUBLISHING]=0,[Status.COMPLETED]=1,[Status.PAUSED]=2, else [Status.UNKNOWN]
         * @return [Status]
         */
        fun NovelStatus(type: Int): Status = when (type) {
            0 -> Status.PUBLISHING
            1 -> Status.COMPLETED
            2 -> Status.PAUSED
            else -> Status.UNKNOWN
        }


        // For normal people, these simple GET and POST are sufficient.
        fun GET(url: String, headers: Headers, cacheControl: CacheControl):
                Request = Request.Builder().url(url).headers(headers).cacheControl(cacheControl).build()

        fun POST(url: String, headers: Headers, body: RequestBody, cacheControl: CacheControl):
                Request = Request.Builder().url(url).post(body).headers(headers).cacheControl(cacheControl).build()

        fun getResponse(request: Request) = okHttpClient.newCall(request).execute()

        // For advanced users who want to do everything themselves.
        fun RequestBuilder() = Request.Builder()

        fun HeadersBuilder() = Headers.Builder()
        fun FormBodyBuilder() = FormBody.Builder()
        fun DefaultCacheControl() = CacheControl.Builder()
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
                Pair("map", """
                        local o, f = ...
                        local t = {}
                        for i=1, o:size() do
                            t[i] = f(o:get(i-1))
                        end
                        return t
                """.trimIndent()),
                Pair("mapNotNil", """
                        local o, f = ...
                        local t = {}
                        local b = 1
                        for i=1, o:size() do
                            local v = f(o:get(i-1))
                            if v ~= nil then
                                t[b] = v
                                b = b + 1
                            end
                        end
                        return t
                """.trimIndent()),
                Pair("map2flat", """
                        local o1, f1, f2 = ...
                        local t = {}
                        local i = 1
                        for j = 1, o1:size() do
                            local o2 = f1(o1:get(j - 1))
                            if o2 then
                                for k = 1, o2:size() do
                                    t[i] = f2(o2:get(k - 1))
                                    i = i + 1
                                end
                            end
                        end
                        return t
                """.trimIndent()),
                Pair("first", """
                        local o, f = ...
                        for i=1, o:size() do
                            local v = o:get(i-1)
                            if f(v) then return v end
                        end
                        return nil
                """.trimIndent()),
                Pair("wrap", """
                        local o, f = ...
                        return function(...)
                            return f(o, ...)
                        end
                """.trimIndent())
        ).map { e -> Pair(e.key, load.call(e.value)) }.toMap()

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
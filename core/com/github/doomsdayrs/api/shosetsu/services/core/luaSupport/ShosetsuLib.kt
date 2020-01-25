package com.github.doomsdayrs.api.shosetsu.services.core.luaSupport

import com.github.doomsdayrs.api.shosetsu.services.core.objects.LibraryLoaderSync
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelStatus
import com.github.doomsdayrs.api.shosetsu.services.core.objects.Ordering
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
        var libraryLoaderSync: LibraryLoaderSync = throw Exception("Stub!")
    }


    internal class LibFunctions {
        private val DEFAULT_CACHE_CONTROL = CacheControl.Builder().maxAge(10, TimeUnit.MINUTES).build()
        private val DEFAULT_HEADERS = Headers.Builder().build()
        private val DEFAULT_BODY: RequestBody = FormBody.Builder().build()

        fun <E> List(): ArrayList<E> = ArrayList()
        fun <E> AsList(arr: Array<E>): ArrayList<E> = ArrayList(arr.asList())
        fun <E> Reverse(arr: ArrayList<E>) = arr.reverse()

        fun Novel() = com.github.doomsdayrs.api.shosetsu.services.core.objects.Novel()
        fun NovelChapter() = com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelChapter()
        fun NovelPage() = com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelPage()

        /**
         * @param name Name of the library to request
         * @throws LuaError if library not present
         * @return [LuaValue] of the library if it is available, [LuaValue.NIL] otherwise
         */
        fun Require(name: String): LuaValue? {
            return libraries.computeIfAbsent(name) {
                libraryLoaderSync.getScript(it).takeIf { value ->
                    if (value != LuaValue.NIL || value != null) true else throw LuaError("MISLIB:$it")
                }!!
            }
        }
        //throw LuaError("MISLIB:$name")

        /**
         * @param type specified by an ID.[NovelStatus.PUBLISHING]=0,[NovelStatus.COMPLETED]=1,[NovelStatus.PAUSED]=2, else [NovelStatus.UNKNOWN]
         * @return [NovelStatus]
         */
        fun NovelStatus(type: Int): NovelStatus = when (type) {
            0 -> NovelStatus.PUBLISHING
            1 -> NovelStatus.COMPLETED
            2 -> NovelStatus.PAUSED
            else -> NovelStatus.UNKNOWN
        }

        /**
         * @param type specified by an ID.[Ordering.TopBottomLatestOldest]=0|default,[Ordering.BottomTopLatestOldest]=1
         * @return [Ordering]
         */
        fun Ordering(type: Int): Ordering = when (type) {
            0 -> Ordering.TopBottomLatestOldest
            1 -> Ordering.BottomTopLatestOldest
            else -> Ordering.TopBottomLatestOldest
        }

        fun GET(
                url: String,
                headers: Headers = DEFAULT_HEADERS,
                cacheControl: CacheControl = DEFAULT_CACHE_CONTROL
        ): Request = Request.Builder().url(url).headers(headers).cacheControl(cacheControl).build()

        fun POST(
                url: String,
                headers: Headers = DEFAULT_HEADERS,
                body: RequestBody = DEFAULT_BODY,
                cacheControl: CacheControl = DEFAULT_CACHE_CONTROL
        ): Request = Request.Builder().url(url).post(body).headers(headers).cacheControl(cacheControl).build()

    }

    /**
     * Makes use of lua metatables and its "__index" metamethod, which is called when
     * trying to index a table and a key is not present, to inject our library into the script's Globals.
     */
    internal class __index(g: Globals) : TwoArgFunction() {
        /*
         * Normally, you'd call functions in java objects with o:F(...), which is actually syntax sugar for o.F(lib, ...),
         * the "wrap" function bypasses this.
         */
        private val load: LuaFunction = g["load"] as LuaFunction
        private val wrap: LuaFunction = load.call("local o,f = ...; return function(...) return f(o, ...) end") as LuaFunction
        private val lib: LuaValue = CoerceJavaToLua.coerce(LibFunctions())

        private val luaFuncs: Map<LuaValue, LuaValue> = mapOf(
                Pair("map", """
                        local o, f = ...
                        local t = {}
                        for i=1, o:size() do
                            t[i] = f(o:get(i-1))
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
                """.trimIndent())
        ).map { e -> Pair(LuaValue.valueOf(e.key), load.call(e.value)) }.toMap()

        override fun call(_self: LuaValue, k: LuaValue): LuaValue {
            if (!k.isstring()) return LuaValue.NIL

            val o = lib.get(k.tostring())
            if (o != null && o != LuaValue.NIL)
                return wrap.call(lib, o)

            val f = luaFuncs[k.tostring()]
            if (f != null) return f

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
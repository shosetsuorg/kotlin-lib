package com.github.doomsdayrs.api.shosetsu.services.core.luaSupport

import org.luaj.vm2.*
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua

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
    }

    internal class LibFunctions {
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
        fun Require(name: String): LuaValue? = libraries[name].takeIf {
            if (it == LuaValue.NIL || it == null)
                throw LuaError("MISLIB:$name")
            true
        }

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
        private var wrap: LuaFunction = g["load"].call("local o,f = ...; return function(...) return f(o, ...) end") as LuaFunction
        private var lib: LuaValue = CoerceJavaToLua.coerce(LibFunctions())

        override fun call(_self: LuaValue, k: LuaValue): LuaValue {
            if (!k.isstring()) return LuaValue.NIL

            val o = lib.get(k.tostring())
            if (o != LuaValue.NIL && o != null)
                return wrap.call(lib, o)

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
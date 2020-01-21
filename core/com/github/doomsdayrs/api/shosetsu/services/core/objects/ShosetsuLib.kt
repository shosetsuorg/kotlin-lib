package com.github.doomsdayrs.api.shosetsu.services.core.objects

import org.luaj.vm2.*
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.ZeroArgFunction
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.ThreeArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.CoerceLuaToJava

/**
 * com.github.doomsdayrs.api.shosetsu.extensions.lang.en
 * 20 / January / 2020
 *
 * @author github.com/technojo4
 */
class ShosetsuLib : TwoArgFunction() {
    internal class LibFunctions {
        fun <E>List(): ArrayList<E> = ArrayList()
        fun <E>AsList(arr: Array<E>): ArrayList<E> = ArrayList<E>(arr.asList())
        fun <E>Reverse(arr: ArrayList<E>) = arr.reverse()

        fun Novel() = com.github.doomsdayrs.api.shosetsu.services.core.objects.Novel()
        fun NovelChapter() = com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelChapter()
        fun NovelPage() = com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelPage()

        fun NovelStatus(type: Int): NovelStatus {
            return when (type) {
                0 -> NovelStatus.PUBLISHING
                1 -> NovelStatus.COMPLETED
                2 -> NovelStatus.PAUSED
                else -> NovelStatus.UNKNOWN
            }
        }

        fun Ordering(type: Int): Ordering {
            return when (type) {
                0 -> Ordering.TopBottomLatestOldest
                1 -> Ordering.BottomTopLatestOldest
                else -> Ordering.TopBottomLatestOldest
            }
        }
    }

    internal class __index(var g: Globals) : TwoArgFunction() {
        private var wrap: LuaFunction = g["load"].call("local o,f = ...; return function(...) return f(o, ...) end") as LuaFunction
        private var lib: LuaValue = CoerceJavaToLua.coerce(LibFunctions())

        override fun call(_self: org.luaj.vm2.LuaValue, k: LuaValue): LuaValue {
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
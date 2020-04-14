package app.shosetsu.lib

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform

val SHOSETSU_GLOBALS = mapOf<String, LuaValue>(
		"QUERY" to LuaValue.valueOf(LuaFormatter.FILTER_POSITION_QUERY),
		"FRESH_NOVEL" to LuaValue.valueOf(1),
		"FRESH_CHAPT" to LuaValue.valueOf(2)
)

/**
 * Globals for shosetsu
 */
fun shosetsuGlobals(): Globals {
	val globals = JsePlatform.standardGlobals()
	SHOSETSU_GLOBALS.forEach { (s, luaValue) -> globals.set(s, luaValue) }
	globals.load(ShosetsuLib())
	return globals
}
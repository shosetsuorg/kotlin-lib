package app.shosetsu.lib

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform
import org.luaj.vm2.LuaValue.valueOf as lValueOf

const val QUERY_INDEX: Int = 0
const val FRESH_NOVEL_URL: Int = 1
const val FRESH_CHAPTER_URL: Int = 0

val SHOSETSU_GLOBALS: Map<String, LuaValue> = mapOf<String, LuaValue>(
		"QUERY" to lValueOf(QUERY_INDEX),
		"FRESH_NOVEL" to lValueOf(FRESH_NOVEL_URL),
		"FRESH_CHAPT" to lValueOf(FRESH_CHAPTER_URL)
)

/**
 * Globals for shosetsu
 */
fun shosetsuGlobals(): Globals {
	val globals = JsePlatform.standardGlobals()

	// Applies shosetsu globals on top of the standard ones
	SHOSETSU_GLOBALS.forEach { (s, luaValue) -> globals.set(s, luaValue) }

	// Loads shosetsu support lib
	globals.load(ShosetsuLib())

	return globals
}
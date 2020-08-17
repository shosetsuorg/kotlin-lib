package app.shosetsu.lib

import app.shosetsu.lib.Formatter.Companion.KEY_CHAPTER_URL
import app.shosetsu.lib.Formatter.Companion.KEY_NOVEL_URL
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform
import org.luaj.vm2.LuaValue.valueOf as lValueOf

/**
 * The index all queries are set to when passing data along
 */
const val QUERY_INDEX: Int = 0

/**
 * Global values that are passed into lua with [shosetsuGlobals]
 */
val SHOSETSU_GLOBALS: Map<String, LuaValue> = mapOf<String, LuaValue>(
		"QUERY" to lValueOf(QUERY_INDEX),
		"KEY_CHAPTER_URL" to lValueOf(KEY_CHAPTER_URL),
		"KEY_NOVEL_URL" to lValueOf(KEY_NOVEL_URL)
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
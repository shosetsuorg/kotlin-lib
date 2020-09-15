package app.shosetsu.lib

import app.shosetsu.lib.Formatter.Companion.KEY_CHAPTER_URL
import app.shosetsu.lib.Formatter.Companion.KEY_NOVEL_URL
import org.luaj.vm2.*
import org.luaj.vm2.compiler.LuaC
import org.luaj.vm2.lib.*
import org.luaj.vm2.lib.jse.JseBaseLib
import org.luaj.vm2.lib.jse.JseMathLib
import org.luaj.vm2.lib.jse.JseOsLib
import org.luaj.vm2.lib.jse.LuajavaLib
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

fun LuaTable.frozen(name: String, allowed: Array<String>? = null): LuaTable {
	var tbl = this
	if (allowed != null) {
		val new = LuaTable()
		allowed.forEach { new[it] = tbl[it] }
		tbl = new
	}

	val mt = LuaTable()
	val new = LuaTable()
	mt["__index"] = tbl
	mt["__newindex"] = object : ZeroArgFunction() {
		override fun call(): LuaValue = throw LuaError("$name is read-only.")
	}
	new.setmetatable(mt)

	return new
}

fun Globals.freezeLib(key: String, allowed: Array<String>? = null) = this.set(key, (this.get(key) as? LuaTable)!!.frozen(key, allowed))

fun Globals.frozen(): Globals {
	val mt = LuaTable()
	val new = Globals()
	new["_G"] = new

	// why does LuaJ have all this shit
	new.STDIN = this.STDIN
	new.STDOUT = this.STDOUT
	new.STDERR = this.STDERR
	new.finder = this.finder
	new.running = this.running
	new.baselib = this.baselib
	new.package_ = this.package_
	new.debuglib = this.debuglib
	new.loader = this.loader
	new.undumper = this.undumper
	new.compiler = this.compiler

	mt["__index"] = this
	mt["__newindex"] = object : ZeroArgFunction() {
		// not sure if this is the best idea, but it enforces better code quality/speed and it should reduce risks.
		override fun call(): LuaValue = throw LuaError("Cannot create a global. Please use a local variable/function.")
	}
	new.setmetatable(mt)
	return new
}


fun shosetsuGlobals(): Globals {
	// Creation of globals
	var globals = Globals()
	globals.load(JseBaseLib())
	globals.load(PackageLib())
	globals.load(Bit32Lib())
	globals.load(TableLib())
	globals.load(StringLib())
	globals.load(CoroutineLib())
	globals.load(JseMathLib())
	globals.load(JseOsLib())
	globals.load(LuajavaLib())

	// Install compilers.
	LoadState.install(globals)
	LuaC.install(globals)

	// Load shosetsu environment
	globals.load(ShosetsuLib())
	SHOSETSU_GLOBALS.forEach { (s, luaValue) -> globals.set(s, luaValue) }

	// Freezing & Sandboxing
	globals.freezeLib("package")
	globals.freezeLib("bit32")
	globals.freezeLib("table")
	globals.freezeLib("coroutine")
	globals.freezeLib("math")
	globals.freezeLib("os", arrayOf("clock", "date", "difftime", "setlocale", "time"))
	globals = globals.frozen()

	return globals
}

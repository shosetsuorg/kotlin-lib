package app.shosetsu.lib.js

import org.mozilla.javascript.Context
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.Scriptable

/**
 * 03 / 08 / 2022
 */
fun shosetsuJSGlobals(context: Context): Scriptable {
	val scriptable = context.initSafeStandardObjects()
	scriptable.put("console", scriptable, Context.toObject(JSConsole(), scriptable))
	return scriptable
}

class JSConsole {
	fun log(array: NativeArray) {
		log(array.toArray().contentToString())
	}

	fun log(any: Any) {
		println(any)
	}
}
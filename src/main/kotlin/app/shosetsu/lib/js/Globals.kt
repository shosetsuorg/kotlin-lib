package app.shosetsu.lib.js

import org.mozilla.javascript.Context
import org.mozilla.javascript.ScriptableObject

/**
 * 03 / 08 / 2022
 */
fun shosetsuJSGlobals(context: Context): ScriptableObject {
	return context.initSafeStandardObjects()
}
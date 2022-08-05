package app.shosetsu.lib.js

import org.mozilla.javascript.Scriptable

/**
 * 04 / 08 / 2022
 */

@Throws(NullPointerException::class)
fun Scriptable.getOrThrow(key: String, scope: Scriptable): Any {
	val obj = this.get(key, scope)
	if (obj == Scriptable.NOT_FOUND) {
		throw NullPointerException("`$key` not found in script")
	}
	return obj
}
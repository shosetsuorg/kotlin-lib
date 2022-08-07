package app.shosetsu.lib.js

import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.Scriptable

/*
 * 04 / 08 / 2022
 */

@Throws(NullPointerException::class, IllegalArgumentException::class)
inline fun <reified O> Scriptable.getOrThrow(key: String, scope: Scriptable): O {
	val obj = this.get(key, scope)
	if (obj == Scriptable.NOT_FOUND) {
		throw NullPointerException("`$key` not found in script")
	}
	return Context.jsToJava(obj, O::class.java) as O
}

@Throws(NullPointerException::class, IllegalArgumentException::class)
inline fun <reified O> Scriptable.getArrayOrThrow(key: String, scope: Scriptable): Array<O> {
	val obj = this.get(key, scope)
	if (obj == Scriptable.NOT_FOUND) {
		throw NullPointerException("`$key` not found in script")
	}
	if (obj is NativeArray) {
		return obj.map { Context.jsToJava(it, O::class.java) as O }.toTypedArray()
	}
	throw ClassCastException("Return is not an array, is `${obj::class.simpleName}`")
}

@Throws(NullPointerException::class)
fun Scriptable.callOrThrow(
	context: Context,
	key: String,
	scope: Scriptable,
	vararg arguments: Any?
) {
	val func = this.getOrThrow(key, scope) as Function
	func.call(context, scope, scope, arguments)
}

@Throws(NullPointerException::class)
inline fun <reified O> Scriptable.callOrThrowReturn(
	context: Context,
	key: String,
	scope: Scriptable,
	vararg arguments: Any?
): O? {
	val func = this.getOrThrow(key, scope) as Function
	val result = func.call(context, scope, scope, arguments)

	return Context.jsToJava(result, O::class.java) as O
}

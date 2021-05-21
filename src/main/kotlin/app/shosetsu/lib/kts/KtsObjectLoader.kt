package app.shosetsu.lib.kts

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * This class is not thread-safe, don't use it for parallel executions and create new instances instead.
 */
class KtsObjectLoader(classLoader: ClassLoader? = Thread.currentThread().contextClassLoader) {

	val engine: ScriptEngine = ScriptEngineManager(classLoader).getEngineByExtension("kts")

	@Throws(IllegalArgumentException::class)
	inline fun <reified T> Any?.castOrError(): T = takeIf { it is T }?.let { it as T }
		?: throw IllegalArgumentException("Cannot cast $this to expected type ${T::class}")

	@Throws(RuntimeException::class)
	inline fun <reified T> load(script: String): T =
		kotlin.runCatching { engine.eval(script) }
			.getOrElse @Throws(RuntimeException::class) { throw RuntimeException("Cannot load script", it) }
			.castOrError()
}
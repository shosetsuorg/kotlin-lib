package app.shosetsu.lib

import app.shosetsu.lib.lua.shosetsuGlobals
import org.junit.Test
import kotlin.system.measureTimeMillis

/*
 * shosetsu-kotlin-lib
 * 02 / 11 / 2021
 */
class LuaLibTest {
	@Test
	fun delayTest() {
		val time = 500L
		val script = shosetsuGlobals().load("delay($time)", "delayTest")!!

		val resultTime = measureTimeMillis { script.call() }

		assert(resultTime > time) { "func 'delay' time < ${time}ms" }
	}
}
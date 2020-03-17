package com.github.doomsdayrs.api.shosetsu.services.core

import com.github.doomsdayrs.api.shosetsu.services.core.ShosetsuLib.Companion.FILTER_ID_QUERY
import com.github.doomsdayrs.api.shosetsu.services.core.ShosetsuLib.Companion.defaultMap
import okhttp3.OkHttpClient
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File
import java.util.concurrent.TimeUnit.MILLISECONDS

/*
 * This file is part of shosetsu-services.
 * shosetsu-services is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * shosetsu-services is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with shosetsu-services.  If not, see https://www.gnu.org/licenses/.
 * ====================================================================
 */
/**
 * shosetsu-services
 * 03 / June / 2019
 *
 * @author github.com/doomsdayrs;github.com/TechnoJo4
 *
 * In IDEA, The Classpath should be shosetsu-services but the Working directory should be shosetsu-extensions.
 */
private object Test {
	// CONFIG
	private const val SEARCH_VALUE = "world"
	private const val PRINT_LISTINGS = false
	private const val PRINT_LIST_STATS = true
	private const val PRINT_NOVELS = false
	private const val PRINT_NOVEL_STATS = true
	private const val PRINT_PASSAGES = false

	private val SOURCES = arrayOf(
			"en/VipNovel"
	).map { "src/main/resources/src/$it.lua" }

	private val REPORTER: (String) -> Unit = { println("Progress: $it") }
	// END CONFIG


	private fun loadScript(file: File): LuaValue {
		val script = JsePlatform.standardGlobals()
		script.load(ShosetsuLib())
		script.set("QUERY", FILTER_ID_QUERY)
		val l = try {
			script.load(file.readText())!!
		} catch (e: Error) {
			throw e
		}

		return l.call()!!
	}

	private fun printMap(map: MutableMap<Int, Any?>) {
		with(map) {
			forEach { i: Int, any: Any? ->
				println("$i->$any")
				any?.let {
					println(it::class.java)
					if (it::class.java.equals(LinkedHashMap::class.java))
						printMap(it as MutableMap<Int, Any?>)
				}
			}
		}
	}

	@Suppress("ConstantConditionIf")
	private fun showListing(fmt: Formatter, novels: Array<Novel.Listing>) {
		if (PRINT_LISTINGS)
			println("[" + novels.joinToString(", ") { it.toString() } + "]")

		println("${novels.size} novels.")
		if (PRINT_LIST_STATS)
			println("${novels.count { it.title == "" }} with no title, ${novels.count { it.link == "" }} with no link, ${novels.count { it.imageURL == "" }} with no image url.")

		println()

		val novel = fmt.parseNovel(novels[0].link, true, REPORTER)
		if (PRINT_NOVELS) println(novel)
		if (PRINT_NOVEL_STATS) println("${novel.title} - ${novel.chapters.size} chapters.")

		println()

		val passage = fmt.getPassage(novel.chapters[0].link)
		if (PRINT_PASSAGES)
			println(passage)
		else
			println(with(passage) {
				if (length < 25) "Result: $this"
				else "$length chars long result: ${take(10)} [...] ${takeLast(10)}"
			})
	}

	@Throws(java.io.IOException::class, InterruptedException::class)
	@JvmStatic
	fun main(args: Array<String>) {
		try {
			ShosetsuLib.libLoader = { loadScript(File("src/main/resources/lib/$it.lua")) }
			ShosetsuLib.httpClient = OkHttpClient()

			for (format in SOURCES) {
				println("\n\n========== $format ==========")

				val formatter = LuaFormatter(File(format))
				val map: MutableMap<Int, Any?> = mutableMapOf()

				println("ID       : ${formatter.formatterID}")
				println("Name     : ${formatter.name}")
				println("Image    : ${formatter.imageURL}")
				println("Settings : ${formatter.settings}")
				println("Filters  : ${formatter.filters}")
				map.putAll(formatter.filters.defaultMap())
				printMap(map)

				formatter.listings.forEach { l ->
					with(l) {
						println("\n-------- Listing \"${name}\" ${if (isIncrementing) "(incrementing)" else ""} --------")
						var novels = getListing(map, if (isIncrementing) 1 else null)
						if (isIncrementing) novels += getListing(map, 2)
						showListing(formatter, novels)
						MILLISECONDS.sleep(500)
					}
				}

				if (formatter.hasSearch) {
					println("\n-------- Search --------")
					map[FILTER_ID_QUERY] = SEARCH_VALUE
					showListing(formatter, formatter.search(map, REPORTER))
				}

				MILLISECONDS.sleep(500)
			}
			println("\n\tTESTS COMPLETE")
		} catch (e: Exception) {
			e.printStackTrace()
			e.message?.let {
				print("\n\u001B[31m${it.substring(it.lastIndexOf("}") + 1)}\n")
			}
		}
	}

}
package com.github.doomsdayrs.api.shosetsu.services.core

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
            "en/MTLNovel"
    ).map { "src/main/resources/src/$it.lua" }

    private val REPORTER: (String) -> Unit = { println("Progress: $it") }
    // END CONFIG


    private fun loadScript(file: File): LuaValue {
        val script = JsePlatform.standardGlobals()
        script.load(ShosetsuLib())
        val l = try {
            script.load(file.readText())!!
        } catch (e: Error) {
            throw e
        }

        return l.call()!!
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

    fun defaultMapFromFilters(filters: Array<Filter<*>>): Map<Int, Any?> {
        val m = mutableMapOf<Int, Any?>()
        filters.forEach {
            m[it.id] = when (it) {
                is TextFilter -> ""
                is SwitchFilter -> true
                is DropdownFilter -> 0
                is RadioGroupFilter -> 0
                else -> null
            }
        }
        return m
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

                println("ID    : ${formatter.formatterID}")
                println("Name  : ${formatter.name}")
                println("Image : ${formatter.imageURL}")

                formatter.listings.forEach { l ->
                    with(l) {

                        println("\n-------- Listing \"${name}\" ${if (isIncrementing) "(incrementing)" else ""} --------")
                        var novels = getListing(if (isIncrementing) 1 else null, defaultMapFromFilters(l.filters))
                        if (isIncrementing) novels += getListing(2, defaultMapFromFilters(l.filters))
                        showListing(formatter, novels)
                        MILLISECONDS.sleep(500)
                    }
                }

                if (formatter.hasSearch) {
                    println("\n-------- Search --------")
                    showListing(formatter, formatter.search(mapOf(0 to SEARCH_VALUE), REPORTER))
                }

                MILLISECONDS.sleep(500)
            }
            println("\n\tTESTS COMPLETE")
        } catch (e: Exception) {
            e.message?.let {
                print("\n\u001B[31m${it.substring(it.lastIndexOf("}") + 1)}\n")
            }
        }
    }

}
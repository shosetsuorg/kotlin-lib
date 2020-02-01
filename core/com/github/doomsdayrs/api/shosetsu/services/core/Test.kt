package com.github.doomsdayrs.api.shosetsu.services.core

import okhttp3.OkHttpClient
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File
import java.util.concurrent.TimeUnit.SECONDS

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
 * @author github.com/doomsdayrs
 *
 * In IDEA, The Classpath should be shosetsu-services but the Working directory should be shosetsu-extensions.
 */
private object Test {
    // CONFIG
    private const val PRINT_LISTINGS = false
    private const val PRINT_LIST_STATS = true
    private const val PRINT_NOVELS = false
    private const val PRINT_NOVEL_STATS = true
    private val SOURCES = arrayOf(
            "en/BestLightNovel", "vi/247Truyen"
    ).map { "src/main/resources/src/$it.lua" }


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

    @Throws(java.io.IOException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        ShosetsuLib.libLoader = { loadScript(File("src/main/resources/lib/$it.lua")) }
        ShosetsuLib.httpClient = OkHttpClient()

        for (format in SOURCES) {
            println("\n\n========== $format ==========")

            val formatter = LuaFormatter(File(format))
            println("ID    : ${formatter.formatterID}")
            println("Name  : ${formatter.name}")
            println("Image : ${formatter.imageURL}")

            @Suppress("ConstantConditionIf")
            formatter.listings.forEach { l ->
                with(l) {
                    println("\n-------- Listing \"${name}\" ${if (isIncrementing) "(incrementing)" else ""} --------")
                    val novels = getListing(if (isIncrementing) 1 else null)
                    if (PRINT_LISTINGS) println(novels)

                    println("${novels.size} novels.")
                    if (PRINT_LIST_STATS)
                        println("${novels.count { it.title == "" }} with no title, ${novels.count { it.link == "" }} with no link, ${novels.count { it.imageURL == "" }} with no image url.")

                    println()

                    val novel = formatter.parseNovel(novels[0].link, true)
                    if (PRINT_NOVELS) println(novel)
                    if (PRINT_NOVEL_STATS) println("${novel.title} - ${novel.chapters.size} chapters.")

                    println(run {
                        val it = formatter.getPassage(novel.chapters[0].link)
                        if (it.length < 25) "Result: $it"
                        else "${it.length} chars long result: ${it.take(10)} [...] ${it.takeLast(10)}"
                    })
                    SECONDS.sleep(1)
                }
            }

            SECONDS.sleep(1)
        }
        println("\n\tTESTS COMPLETE")
    }

}
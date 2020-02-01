package com.github.doomsdayrs.api.shosetsu.services.core
import okhttp3.*
import okhttp3.OkHttpClient
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File

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
 * In IDEA, The Classpath should be shosetsu-services BUT the Working directory should be shosetsu-extensions.
 */
private object Test {
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

        // TODO: Reimplement when 1.0 services AND extensions are complete.
        for (format in arrayOf(
                "src/main/resources/src/en/NovelFull.lua"
        )) {
            println("\n========== $format ==========")

            val luaFormatter = LuaFormatter(File(format))

            // Data
            println(luaFormatter.name)
            println(luaFormatter.formatterID)
            println(luaFormatter.imageURL)

            // Parse novel passage
            java.util.concurrent.TimeUnit.SECONDS.sleep(1)
            //println(run {
            //    val it = luaFormatter.getPassage(novel.novelChapters[0].link)
            //    if (it.length < 25) "Result: $it"
            //    else "${it.length} chars long result: ${it.take(10)} [...] ${it.takeLast(10)}"
            //})
            println()

            println("DEBUG COMPLETE")
        }
    }

}
package com.github.doomsdayrs.api.shosetsu.services.core

import com.github.doomsdayrs.api.shosetsu.services.core.dep.LuaFormatter
import com.github.doomsdayrs.api.shosetsu.services.core.luaSupport.*
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaError
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File


/*
 * This file is part of shosetsu-extensions.
 * shosetsu-extensions is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * shosetsu-extensions is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with shosetsu-extensions.  If not, see https://www.gnu.org/licenses/.
 * ====================================================================
 */
/**
 * novelreader-extensions
 * 03 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
internal class Test {

    companion object {
        // The below is methods robbed from ScrapeFormat class
        private val builder: okhttp3.Request.Builder = okhttp3.Request.Builder()
        private val client: okhttp3.OkHttpClient = okhttp3.OkHttpClient()

        @Throws(java.io.IOException::class)
        private fun request(url: String?): okhttp3.ResponseBody? {
            println(url)
            val u = java.net.URL(url)
            val request = builder.url(u).build()
            return client.newCall(request).execute().body
        }

        @Throws(java.io.IOException::class)
        private fun docFromURL(URL: String): org.jsoup.nodes.Document {
            return org.jsoup.Jsoup.parse(request(URL)!!.string())
        }


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
            for (lib in arrayOf(
                "Madara"
            )) ShosetsuLib.libraries[lib] = loadScript(File("src/main/resources/lib/$lib.lua"))

            testScripts()
        }

        private fun testScripts() {
            for (format in arrayOf(
                    "src/main/resources/src/ReadNovelForLife.lua"
            )) {
                println("========== $format ==========")

                val luaFormatter = LuaFormatter(File(format))

                // Data
                println(luaFormatter.genres)
                println(luaFormatter.name)
                println(luaFormatter.formatterID)
                println(luaFormatter.imageURL)

                // Latest
                java.util.concurrent.TimeUnit.SECONDS.sleep(1)
                val list = luaFormatter.parseLatest(docFromURL(luaFormatter.getLatestURL(1)))
                println(list)
                println()

                // Search
                java.util.concurrent.TimeUnit.SECONDS.sleep(1)
                println(luaFormatter.parseSearch(docFromURL(luaFormatter.getSearchString("reinca"))))
                println()

                // Novel
                java.util.concurrent.TimeUnit.SECONDS.sleep(1)
                val novel = luaFormatter.parseNovel(docFromURL(luaFormatter.novelPageCombiner(list[0].link, 2)), 2)
                println(novel)

                // Parse novel passage
                java.util.concurrent.TimeUnit.SECONDS.sleep(1)
                println(luaFormatter.getNovelPassage(docFromURL(novel.novelChapters[0].link)))
                println()

                println("DEBUG")
            }
        }

    }
}
package com.github.doomsdayrs.api.shosetsu.services.core

import com.github.doomsdayrs.api.shosetsu.services.core.dep.LuaFormatter
import com.github.doomsdayrs.api.shosetsu.services.core.luaSupport.ShosetsuLib
import com.github.doomsdayrs.api.shosetsu.services.core.objects.LibraryLoaderSync
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
 * novelreader-extensions
 * 03 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
private object Test {
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

    /**
     * Get's file from resources
     */
    fun getFile(file: String): File {
        return File(javaClass.classLoader.getResource(file)!!.file)
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
        ShosetsuLib.libraryLoaderSync = object : LibraryLoaderSync {
            override fun getScript(name: String): LuaValue? = loadScript(File("src/main/resources/lib/$name.lua"))
        }
        testScripts()
    }


    private fun testScripts() {
        for (format in arrayOf(
                //     "src/main/resources/src/jp/Syosetsu.lua",
                //     "src/main/resources/src/en/BestLightNovel.lua",
                //     "src/main/resources/src/en/BoxNovel.lua",
                //     "src/main/resources/src/en/Foxaholic.lua",
                //     "src/main/resources/src/en/NovelFull.lua",
                //     "src/main/resources/src/en/ReadNovelForLife.lua",
                //     "src/main/resources/src/en/VipNovel.lua",
                "src/main/resources/src/en/KissLightNovels.lua",
                "src/main/resources/src/vi/247truyen.lua"
        )) {
            println("\n========== $format ==========")

            val luaFormatter = LuaFormatter(File(format))

            // Data
            val ge = luaFormatter.genres
            for (g in ge) println(g.toString())

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
            println(run {
                val it = luaFormatter.getNovelPassage(docFromURL(novel.novelChapters[0].link))
                if (it.length < 25) "Result: $it"
                else "${it.length} chars long result: ${it.take(10)} [...] ${it.takeLast(10)}"
            })
            println()

            println("DEBUG COMPLETE")
        }
    }

}
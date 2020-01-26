package com.github.doomsdayrs.api.shosetsu.services.core.dep

import com.github.doomsdayrs.api.shosetsu.services.core.luaSupport.ShosetsuLib
import com.github.doomsdayrs.api.shosetsu.services.core.objects.Novel
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelGenre
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelPage
import com.github.doomsdayrs.api.shosetsu.services.core.objects.Ordering
import org.json.JSONObject
import org.jsoup.nodes.Document
import org.luaj.vm2.LuaString.EMPTYSTRING
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.LuaValue.*
import org.luaj.vm2.lib.jse.CoerceJavaToLua.coerce
import org.luaj.vm2.lib.jse.CoerceLuaToJava
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

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
 * shosetsu-extensions
 * 16 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class LuaFormatter(val file: File) : Formatter {
    companion object {
        val defaults: Map<String, LuaValue> = mapOf(
                Pair("genres", LuaTable()),
                Pair("imageURL", EMPTYSTRING),

                Pair("hasCloudFlare", FALSE),
                Pair("isIncrementingChapterList", FALSE),
                Pair("isIncrementingPassagePage", FALSE),
                Pair("hasSearch", TRUE),
                Pair("hasGenres", TRUE),

                Pair("latestOrder", coerce(Ordering.TopBottomLatestOldest)),
                Pair("chapterOrder", coerce(Ordering.TopBottomLatestOldest))
        )

        val keys: Map<String, Int> = mapOf(
                Pair("id", TNUMBER),
                Pair("name", TSTRING),

                Pair("getLatestURL", TFUNCTION),
                Pair("getNovelPassage", TFUNCTION),
                Pair("parseNovel", TFUNCTION),
                Pair("parseNovelI", TFUNCTION),
                Pair("novelPageCombiner", TFUNCTION),
                Pair("parseLatest", TFUNCTION),
                Pair("parseSearch", TFUNCTION),
                Pair("getSearchString", TFUNCTION)
        )
    }

    fun getMetaData(): JSONObject? {
        return try {
            BufferedReader(FileReader(file)).use { br ->
                val line: String? = br.readLine()
                br.close()
                if (line != null) JSONObject(line.toString().replace("-- ", "")) else null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun loadScript(file: File): LuaTable {
        val script = JsePlatform.standardGlobals()
        script.load(ShosetsuLib())
        val l = try {
            script.load(file.readText())!!
        } catch (e: Error) {
            throw e
        }

        return l.call() as LuaTable
    }

    private val source: LuaTable = loadScript(file)

    init {
        val missingValues: ArrayList<String> = ArrayList()

        for (it in keys)
            if (source.get(it.key).type() != it.value)
                missingValues.add(it.key)

        for (it in defaults)
            if (source.get(it.key).isnil())
                source.set(it.key, it.value)

        if (missingValues.size > 0)
            throw NullPointerException("Lua Script is missing methods:$missingValues")
    }

    override val formatterID: Int
        get() = source["id"].toint()

    override var isIncrementingChapterList: Boolean
        get() = source["isIncrementingChapterList"].toboolean()
        set(@Suppress("UNUSED_PARAMETER") value) {}

    override var isIncrementingPassagePage: Boolean
        get() = source["isIncrementingPassagePage"].toboolean()
        set(@Suppress("UNUSED_PARAMETER") value) {}

    override var chapterOrder: Ordering
        get() = CoerceLuaToJava.coerce(source["chapterOrder"], Ordering::class.java) as Ordering
        set(@Suppress("UNUSED_PARAMETER") value) {}

    override var latestOrder: Ordering
        get() = CoerceLuaToJava.coerce(source["latestOrder"], Ordering::class.java) as Ordering
        set(@Suppress("UNUSED_PARAMETER") value) {}

    override val hasCloudFlare: Boolean
        get() = source["hasCloudFlare"].toboolean()

    override val hasSearch: Boolean
        get() = source["hasSearch"].toboolean()

    override val hasGenres: Boolean
        get() = source["hasGenres"].toboolean()


    @Suppress("UNCHECKED_CAST")
    override val genres: Array<NovelGenre>
        get() = CoerceLuaToJava.coerce(source["genres"], Array<NovelGenre>::class.java) as Array<NovelGenre>

    override val imageURL: String
        get() = source["imageURL"].toString()

    override val name: String
        get() = source["name"].toString()


    override fun getLatestURL(page: Int): String {
        return source["getLatestURL"].call(valueOf(page)).toString()
    }

    override fun getNovelPassage(document: Document): String {
        return source["getNovelPassage"].call(coerce(document)).toString()
    }

    override fun getSearchString(query: String): String {
        return source["getSearchString"].call(query).toString()
    }

    override fun novelPageCombiner(url: String, increment: Int): String {
        val out = source["novelPageCombiner"].call(valueOf(url), valueOf(increment))
        return out.toString()
    }

    override fun parseLatest(document: Document): List<Novel> {
        val out = source["parseLatest"].call(coerce(document))
        @Suppress("UNCHECKED_CAST")
        return CoerceLuaToJava.coerce(out, ArrayList::class.java) as ArrayList<Novel>
    }

    override fun parseNovel(document: Document): NovelPage {
        val out = source["parseNovel"].call(coerce(document))
        return CoerceLuaToJava.coerce(out, NovelPage::class.java) as NovelPage
    }

    override fun parseNovel(document: Document, increment: Int): NovelPage {
        val out = source["parseNovelI"].call(coerce(document), valueOf(increment))
        return CoerceLuaToJava.coerce(out, NovelPage::class.java) as NovelPage
    }

    override fun parseSearch(document: Document): List<Novel> {
        val out = source["parseSearch"].call(coerce(document))
        @Suppress("UNCHECKED_CAST")
        return CoerceLuaToJava.coerce(out, ArrayList::class.java) as ArrayList<Novel>
    }
}
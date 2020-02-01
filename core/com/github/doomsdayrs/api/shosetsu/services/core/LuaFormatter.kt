package com.github.doomsdayrs.api.shosetsu.services.core

import org.json.JSONObject
import org.luaj.vm2.LuaString.EMPTYSTRING
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.LuaValue.*
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
class LuaFormatter(private val file: File) : Formatter {
    companion object {
        val defaults: Map<String, LuaValue> = mapOf(
                Pair("imageURL", EMPTYSTRING),
                Pair("hasCloudFlare", FALSE),
                Pair("hasSearch", TRUE)
        )

        val keys: Map<String, Int> = mapOf(
                Pair("id", TNUMBER),
                Pair("name", TSTRING),
                Pair("imageURL", TSTRING),

                Pair("listings", TTABLE),

                Pair("getPassage", TFUNCTION),
                Pair("parseNovel", TFUNCTION),
                Pair("parseNovelI", TFUNCTION),
                Pair("search", TFUNCTION)
        )
    }

    fun getMetaData(): JSONObject? = try {
        JSONObject(BufferedReader(FileReader(file)).use { br ->
            val line: String? = br.readLine(); br.close(); line
        }?.dropWhile { it != '{' })
    } catch (e: IOException) {
        e.printStackTrace(); null
    }

    private val source: LuaTable

    init {
        val script = JsePlatform.standardGlobals()
        script.load(ShosetsuLib())
        val l = try {
            script.load(file.readText())!!
        } catch (e: Error) { throw e }
        source = l.call() as LuaTable

        // Checks table
        defaults.filter { source[it.key].isnil() }.forEach { source[it.key] = it.value }
        with (keys.filter { source.get(it.key).type() != it.value }.map { it.key }) {
            if (isNotEmpty())
                throw NullPointerException("Lua Script is missing methods:" + fold("", { a, s -> "$a\n\t\t$s;" }))
        }
    }

    override val name: String = source["name"].toString()
    override val formatterID: Int = source["id"].toint()
    override val imageURL: String = source["imageURL"].toString()
    override val hasCloudFlare: Boolean = source["hasCloudFlare"].toboolean()
    override val hasSearch: Boolean = source["hasSearch"].toboolean()

    @Suppress("UNCHECKED_CAST")
    override val listings: Array<Formatter.Listing>
            = CoerceLuaToJava.coerce(source["listings"], Array<Formatter.Listing>::class.java) as Array<Formatter.Listing>

    override fun getPassage(chapterURL: String): String
            = source["getPassage"].call(chapterURL).tojstring()
    override fun parseNovel(novelURL: String): Novel.Info
            = CoerceLuaToJava.coerce(source["parseNovel"].call(valueOf(novelURL)), Novel.Info::class.java) as Novel.Info
    override fun parseNovel(novelURL: String, increment: Int): Novel.Info
            = CoerceLuaToJava.coerce(source["parseNovelI"].call(valueOf(novelURL), valueOf(increment)), Novel.Info::class.java) as Novel.Info

    @Suppress("UNCHECKED_CAST")
    override fun search(data: LuaTable): Array<Novel.Listing>
            = CoerceLuaToJava.coerce(source["search"].call(data), Array<Novel.Listing>::class.java) as Array<Novel.Listing>
}
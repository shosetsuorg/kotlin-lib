package com.github.doomsdayrs.api.shosetsu.services.core.dep

import com.github.doomsdayrs.api.shosetsu.services.core.luaSupport.*
import com.github.doomsdayrs.api.shosetsu.services.core.objects.*
import org.jsoup.nodes.Document
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue.*
import org.luaj.vm2.lib.jse.CoerceJavaToLua.coerce
import org.luaj.vm2.lib.jse.CoerceLuaToJava
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.File

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
        val keys: Map<String, Int> = mapOf(
                Pair("id", TNUMBER),
                Pair("name", TSTRING),
                Pair("imageURL", TSTRING),

                Pair("genres", TTABLE),

                Pair("hasCloudFlare", TBOOLEAN),
                Pair("isIncrementingChapterList", TBOOLEAN),
                Pair("isIncrementingPassagePage", TBOOLEAN),
                Pair("hasSearch", TBOOLEAN),
                Pair("hasGenres", TBOOLEAN),

                Pair("latestOrder", TUSERDATA),
                Pair("chapterOrder", TUSERDATA),

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

    private val luaObject: LuaTable = loadScript(file)

    init {
        val missings: ArrayList<String> = ArrayList()

        for (it in keys)
            if (luaObject.get(it.key).type() != it.value)
                missings.add(it.key)

        if (missings.size > 0)
            throw NullPointerException("Lua Script is missing methods:$missings")
    }

    override val formatterID: Int
        get() = luaObject["id"].toint()

    override var isIncrementingChapterList: Boolean
        get() = luaObject["isIncrementingChapterList"].toboolean()
        set(value) {}

    override var isIncrementingPassagePage: Boolean
        get() = luaObject["isIncrementingPassagePage"].toboolean()
        set(value) {}

    override var chapterOrder: Ordering
        get() = CoerceLuaToJava.coerce(luaObject["chapterOrder"], Ordering::class.java) as Ordering
        set(value) {}

    override var latestOrder: Ordering
        get() = CoerceLuaToJava.coerce(luaObject["latestOrder"], Ordering::class.java) as Ordering
        set(value) {}

    override val hasCloudFlare: Boolean
        get() = luaObject["hasCloudFlare"].toboolean()

    override val hasSearch: Boolean
        get() = luaObject["hasSearch"].toboolean()

    override val hasGenres: Boolean
        get() = luaObject["hasGenres"].toboolean()


    override val genres: Array<NovelGenre>
        get() = CoerceLuaToJava.coerce(luaObject["genres"], Array<NovelGenre>::class.java) as Array<NovelGenre>

    override val imageURL: String
        get() = luaObject["imageURL"].toString()

    override val name: String
        get() = luaObject["name"].toString()


    override fun getLatestURL(page: Int): String {
        return luaObject["getLatestURL"].call(valueOf(page)).toString()
    }

    override fun getNovelPassage(document: Document): String {
        return luaObject["getNovelPassage"].call(coerce(document)).toString()
    }

    override fun getSearchString(query: String): String {
        return luaObject["getSearchString"].call(query).toString()
    }

    override fun novelPageCombiner(url: String, increment: Int): String {
        val out = luaObject["novelPageCombiner"].call(valueOf(url), valueOf(increment))
        return out.toString()
    }

    override fun parseLatest(document: Document): List<Novel> {
        val out = luaObject["parseLatest"].call(coerce(document))
        return CoerceLuaToJava.coerce(out, ArrayList::class.java) as ArrayList<Novel>
    }

    override fun parseNovel(document: Document): NovelPage {
        val out = luaObject["parseNovel"].call(coerce(document))
        return CoerceLuaToJava.coerce(out, NovelPage::class.java) as NovelPage
    }

    override fun parseNovel(document: Document, increment: Int): NovelPage {
        val out = luaObject["parseNovelI"].call(coerce(document), valueOf(increment))
        return CoerceLuaToJava.coerce(out, NovelPage::class.java) as NovelPage
    }

    override fun parseSearch(document: Document): List<Novel> {
        val out = luaObject["parseSearch"].call(coerce(document))
        return CoerceLuaToJava.coerce(out, ArrayList::class.java) as ArrayList<Novel>
    }
}
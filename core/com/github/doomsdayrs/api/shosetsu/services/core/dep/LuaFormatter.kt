package com.github.doomsdayrs.api.shosetsu.services.core.dep

import com.github.doomsdayrs.api.shosetsu.services.core.objects.*
import org.json.JSONObject
import org.jsoup.nodes.Document
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua.coerce
import org.luaj.vm2.lib.jse.CoerceLuaToJava
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

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
        val keys: Array<String> = arrayOf(
                "isIncrementingChapterList",
                "isIncrementingPassagePage",
                "chapterOrder",
                "latestOrder",
                "hasCloudFlare",
                "hasSearch",
                "hasGenres",
                "genres",
                "getImageURL",
                "getName",
                "getLatestURL",
                "getNovelPassage",
                "getSearchString",
                "novelPageCombiner",
                "parseLatest",
                "parseNovel",
                "parseSearch",
                "getID"
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

    fun getScriptFromSystem(path: String): LuaValue {
        val script: Globals = JsePlatform.standardGlobals()
        script.load(ShosetsuLib())
        script["dofile"].call(LuaValue.valueOf(path))
        script.STDOUT = System.out
        return script
    }

    val luaObject: LuaValue = getScriptFromSystem(file.absolutePath);

    init {
        //TODO Check if script has a function
        val missings: ArrayList<String> = ArrayList()
        for (key in keys) {
            if (!luaObject.get(key).isfunction()) {
                missings.add(key)
            }
        }
        if (missings.size > 0)
            throw NullPointerException("Lua Script is missing methods:$missings")
    }

    override val formatterID: Int
        get() = luaObject.get("getID").call().toint()

    override var isIncrementingChapterList: Boolean
        get() = luaObject.get("isIncrementingChapterList").call().toboolean()
        set(value) {}

    override var isIncrementingPassagePage: Boolean
        get() = luaObject.get("isIncrementingPassagePage").call().toboolean()
        set(value) {}

    override var chapterOrder: Ordering
        get() = CoerceLuaToJava.coerce(luaObject.get("chapterOrder").call(), Ordering::class.java) as Ordering
        set(value) {}

    override var latestOrder: Ordering
        get() = CoerceLuaToJava.coerce(luaObject.get("latestOrder").call(), Ordering::class.java) as Ordering
        set(value) {}

    override val hasCloudFlare: Boolean
        get() = luaObject.get("hasCloudFlare").call().toboolean()

    override val hasSearch: Boolean
        get() = luaObject.get("hasSearch").call().toboolean()

    override val hasGenres: Boolean
        get() = luaObject.get("hasGenres").call().toboolean()


    override val genres: Array<NovelGenre>
        get() = CoerceLuaToJava.coerce(luaObject.get("genres").call(), Array<NovelGenre>::class.java) as Array<NovelGenre>

    override val imageURL: String
        get() = luaObject.get("getImageURL").call().toString()

    override val name: String
        get() = luaObject.get("getName").call().toString()


    override fun getLatestURL(page: Int): String {
        return luaObject.get("getLatestURL").call(LuaValue.valueOf(page)).toString()
    }

    override fun getNovelPassage(document: Document): String {
        return luaObject.get("getNovelPassage").call(coerce(document)).toString()
    }

    override fun getSearchString(query: String): String {
        return luaObject.get("getSearchString").call(query).toString()
    }

    override fun novelPageCombiner(url: String, increment: Int): String {
        val out = luaObject.get("novelPageCombiner").call(LuaValue.valueOf(url), LuaValue.valueOf(increment))
        return out.toString()
    }

    override fun parseLatest(document: Document): List<Novel> {
        val out = luaObject.get("parseLatest").call(coerce(document))
        return CoerceLuaToJava.coerce(out, ArrayList::class.java) as ArrayList<Novel>
    }

    override fun parseNovel(document: Document): NovelPage {
        val out = luaObject.get("parseNovel").call(coerce(document))
        return CoerceLuaToJava.coerce(out, NovelPage::class.java) as NovelPage
    }

    override fun parseNovel(document: Document, increment: Int): NovelPage {
        val out = luaObject.get("parseNovelI").call(coerce(document), LuaValue.valueOf(increment))
        return CoerceLuaToJava.coerce(out, NovelPage::class.java) as NovelPage
    }

    override fun parseSearch(document: Document): List<Novel> {
        val out = luaObject.get("parseSearch").call(coerce(document))
        return CoerceLuaToJava.coerce(out, ArrayList::class.java) as ArrayList<Novel>
    }
}
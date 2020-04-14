package app.shosetsu.lib

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

import org.json.JSONObject
import org.luaj.vm2.LuaString.EMPTYSTRING
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.LuaValue.*
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua.coerce
import org.luaj.vm2.lib.jse.CoerceLuaToJava
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException


/**
 * shosetsu-extensions
 * 16 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class LuaFormatter(private val file: File) : Formatter {
	companion object {
		/**
		 * Values that may not be present
		 */
		val defaults: Map<String, LuaValue> = mapOf(
				"imageURL" to EMPTYSTRING,
				"hasCloudFlare" to FALSE,
				"hasSearch" to TRUE,
				"searchFilters" to LuaTable(),
				"settings" to LuaTable()
		)

		/**
		 * Values that must be present
		 */
		val hardKeys: Map<String, Int> = mapOf(
				"id" to TNUMBER,
				"name" to TSTRING,

				"listings" to TTABLE,
				"searchFilters" to TTABLE,

				"getPassage" to TFUNCTION,
				"parseNovel" to TFUNCTION
		)

		/**
		 * What is unique about soft keys is they are conditional on the soft key beforehand
		 * IE, if hasSearch is false, then search does not need to be present in script
		 */
		val softKeys: Map<String, Pair<Pair<String, Int>, (LuaValue) -> Boolean>> = mapOf(
				"hasSearch" to Pair(Pair("search", TFUNCTION), { (it == TRUE) }),
				"settings" to Pair(Pair("updateSetting", TFUNCTION), { (it as LuaTable).length() != 0 })
		)

		const val FILTER_POSITION_QUERY = 0

		private fun makeLuaReporter(f: (status: String) -> Unit) = object : OneArgFunction() {
			override fun call(p0: LuaValue?): LuaValue {
				f(p0!!.tojstring())
				return LuaValue.NIL
			}
		}

		private fun tableToFilters(table: LuaTable): Array<Filter<*>> =
				table.keys().map { table[it] }.filter { !it.isnil() }
						.map { CoerceLuaToJava.coerce(it, Any::class.java) as Filter<*> }.toTypedArray()

		fun Array<*>.toLua(): LuaTable {
			val t = LuaTable()
			this.map { coerce(it) }.forEachIndexed { i, v -> t[i] = v }
			return t
		}

		fun Array<*>.toLua(oneIndex: Boolean): LuaTable {
			val t = LuaTable()
			this.map { coerce(it) }.forEachIndexed { i, v -> t[if (oneIndex) i + 1 else i] = v }
			return t
		}

	}

	@Suppress("unused")
	fun getMetaData(): JSONObject? = try {
		JSONObject(BufferedReader(FileReader(file)).use { it.readLine() }?.dropWhile { it != '{' })
	} catch (e: IOException) {
		e.printStackTrace(); null
	}

	private val source: LuaTable

	init {
		val globals = shosetsuGlobals()
		val l = try {
			globals.load(file.readText())!!
		} catch (e: Error) {
			throw e
		}
		source = l.call() as LuaTable

		// If the modified default value doesnt exist, it assigns the default
		defaults.filter { source[it.key].isnil() }.forEach { source[it.key] = it.value }

		// If any required value is not found, it throws an NullPointerException
		with(hardKeys.filter { source.get(it.key).type() != it.value }.map { it.key }) {
			if (isNotEmpty())
				throw NullPointerException(
						"LuaScript has missing or invalid:" + fold("", { a, s -> "$a\n\t\t$s;" })
				)
		}

		// If any of the softKeys matching their condition of requirement are not found, it throws an NullPointerException
		with(softKeys.filter {
			val t = it.value.first
			if (it.value.second(source[it.key])) {
				source.get(t.first).type() != t.second
			} else false
		}.map { it.value.first.first }) {
			if (isNotEmpty())
				throw NullPointerException(
						"LuaScript has missing or invalid:" + fold("", { a, s -> "$a\n\t\t$s;" })
				)
		}

	}

	override val name: String by lazy { source["name"].tojstring() }
	override val baseURL: String by lazy { source["baseURL"].tojstring() }
	override val formatterID by lazy { source["id"].toint() }
	override val imageURL: String by lazy { source["imageURL"].tojstring() }
	override val hasCloudFlare by lazy { source["hasCloudFlare"].toboolean() }
	override val hasSearch by lazy { source["hasSearch"].toboolean() }

	@Suppress("UNCHECKED_CAST")
	override val listings by lazy {
		CoerceLuaToJava.coerce(source["listings"], Array<Formatter.Listing>::class.java) as Array<Formatter.Listing>
	}

	@Suppress("UNCHECKED_CAST")
	override val searchFilters by lazy {
		tableToFilters(source["searchFilters"] as LuaTable)
	}

	@Suppress("UNCHECKED_CAST")
	override val settings by lazy {
		tableToFilters(source["settings"] as LuaTable)
	}

	override fun updateSetting(id: Int, value: Any?) {
		val f = source["updateSetting"]
		if (f.type() != TFUNCTION) return
		f.call(valueOf(id), coerce(value))
	}

	override fun getPassage(chapterURL: String): String =
			source["getPassage"].call(chapterURL).tojstring()

	override fun parseNovel(novelURL: String, loadChapters: Boolean, reporter: (status: String) -> Unit): Novel.Info =
			CoerceLuaToJava.coerce(source["parseNovel"].call(
					valueOf(novelURL),
					valueOf(loadChapters),
					makeLuaReporter(reporter)
			), Novel.Info::class.java) as Novel.Info

	@Suppress("UNCHECKED_CAST")
	override fun search(data: Array<*>, reporter: (status: String) -> Unit): Array<Novel.Listing> =
			CoerceLuaToJava.coerce(source["search"].call(
					data.toLua(),
					makeLuaReporter(reporter)
			), Array<Novel.Listing>::class.java) as Array<Novel.Listing>

	override fun freshURL(smallURL: String, type: Int): String {
		val f = source["freshURL"]
		if (f.type() != TFUNCTION) return smallURL
		return f.call(valueOf(smallURL), valueOf(type)).tojstring()
	}
}
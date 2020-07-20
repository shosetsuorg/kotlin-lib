package app.shosetsu.lib

import org.luaj.vm2.LuaTable
import org.luaj.vm2.lib.jse.CoerceJavaToLua

/*
 * This file is part of shosetsu-kotlin-lib.
 * shosetsu-kotlin-lib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * shosetsu-kotlin-lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with shosetsu-kotlin-lib.  If not, see <https://www.gnu.org/licenses/>.
 */

/** Converts a [Map] to a [LuaTable] */
fun Map<Int, *>.toLua(): LuaTable = LuaTable().also {
	this.forEach { (i, any) -> it[i] = CoerceJavaToLua.coerce(any) }
}


/** Converts a [Map] to a [LuaTable] */
fun Array<*>.toLua(): LuaTable = LuaTable().also {
	this.map { CoerceJavaToLua.coerce(it) }.forEachIndexed { i, v -> it[i] = v }
}

/** Converts [Array] of anything to [LuaTable] */
fun Array<*>.toLua(oneIndex: Boolean): LuaTable = LuaTable().also {
	this.map { CoerceJavaToLua.coerce(it) }.forEachIndexed { i, v -> it[if (oneIndex) i + 1 else i] = v }
}

fun Array<Filter<*>>.values(): Array<*> = this.map { it.state }.toTypedArray()

fun Array<Filter<*>>.valuesMap(): Map<Int, *> {
	val map = HashMap<Int, Any?>()
	this.forEach { map[it.id] = it.state }
	return map
}



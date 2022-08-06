@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package app.shosetsu.lib

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

/**
 * shosetsu-kotlin-lib
 * 31 / 01 / 2020
 *
 * @param id [Int] This is a unique calling card
 * for the specific filter / setting.
 *
 * If the setting type is ever changed, the ID should be changed
 *
 * @param name Name of the filer
 *
 * @param state States of the filter stuff
 */
sealed class Filter<T>(val id: Int, val name: String, open var state: T) {
	@Suppress("IMPLICIT_CAST_TO_ANY")
	override fun toString(): String =
		"Filter(id='$id', name='$name', state=`${
			if (state is Sequence<*>)
				state.toString() else state
		}`)"

	/**
	 * Represents a header, used to separate different parts of filters/settings
	 * Includes [Separator]
	 */
	class Header(name: String) : Filter<Unit>(-1, name, Unit)

	/**
	 * Divides parts of the filters/settings
	 */
	class Separator : Filter<Unit>(-1, "", Unit)

	/**
	 * Input for text
	 * Includes [Separator]
	 */
	class Text(id: Int, name: String) : Filter<String>(id, name, "")

	/**
	 * Input for passwords
	 */
	class Password(id: Int, name: String) : Filter<String>(id, name, "")

	/**
	 * Input for boolean option
	 * Includes [Separator]
	 */
	class Switch(
		id: Int,
		name: String
	) : Filter<Boolean>(id, name, false)


	/**
	 * Input for boolean option
	 * Includes [Separator]
	 */
	class Checkbox(
		id: Int,
		name: String
	) : Filter<Boolean>(id, name, false)


	/**
	 * Input for ternary value
	 * Includes [Separator]
	 */
	class TriState(
		id: Int,
		name: String
	) : Filter<Int>(id, name, STATE_IGNORED) {
		companion object {
			const val STATE_IGNORED = 0
			const val STATE_INCLUDE = 1
			const val STATE_EXCLUDE = 2
		}
	}


	/**
	 * Input for a choice from a list
	 * Includes [Separator]
	 */
	class Dropdown(
		id: Int,
		name: String,
		val choices: Array<String>
	) : Filter<Int>(id, name, 0)


	/**
	 * Input for a choice from a list
	 * Includes [Separator]
	 */
	class RadioGroup(
		id: Int,
		name: String,
		val choices: Array<String>
	) : Filter<Int>(id, name, 0)


	// Grouping

	/**
	 * A collapsable list of filters
	 * Includes [Separator]
	 * @param filters Filters present
	 */
	class List(
			name: String,
			val filters: Array<Filter<*>>
	) : Filter<Map<Int, Any>>(-1, name, filters.mapify()) {
		override var state: Map<Int, Any>
			get() = filters.mapify()
			set(_) {}
	}

	/**
	 * Input for a specific list of filters
	 * Includes [Separator]
	 * @param filters Filters present
	 */
	class Group<T>(
			name: String,
			val filters: Array<Filter<T>>
	) : Filter<Map<Int, T>>(-1, name, filters.mapifyS()) {
		override var state: Map<Int, T>
			get() = filters.mapifyS()
			set(_) {}
	}
}

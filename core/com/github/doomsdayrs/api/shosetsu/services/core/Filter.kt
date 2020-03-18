package com.github.doomsdayrs.api.shosetsu.services.core

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
 * along with shosetsu-extensions.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */
/**
 * shosetsu-extensions
 * 31 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
abstract class Filter<T>(val name: String, var state: T)

class Header(name: String): Filter<Unit>(name, Unit)
class Separator : Filter<Unit>("", Unit)

class TextFilter(name: String) : Filter<String>(name, "")

class SwitchFilter(name: String) : Filter<Boolean>(name, false)
class CheckboxFilter(name: String) : Filter<Boolean>(name, false)

class TriStateFilter(name: String) : Filter<Int>(name, STATE_IGNORED) {
    companion object {
        const val STATE_IGNORED = 0
        const val STATE_INCLUDE = 1
        const val STATE_EXCLUDE = 2
    }
}

class DropdownFilter(name: String, val choices: Array<String>) : Filter<Int>(name, 0)
class RadioGroupFilter(name: String, val choices: Array<String>) : Filter<Int>(name, 0)

class FilterList(name: String, val filters: Array<Filter<*>>) : Filter<Array<*>>(name, emptyArray<Any>())
class FilterGroup<I, T>(name: String, val filters: Array<I>) : Filter<Array<T>>(name, emptyArray<T>()) where I : Filter<T>


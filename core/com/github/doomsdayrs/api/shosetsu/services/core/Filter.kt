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
 * shosetsu-extensions
 * 31 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */

abstract class Filter<T>(val id: Int)

class TextFilter(id: Int, val name: String) : Filter<String>(id)
class SwitchFilter(id: Int, val name: String) : Filter<Boolean>(id)
class RadioGroupFilter(id: Int, val name: String, val choices: Array<String>) : Filter<Int>(id)
class FilterGroup<T>(id: Int, name: String, filters: Array<Filter<T>>) : Filter<Array<T>>(id)
class CheckboxFilter(id: Int, name: String) : Filter<Boolean>(id)

/** Android's Spinner */
class DropdownFilter(id: Int, val name: String, val choices: Array<String>) : Filter<Int>(id)

sealed class TriStateFilter(id: Int, name: String) : Filter<Array<Int>>(id)


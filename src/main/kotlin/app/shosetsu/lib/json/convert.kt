package app.shosetsu.lib.json

import org.json.JSONArray
import java.util.*

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
 * along with shosetsu-kotlin-libs.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * kotlin-lib
 * 23 / 10 / 2020
 */
fun JSONArray.toAndroid(): List<Any> {
	val list = ArrayList<Any>()
	val size = this.length()
	for (i in 0 until size)
		list.add(get(i))
	return list
}
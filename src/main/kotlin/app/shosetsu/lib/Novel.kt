@file:Suppress("MemberVisibilityCanBePrivate")

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
 * @author github.com/doomsdayrs
 */
class Novel {
	data class Listing(
		var title: String = "",
		var link: String = "",
		var imageURL: String = ""
	)

	data class Chapter(
		var release: String = "",
		var title: String = "",
		var link: String = "",
		var order: Double = 0.0
	)

	enum class Status(val title: String, val key: Int) {
		PUBLISHING("Publishing", 0),
		COMPLETED("Completed", 1),
		PAUSED("Paused", 2),
		UNKNOWN("Unknown", -1);


		override fun toString() = "NovelStatus($title)"

		companion object {
			fun fromInt(key: Int): Status =
				when (key) {
					0 -> PUBLISHING
					1 -> COMPLETED
					2 -> PAUSED
					else -> UNKNOWN
				}
		}
	}

	@Suppress("ArrayInDataClass")
	data class Info(
		var title: String = "",
		var alternativeTitles: Array<String> = arrayOf(),
		var imageURL: String = "",
		var language: String = "",
		var description: String = "",
		var status: Status = Status.UNKNOWN,
		var tags: Array<String> = arrayOf(),
		var genres: Array<String> = arrayOf(),
		var authors: Array<String> = arrayOf(),
		var artists: Array<String> = arrayOf(),
		var chapters: List<Chapter> = arrayListOf()
	)

	/** Represents the data type of a chapter */
	enum class ChapterType(val key: Int, val fileExtension: String) {

		/** Strings with no formatting */
		STRING(0, "txt"),

		/** HTML pages */
		HTML(1, "html");

		companion object {
			fun valueOf(key: Int): ChapterType =
				when (key) {
					1 -> HTML
					else -> STRING
				}
		}

	}
}
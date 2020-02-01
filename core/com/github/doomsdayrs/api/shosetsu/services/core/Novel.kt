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
class Novel {
    data class Listing(var title: String = "unknown", var link: String = "", var imageURL: String = "")
    data class Chapter(var release: String = "unknown", var title: String = "unknown", var link: String = "", var order: Double = 0.0)
    enum class Status(val title: String) {
        PUBLISHING("Publishing"), COMPLETED("Completed"), PAUSED("Paused"), UNKNOWN("Unknown");

        override fun toString(): String {
            return "NovelStatus(title='$title')"
        }
    }

    data class Info(var title: String = "unknown",
                    var imageURL: String = "",
                    var description: String = "unknown",
                    var genres: Array<String> = arrayOf(),
                    var authors: Array<String> = arrayOf(),
                    var status: Status = Status.UNKNOWN,
                    var tags: Array<String> = arrayOf(),
                    var artists: Array<String> = arrayOf(),
                    var language: String = "unknown",
                    /**
                     * If chapter list is incrementing, then this is used to prevent the loader from going past it
                     */
                    var maxChapterPage: Int = 0,
                    var novelChapters: List<Chapter> = arrayListOf()) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Info

            if (title != other.title) return false
            if (imageURL != other.imageURL) return false
            if (description != other.description) return false
            if (!genres.contentEquals(other.genres)) return false
            if (!authors.contentEquals(other.authors)) return false
            if (status != other.status) return false
            if (!tags.contentEquals(other.tags)) return false
            if (!artists.contentEquals(other.artists)) return false
            if (language != other.language) return false
            if (maxChapterPage != other.maxChapterPage) return false
            if (novelChapters != other.novelChapters) return false

            return true
        }

        override fun hashCode(): Int {
            var result = title.hashCode()
            result = 31 * result + imageURL.hashCode()
            result = 31 * result + description.hashCode()
            result = 31 * result + genres.contentHashCode()
            result = 31 * result + authors.contentHashCode()
            result = 31 * result + status.hashCode()
            result = 31 * result + tags.contentHashCode()
            result = 31 * result + artists.contentHashCode()
            result = 31 * result + language.hashCode()
            result = 31 * result + maxChapterPage
            result = 31 * result + novelChapters.hashCode()
            return result
        }
    }
}
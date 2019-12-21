package com.github.doomsdayrs.api.shosetsu.services.core.objects

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

/**
 * shosetsu-services
 * 30 / May / 2019
 *
 * @author github.com/doomsdayrs
 */
@Suppress("MemberVisibilityCanBePrivate")
class NovelPage {
    var title: String = "unknown"
    var imageURL: String = ""
    var description: String = "unknown"
    var genres: Array<String> = arrayOf()
    var authors: Array<String> = arrayOf()
    var status = NovelStatus.UNKNOWN
    var tags: Array<String> = arrayOf()
    var artists: Array<String> = arrayOf()
    var language: String = "unknown"

    /**
     * If chapter list is incrementing, then this is used to prevent the loader from going past it
     */
    var maxChapterPage = 0
    var novelChapters: List<NovelChapter> = arrayListOf()

    override fun toString(): String {
        return "NovelPage(title='$title', imageURL='$imageURL', description='$description', genres=${genres.contentToString()}, authors=${authors.contentToString()}, status=$status, tags=${tags.contentToString()}, artists=${artists.contentToString()}, language='$language', maxChapterPage=$maxChapterPage, novelChapters=$novelChapters)"
    }
}
package com.github.doomsdayrs.api.shosetsu.services.core

import org.jsoup.nodes.Document

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
 * @noinspection unused
 */
@Suppress("unused")
interface Formatter {
    val formatterID: Int

    val isIncrementingChapterList: Boolean
    val isIncrementingPassagePage: Boolean
    val hasCloudFlare: Boolean
    val hasSearch: Boolean
    val hasGenres: Boolean


    // Must be initialized
    val name: String
    val imageURL: String


    /**
     * Parse the novel Chapter
     *
     * @param document incoming page to parse
     * @return The Passage of the novel
     */
    fun getNovelPassage(document: Document): String

    /**
     * Only if isIncrementingChapterList() is true, this can be used
     *
     *
     * Returns a URL that includes the specified increment
     *
     * @param url       URL of the novel
     * @param increment increment to add to the URL
     * @return combined [String] url
     */
    fun novelPageCombiner(url: String, increment: Int): String

    /**
     * If there is a latest page, use this to return a certain page. Starts at 1 onwards
     *
     * @param page page number
     * @return [String] URL of the next latest page
     */
    fun getLatestURL(page: Int): String

    /**
     * @param document document to parse
     * @return [List] of novels listed
     */
    fun parseLatest(document: Document): List<Novel.Listing>

    /**
     * @param query query string to be searched for
     * @return [String] url of the query
     */
    fun getSearchString(query: String): String

    /**
     * Parse document to get list
     *
     * @param document document to parse
     * @return [List] of novels
     */
    fun parseSearch(document: Document): List<Novel.Listing>

    /**
     * Parse the novelPage
     *
     * @param document incoming document to parse
     * @return NovelPage object with as many parameters filled as possible;
     */
    fun parseNovel(document: Document): Novel.Info

    /**
     * Above but with increment
     *
     * @param document  doc to parse
     * @param increment increment
     * @return [NovelPage]
     */
    fun parseNovel(document: Document, increment: Int): Novel.Info
}
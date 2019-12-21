@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.github.doomsdayrs.api.shosetsu.services.core.dep

import com.github.doomsdayrs.api.shosetsu.services.core.objects.Novel
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelGenre
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelPage
import com.github.doomsdayrs.api.shosetsu.services.core.objects.Ordering
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.net.URL

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
 * 29 / May / 2019
 *
 * @author github.com/doomsdayrs
 */
abstract class ScrapeFormat(override val formatterID: Int = -2) : Formatter {

    // Variables that can be adjusted
    override var builder: Request.Builder = Request.Builder()
    override var client: OkHttpClient = OkHttpClient()
    override var isIncrementingChapterList: Boolean = false
    override var isIncrementingPassagePage: Boolean = false
    override var chapterOrder = Ordering.TopBottomLatestOldest
    override var latestOrder = Ordering.TopBottomLatestOldest
    override val hasCloudFlare: Boolean = false
    override val hasSearch: Boolean = true
    override val hasGenres: Boolean = false

    /**
     * Requests the data
     *
     * @param url URL to fetch
     * @return the response
     * @throws IOException errorrrr
     */
    @Throws(IOException::class)
    protected fun request(url: String): ResponseBody? {
        println(url)
        val u = URL(url)
        val request = builder.url(u).build()
        return client.newCall(request).execute().body
    }

    /**
     * Parses website to a Document
     *
     * @param URL URL to scrape
     * @return Document
     * @throws IOException if anything goes wrong
     * @noinspection unused
     */
    @Throws(IOException::class)
    protected fun docFromURL(URL: String): Document {
        return Jsoup.parse(request(URL)!!.string())
    }

    // Methods and variables that MUST be initialized
    abstract override val name: String
    abstract override val imageURL: String
    abstract override fun getNovelPassage(document: Document): String
    abstract override fun parseNovel(document: Document): NovelPage
    abstract override fun novelPageCombiner(url: String, increment: Int): String
    abstract override fun parseLatest(document: Document): List<Novel>
    abstract override fun parseNovel(document: Document, increment: Int): NovelPage
    abstract override fun getSearchString(query: String): String
    abstract override fun parseSearch(document: Document): List<Novel>
    abstract override fun getLatestURL(page: Int): String
    abstract override val genres: Array<NovelGenre>

    companion object {
        /**
         * Fixes URL. Whenever a URL enters the code, or leaves. It should be verified.
         *
         * @param baseURL the starter of the URL. IE `https://www.novelsite.net` or `https://www.novelsite.net/`
         * @param target  the url to verify, ie '/subURL' or 'subURL'
         * @return Fixed URL
         */
        protected fun verify(baseURL: String, target: String): String {
            var storedTarget = target
            val a = baseURL.length
            val b = storedTarget.length
            if (baseURL.endsWith("/")) if (storedTarget.startsWith("/")) storedTarget = storedTarget.replaceFirst("/".toRegex(), "")
            return if (a < b) {
                val segment = storedTarget.substring(0, a)
                if (segment != baseURL) baseURL + storedTarget else storedTarget
            } else baseURL + storedTarget
        }
    }
}
package com.github.Doomsdayrs.api.shosetsu.services.core.dep;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Novel;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelGenre;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Ordering;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

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
 * shosetsu-services
 * 30 / May / 2019
 *
 * @author github.com/doomsdayrs
 * @noinspection unused
 */
public interface Formatter {


    void setBuilder(Request.Builder builder);

    void setClient(OkHttpClient client);

    /**
     * @return True if has cloudflare protection
     */
    boolean hasCloudFlare();

    /**
     * @return Name of the class
     */
    String getName();

    /**
     * @return Image URL of the parser, yes it can be custom
     */
    String getImageURL();

    /**
     * Set int ID as param in constructor and pass it up to the ScrapeFormat
     *
     * @return Formatter ID
     */
    int getID();

    /**
     * @return Has Search?
     */
    boolean hasSearch();

    /**
     * @return Has Genres?
     */
    boolean hasGenres();

    /**
     * @return returns true if the chapter list is split into many pages
     */
    boolean isIncrementingChapterList();

    /**
     * @return returns true if the novel passage is spilt into many pages
     */
    boolean isIncrementingPassagePage();

    Ordering chapterOrder();

    Ordering latestOrder();

    /**
     * Parse the novel Chapter
     *
     * @param document incoming page to parse
     * @return The Passage of the novel
     */
    String getNovelPassage(Document document);

    @Deprecated
    String getNovelPassage(String URL) throws IOException;

    /**
     * Parse the novelPage
     *
     * @param document incoming document to parse
     * @return NovelPage object with as many parameters filled as possible;
     */
    NovelPage parseNovel(Document document);

    /**
     * Above but with increment
     *
     * @param document  doc to parse
     * @param increment increment
     * @return NovelPage
     */
    NovelPage parseNovel(Document document, int increment);

    /**
     * Only if isIncrementingChapterList() is true, this can be used
     * <p>
     * Returns a URL that includes the specified increment
     *
     * @param url       URL of the novel
     * @param increment increment to add to the URL
     * @return Combined URL
     */
    String novelPageCombiner(String url, int increment);

    @Deprecated
    NovelPage parseNovel(String URL) throws IOException;

    /**
     * the above, except if isIncrementingChapterList() returns true this will be used in its stead
     *
     * @param URL       Incoming Novel page URL to parse
     * @param increment What increment to use
     * @return NovelPage of parsed chapters
     * @throws IOException If any error occurs
     */
    @Deprecated
    NovelPage parseNovel(String URL, int increment) throws IOException;

    /**
     * If there is a latest page, use this to return a certain page. Starts at 1 onwards
     *
     * @param page page number
     * @return string URL of the next latest page
     */
    String getLatestURL(int page);

    /**
     * @param document document to parse
     * @return List of novels listed
     */
    List<Novel> parseLatest(Document document);

    @Deprecated
    List<Novel> parseLatest(String URL) throws IOException;

    /**
     * @param query query string to be searched for
     * @return String url of the query
     */
    String getSearchString(String query);

    /**
     * Parse document to get list
     *
     * @param document document to parse
     * @return List of novels
     */
    List<Novel> parseSearch(Document document);

    @Deprecated
    List<Novel> search(String query) throws IOException;

    // TODO Make an side menu builder

    /**
     * ArrayList of genres site has
     *
     * @return genres
     */
    NovelGenre[] getGenres();
}

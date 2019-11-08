package com.github.Doomsdayrs.api.shosetsu.services.core.dep;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.Novel;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelGenre;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
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
 * novelreader-core
 * 29 / May / 2019
 *
 * @author github.com/doomsdayrs
 */
public abstract class ScrapeFormat implements Formatter {

    private final int ID;
    private Request.Builder builder;
    private OkHttpClient client;

    public ScrapeFormat(int id) {
        ID = id;
        this.builder = new Request.Builder();
        this.client = new OkHttpClient();
    }

    public ScrapeFormat(int id, Request.Builder builder) {
        ID = id;
        this.builder = builder;
        this.client = new OkHttpClient();
    }

    public ScrapeFormat(int id, OkHttpClient client) {
        ID = id;
        this.builder = new Request.Builder();
        this.client = client;
    }

    public ScrapeFormat(int id, Request.Builder builder, OkHttpClient client) {
        ID = id;
        this.builder = builder;
        this.client = client;
    }

    public void setBuilder(Request.Builder builder) {
        this.builder = builder;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    /**
     * Requests the data
     *
     * @param url URL to fetch
     * @return the response
     * @throws IOException errorrrr
     */
    protected ResponseBody request(String url) throws IOException {
        System.out.println(url);
        URL u = new URL(url);
        Request request = builder.url(u).build();
        return client.newCall(request).execute().body();
    }

    /**
     * Parses website to a Document
     *
     * @param URL URL to scrape
     * @return Document
     * @throws IOException if anything goes wrong
     * @noinspection unused
     */
    protected Document docFromURL(String URL) throws IOException {
        return Jsoup.parse(request(URL).string());
    }

    /**
     * Fixes URL. Whenever a URL enters the code, or leaves. It should be verified.
     *
     * @param baseURL the starter of the URL. IE `https://www.novelsite.net` or `https://www.novelsite.net/`
     * @param target  the url to verify, ie '/subURL' or 'subURL'
     * @return Fixed URL
     */
    protected static String verify(String baseURL, String target) {
        int a = baseURL.length();
        int b = target.length();
        if (baseURL.endsWith("/"))
            if (target.startsWith("/"))
                target = target.replaceFirst("/", "");

        if (a < b) {
            String segment = target.substring(0, a);
            if (!segment.equals(baseURL))
                return baseURL + target;
            else return target;
        } else return baseURL + target;
    }


    // Variables that can be adjusted
    private boolean hasSearch = true;
    private boolean incrementingChapterList = false;
    private boolean incrementingPassage = false;
    private boolean hasGenres = false;
    private Ordering chapterOrdering = Ordering.TopBottomLatestOldest;
    private Ordering latestOrdering = Ordering.TopBottomLatestOldest;
    private boolean hasCloudFlare = false;

    /**
     * @return ID of formatter
     */
    public int getID() {
        return ID;
    }

    // Methods below override the formatter classes methods


    @Override
    public boolean hasCloudFlare() {
        return hasCloudFlare;
    }

    public boolean hasCloudFlare(boolean hasCloudFlare) {
        this.hasCloudFlare = hasCloudFlare;
        return hasCloudFlare;
    }

    /**
     * On default will return true, stating site does have a search feature
     *
     * @return Has Search?
     */
    @Override
    public boolean hasSearch() {
        return hasSearch;
    }

    public boolean hasSearch(boolean hasSearch) {
        this.hasSearch = hasSearch;
        return hasSearch;
    }

    /**
     * On default will return true, stating site has genres
     *
     * @return Has genres?
     */
    @Override
    public boolean hasGenres() {
        return hasGenres;
    }

    public boolean hasGenres(boolean hasGenres) {
        this.hasGenres = hasGenres;
        return hasGenres;
    }

    public boolean isIncrementingChapterList() {
        return incrementingChapterList;
    }

    public boolean isIncrementingChapterList(boolean incrementingChapterList) {
        this.incrementingChapterList = incrementingChapterList;
        return incrementingChapterList;
    }

    @Override
    public boolean isIncrementingPassagePage() {
        return incrementingPassage;
    }

    public boolean isIncrementingPassagePage(boolean incrementingPassage) {
        this.incrementingPassage = incrementingPassage;
        return incrementingPassage;
    }

    @Override
    public Ordering chapterOrder() {
        return chapterOrdering;
    }

    public Ordering chapterOrder(Ordering chapterOrdering) {
        this.chapterOrdering = chapterOrdering;
        return chapterOrdering;
    }

    @Override
    public Ordering latestOrder() {
        return latestOrdering;
    }

    public Ordering latestOrder(Ordering latestOrdering) {
        this.latestOrdering = latestOrdering;
        return latestOrdering;
    }

    public abstract String getName();

    public abstract String getImageURL();

    public abstract String getNovelPassage(String URL) throws IOException;

    public abstract NovelPage parseNovel(String URL) throws IOException;

    public abstract NovelPage parseNovel(String URL, int increment) throws IOException;

    public abstract String getLatestURL(int page);

    public abstract List<Novel> parseLatest(String URL) throws IOException;

    public abstract List<Novel> search(String query) throws IOException;

    public abstract NovelGenre[] getGenres();
}

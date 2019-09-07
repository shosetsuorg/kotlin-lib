package com.github.Doomsdayrs.api.shosetsu.services.core.objects;

import java.io.Serializable;
import java.util.Arrays;
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
 * 30 / May / 2019
 *
 * @author github.com/doomsdayrs
 * @noinspection WeakerAccess
 */
public class NovelPage implements Serializable {
    static final long serialVersionUID = 1003;

    public String title;
    public String imageURL;
    public String description;
    public String[] genres;
    public String[] authors;
    public Stati status = Stati.UNKNOWN;
    public String[] tags;
    public String[] artists;
    public String language;
    /**
     * If chapter list is incrementing, then this is used to prevent the loader from going past it
     */
    public int maxChapterPage;
    public List<NovelChapter> novelChapters;


    @Override
    public String toString() {
        return "NovelPage{" +
                "title='" + title + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", description='" + description + '\'' +
                ", genres=" + Arrays.toString(genres) +
                ", authors=" + Arrays.toString(authors) +
                ", status=" + status +
                ", tags=" + Arrays.toString(tags) +
                ", artists=" + Arrays.toString(artists) +
                ", language='" + language + '\'' +
                ", novelChapters=" + novelChapters +
                '}';
    }
}

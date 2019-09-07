package com.github.Doomsdayrs.api.shosetsu.services.core.objects;

import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.Base64;

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
 * along with shosetsu-services.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 * shosetsu-services
 * 07 / 09 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class Serialize {
    private static final String[] NOVELPAGEKEYS = {"title", "imageURL", "description", "genres", "authors", "status", "tags", "artists", "language", "maxChapterPage", "novelChapters"};

    /**
     * Turns an object into a JSON counterpart, then serializes it along with data in it
     *
     * @param object NovelPage or NovelChapter
     * @return Serialized JSON
     */
    public static String serializeOBJECT(Object object) throws IOException {
        if (object.getClass().equals(NovelChapter.class)) {
            NovelChapter novelChapter = (NovelChapter) object;
            return serialize(novelChapterToJSON(novelChapter).toString());
        } else if (object.getClass().equals(NovelPage.class)) {
            NovelPage novelPage = (NovelPage) object;
            JSONObject jsonObject = new JSONObject();

            if (novelPage.title != null)
                jsonObject.put("title", serialize(novelPage.title));
            else jsonObject.put("title", "null");

            if (novelPage.imageURL != null)
                jsonObject.put("imageURL", serialize(novelPage.imageURL));
            else jsonObject.put("imageURL", "null");

            if (novelPage.description != null)
                jsonObject.put("description", serialize(novelPage.description));
            else jsonObject.put("description", "null");

            if (novelPage.genres != null) {
                JSONArray jsonArray = new JSONArray();
                for (String genre : novelPage.genres)
                    jsonArray.put(serialize(genre));
                jsonObject.put("genres", jsonArray);
            } else jsonObject.put("genres", new JSONArray());

            if (novelPage.authors != null) {
                JSONArray jsonArray = new JSONArray();
                for (String author : novelPage.authors)
                    jsonArray.put(serialize(author));
                jsonObject.put("authors", jsonArray);
            } else jsonObject.put("authors", new JSONArray());

            if (novelPage.status != null) {
                jsonObject.put("status", novelPage.status.toString());
            } else jsonObject.put("status", "Unknown");

            if (novelPage.tags != null) {
                JSONArray jsonArray = new JSONArray();
                for (String tag : novelPage.tags)
                    jsonArray.put(serialize(tag));
                jsonObject.put("tags", jsonArray);
            } else jsonObject.put("tags", new JSONArray());

            if (novelPage.artists != null) {
                JSONArray jsonArray = new JSONArray();
                for (String artist : novelPage.artists)
                    jsonArray.put(serialize(artist));
                jsonObject.put("artists", jsonArray);
            } else jsonObject.put("artists", new JSONArray());

            if (novelPage.language != null) {
                jsonObject.put("language", serialize(novelPage.language));
            } else jsonObject.put("language", "null");

            jsonObject.put("maxChapterPage", novelPage.maxChapterPage);

            if (novelPage.novelChapters != null) {
                JSONArray jsonArray = new JSONArray();
                for (NovelChapter novelChapter : novelPage.novelChapters)
                    jsonArray.put(serialize(novelChapterToJSON(novelChapter)));
                jsonObject.put("novelChapters", jsonArray);
            } else jsonObject.put("novelChapters", new JSONArray());
            return serialize(jsonObject.toString());
        } else throw new ClassFormatException("Illegal class");
    }

    public static NovelPage deserializeNovelPageJSON(String serial) throws IOException, ClassNotFoundException {
        NovelPage novelPage = new NovelPage();
        JSONObject jsonObject = new JSONObject(deserialize(serial));

        for (String key : NOVELPAGEKEYS) {
            if (!jsonObject.has(key))
                throw new MalformedParametersException("JSON is invalid due to missing key[" + key + "]");

            switch (key) {
                case "maxChapterPage":
                    novelPage.maxChapterPage = jsonObject.getInt(key);
                    break;

                case "status":
                    switch ((String) deserialize(jsonObject.getString(key))) {
                        case "Publishing":
                            novelPage.status = Stati.PUBLISHING;
                            break;
                        case "Completed":
                            novelPage.status = Stati.COMPLETED;
                            break;
                        case "Paused":
                            novelPage.status = Stati.PAUSED;
                            break;
                        case "Unknown":
                            novelPage.status = Stati.UNKNOWN;
                            break;
                    }
                    break;

                case "genres":
                case "authors":
                case "tags":
                case "artists":
                    JSONArray array = jsonObject.getJSONArray(key);
                    String[] strings = new String[array.length()];
                    for (int x = 0; x < array.length(); x++) {
                        String s = array.getString(x);
                        strings[x] = (String) deserialize(s);
                    }
                    switch (key) {
                        case "genres":
                            novelPage.genres = strings;
                            break;
                        case "authors":
                            novelPage.authors = strings;
                            break;
                        case "tags":
                            novelPage.tags = strings;
                            break;
                        case "artists":
                            novelPage.artists = strings;
                            break;
                    }
                    break;
                case "novelChapters":
                    JSONArray jsonArray = jsonObject.getJSONArray(key);
                    ArrayList<NovelChapter> novelChapters = new ArrayList<>();
                    for (int x = 0; x < jsonArray.length(); x++) {
                        novelChapters.add(deserializeNovelChapterJSON(jsonArray.getString(x)));
                    }
                    novelPage.novelChapters = novelChapters;
                    break;
                default:
                    String response = (String) deserialize(jsonObject.getString(key));
                    switch (key) {

                    }



                    break;
            }
        }
        return novelPage;
    }

    public static NovelChapter deserializeNovelChapterJSON(String serial) {
        NovelChapter novelChapter = new NovelChapter();

        return novelChapter;
    }


    private static JSONObject novelChapterToJSON(NovelChapter novelChapter) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("release", serialize(novelChapter.release));
        jsonObject.put("chapterNum", serialize(novelChapter.chapterNum));
        jsonObject.put("link", serialize(novelChapter.link));
        return jsonObject;
    }


    public static String serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }


    public static Object deserialize(String string) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(string.getBytes());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return objectInputStream.readObject();
    }

}

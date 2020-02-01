package com.github.doomsdayrs.api.shosetsu.services.core

import org.luaj.vm2.LuaTable

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
    class Listing(val id: Int, val name: String = "unknown", val isIncrementing: Boolean = false, val getListing: (increment: Int) -> Array<Novel.Listing>)

    val name: String
    val imageURL: String
    val formatterID: Int
    val hasCloudFlare: Boolean
    val hasSearch: Boolean
    val listings: Array<Listing>

    fun getPassage(chapterURL: String): String
    fun search(data: LuaTable): Array<Novel.Listing>
    fun parseNovel(novelURL: String): Novel.Info
    fun parseNovel(novelURL: String, increment: Int): Novel.Info
}
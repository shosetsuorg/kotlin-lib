@file:Suppress("MemberVisibilityCanBePrivate")

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
 * 12 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class NovelGenre(val name: String, var isURL: Boolean, val type: String){
    override fun toString(): String {
        return "NovelGenre(name='$name', isURL=$isURL, type='$type')"
    }
}
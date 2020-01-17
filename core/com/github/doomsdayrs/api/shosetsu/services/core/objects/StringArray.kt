package com.github.doomsdayrs.api.shosetsu.services.core.objects

/**
 * com.github.doomsdayrs.api.shosetsu.extensions.lang.en
 * 17 / January / 2020
 *
 * @author github.com/doomsdayrs
 */
class StringArray {
    var strings: Array<String> = arrayOf()

    fun setSize(size: Int) {
        val strings: ArrayList<String> = ArrayList()
        for (i in 1..size)
            strings.add("")
        this.strings = strings.toArray(arrayOf(""))
    }

    fun setPosition(index: Int, value: String) {
        strings[index] = value
    }
}
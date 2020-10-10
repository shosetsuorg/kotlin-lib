package app.shosetsu.lib

/**
 * kotlin-lib
 * 10 / 10 / 2020
 *
 * Version data class
 */
val KOTLIN_LIB_VERSION = Version(1, 0, 0)

data class Version(
		val major: Int,
		val minor: Int,
		val patch: Int
) {
	constructor(array: Array<Int>) : this(array[0], array[1], array[2])
	constructor(string: String) : this(string.split(".").map { it.toInt() }.toTypedArray())

	fun isCompatible(other: Version): Boolean = major == other.major && minor >= other.minor

	fun isCompatible(): Boolean = isCompatible(KOTLIN_LIB_VERSION)
}
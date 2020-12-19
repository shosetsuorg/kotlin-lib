package app.shosetsu.lib

import kotlinx.serialization.Serializable


/**
 * Version of the kotlin-lib shosetsu is currently using
 */
val KOTLIN_LIB_VERSION = Version(1, 0, 0)


/**
 * kotlin-lib
 * 10 / 10 / 2020
 *
 * Version data class
 * @param major Incremented with major non backwards compatible changes occur
 * @param minor Incremented with feature releases that don't destroy backwards compatibility
 * @param patch Incremented with bug fixes that don't destroy backwards compatibility
 */
@Serializable(with = VersionSerializer::class)
data class Version(
	val major: Int,
	val minor: Int,
	val patch: Int
) : Comparable<Version> {


	constructor(array: Array<Int>) : this(array[0], array[1], array[2])

	constructor(
		string: String
	) : this(string.split(".").map { it.toInt() }.toTypedArray())

	/**
	 * Checks if this is compatible with [other]
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	fun isCompatible(other: Version): Boolean =
		major == other.major && minor >= other.minor

	/**
	 * Checks if this is compatible with [KOTLIN_LIB_VERSION]
	 */
	@Suppress("unused")
	fun isCompatible(): Boolean =
		isCompatible(KOTLIN_LIB_VERSION)

	@Suppress("MemberVisibilityCanBePrivate")
	override fun compareTo(other: Version): Int {
		if (major == other.major && minor == other.minor && patch == other.patch)
			return 0

		if (major > other.major) return 1
		if (major < other.major) return -1

		if (minor > other.minor) return 1
		if (minor < other.minor) return -1

		if (patch > other.patch) return 1
		if (patch < other.patch) return -1

		return -1
	}

	override fun toString(): String = "$major.$minor.$patch"
}
package app.shosetsu.lib.json

import app.shosetsu.lib.Version

/**
 * 17 / 10 / 2020
 *
 * Represents an extension on the repository,
 * listed as "scripts" in the index
 */
data class RepoExtension internal constructor(
	val id: Int,
	val name: String,
	val fileName: String,
	val imageURL: String,
	val lang: String,
	val version: Version,
	val libVersion: Version,
	val md5: String
)


/**
 * 17 / 10 / 2020
 *
 * Represents a library listed in `libraries` within the repository index
 */
data class RepoLibrary internal constructor(
	val name: String,
	val version: Version
)
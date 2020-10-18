package app.shosetsu.lib.json

import app.shosetsu.lib.Version

/**
 * shosetsu-kotlin-lib
 * 17 / 10 / 2020
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
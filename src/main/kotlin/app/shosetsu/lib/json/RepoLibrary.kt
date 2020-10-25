package app.shosetsu.lib.json

import app.shosetsu.lib.Version

/**
 * shosetsu-kotlin-lib
 * 17 / 10 / 2020
 *
 * Represents a library listed in `libraries` within the repository index
 */
data class RepoLibrary internal constructor(val name: String, val version: Version)
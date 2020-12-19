package app.shosetsu.lib.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * shosetsu-kotlin-lib
 * 17 / 10 / 2020
 * @param libraries Libraries used by the repository
 * @param extensions Extensions listed in this repository
 */
@Serializable
data class RepoIndex internal constructor(
	@SerialName(LIBS_KEY)
	val libraries: List<RepoLibrary>,

	@SerialName(SCPS_KEY)
	val extensions: List<RepoExtension>
) {
	companion object {
		const val LIBS_KEY = "libraries"
		const val SCPS_KEY = "scripts"

		fun fromString(json: String): RepoIndex = Json.decodeFromString(json)
	}
}

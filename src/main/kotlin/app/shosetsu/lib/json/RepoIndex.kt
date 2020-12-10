package app.shosetsu.lib.json

import app.shosetsu.lib.Version
import app.shosetsu.lib.exceptions.JsonMissingKeyException
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.io.File
import java.io.FileReader
import java.io.StringReader

/**
 * shosetsu-kotlin-lib
 * 17 / 10 / 2020
 * @param libraries Libraries used by the repository
 * @param extensions Extensions listed in this repository
 */
data class RepoIndex internal constructor(
	val libraries: List<RepoLibrary>,
	val extensions: List<RepoExtension>
) {
	companion object {
		const val LIBS_KEY = "libraries"
		const val SCPS_KEY = "scripts"
	}

	constructor(jsonFile: File) : this(
		Parser.default().parse(FileReader(jsonFile)) as JsonObject
	)

	constructor(jsonString: String) : this(
		Parser.default().parse(StringReader(jsonString)) as JsonObject
	)

	// Using .toAndroid() to provide android compatiblity
	internal constructor(json: JsonObject) : this(
		json.array<JsonObject>(LIBS_KEY)?.map {
			RepoLibrary(
				name = it.string(J_NAME) ?: throw JsonMissingKeyException(
					LIBS_KEY,
					J_NAME
				),
				version = Version(
					it.string(J_VERSION) ?: throw JsonMissingKeyException(
						LIBS_KEY,
						J_VERSION
					)
				)
			)
		} ?: emptyList(),
		json.array<JsonObject>(SCPS_KEY)?.map {
			RepoExtension(
				id = it.int(J_ID) ?: throw JsonMissingKeyException(
					SCPS_KEY,
					J_ID
				),
				name = it.string(J_NAME) ?: throw JsonMissingKeyException(
					SCPS_KEY,
					J_NAME
				),
				fileName = it.string(J_FILE_NAME)
					?: throw JsonMissingKeyException(
						SCPS_KEY,
						J_FILE_NAME
					),
				imageURL = it.string(J_IMAGE_URL)
					?: throw JsonMissingKeyException(
						SCPS_KEY,
						J_IMAGE_URL
					),
				lang = it.string(J_LANGUAGE) ?: throw JsonMissingKeyException(
					SCPS_KEY,
					J_LANGUAGE
				),
				version = Version(
					it.string(J_VERSION) ?: throw JsonMissingKeyException(
						SCPS_KEY,
						J_VERSION
					)
				),
				libVersion = Version(
					it.string(J_LIB_VERSION)
						?: throw JsonMissingKeyException(
							SCPS_KEY,
							J_LIB_VERSION
						)
				),
				md5 = it.string(J_MD5) ?: throw JsonMissingKeyException(
					SCPS_KEY,
					J_MD5
				)
			)
		} ?: emptyList()
	)
}

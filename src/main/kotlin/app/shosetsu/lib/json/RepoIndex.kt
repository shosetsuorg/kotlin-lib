package app.shosetsu.lib.json

import app.shosetsu.lib.Version
import org.json.JSONObject
import java.io.File

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
	constructor(jsonFile: File) : this(JSONObject(jsonFile))
	constructor(jsonString: String) : this(JSONObject(jsonString))

	// Using .toList() to provide android compatiblity
	constructor(json: JSONObject) : this(
			json.getJSONArray("libraries").toList().map { it as JSONObject }.map {
				RepoLibrary(
						name = it.getString(J_NAME),
						version = Version(it.getString(J_VERSION))
				)
			},
			json.getJSONArray("scripts").toList().map { it as JSONObject }.map {
				RepoExtension(
						id = it.getInt(J_ID),
						name = it.getString(J_NAME),
						fileName = it.getString(J_FILE_NAME),
						imageURL = it.getString(J_IMAGE_URL),
						lang = it.getString(J_LANGUAGE),
						version = Version(it.getString(J_VERSION)),
						libVersion = Version(it.getString(J_LIB_VERSION)),
						md5 = it.getString(J_MD5)
				)
			}
	)
}

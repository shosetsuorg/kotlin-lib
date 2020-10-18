package app.shosetsu.lib.json

import app.shosetsu.lib.Version
import org.json.JSONObject

/**
 * shosetsu-kotlin-lib
 * 17 / 10 / 2020
 */
data class RepoIndex internal constructor(
		val libraries: List<RepoLibrary>,
		val extensions: List<RepoExtension>
) {
	constructor(json: String) : this(JSONObject(json))

	constructor(json: JSONObject) : this(
			json.getJSONArray("libraries").map { it as JSONObject }.map {
				RepoLibrary(
						name = it.getString(J_NAME),
						version = Version(it.getString(J_VERSION))
				)
			},
			json.getJSONArray("scripts").map { it as JSONObject }.map {
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

package app.shosetsu.lib.json

import app.shosetsu.lib.ExtensionType
import app.shosetsu.lib.Version
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 17 / 10 / 2020
 *
 * Represents an extension on the repository,
 * listed as "scripts" in the index
 *
 * @param id Identification of this extension, this should always be unique
 * @param name Name of the extension that is presentable to the user
 * @param fileName FileName of the extension
 * @param imageURL Image URL for this extension, for visual identification
 * @param lang String identification of the language via (ISO 639-1) standard
 * @param version Version of this extension
 * @param libVersion Version the extension is compatible with
 * @param md5 MD5 coding of the extension, for security checking
 */
@Serializable
data class RepoExtension internal constructor(
	@SerialName(J_ID)
	val id: Int,
	@SerialName(J_NAME)
	val name: String,
	@SerialName(J_FILE_NAME)
	val fileName: String,
	@SerialName(J_IMAGE_URL)
	val imageURL: String,
	@SerialName(J_LANGUAGE)
	val lang: String,
	@SerialName(J_VERSION)
	val version: Version,
	@SerialName(J_LIB_VERSION)
	val libVersion: Version,
	@SerialName(J_MD5)
	val md5: String,
	@SerialName(J_EXTENSION_TYPE)
	val type: ExtensionType = ExtensionType.LuaScript
)


/**
 * 17 / 10 / 2020
 *
 * Represents a library listed in `libraries` within the repository index
 *
 * @param name Name of the library, this is always the same as the filename
 *
 * @param version Version of the library
 */
@Serializable
data class RepoLibrary internal constructor(
	@SerialName(J_NAME)
	val name: String,
	@SerialName(J_VERSION)
	val version: Version
)
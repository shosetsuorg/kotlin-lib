package app.shosetsu.lib.kts

import app.shosetsu.lib.IExtension
import java.io.File
import kotlin.time.ExperimentalTime

/**
 * shosetsu-services
 * 06 / 10 / 2020
 */
class KtsExtension(
	private val content: String,
	private val _kts: IExtension = KtsObjectLoader().load(content)
) : IExtension by _kts {
	constructor(file: File) : this(file.readText())
}

package app.shosetsu.lib

/**
 * Type of extension
 */
enum class ExtensionType {
	/** .lua */
	LuaScript,

	/** .kts */
	@Deprecated("Does not work on android")
	KotlinScript
}

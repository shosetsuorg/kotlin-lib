package app.shosetsu.lib

import app.shosetsu.lib.json.RepoIndex
import app.shosetsu.lib.lua.LuaExtension
import app.shosetsu.lib.lua.ShosetsuLuaLib
import app.shosetsu.lib.lua.shosetsuGlobals
import okhttp3.OkHttpClient
import org.luaj.vm2.LuaValue
import java.io.File
import java.util.concurrent.TimeUnit.MILLISECONDS

/*
 * This file is part of shosetsu-services.
 * shosetsu-services is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * shosetsu-services is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with shosetsu-services.  If not, see https://www.gnu.org/licenses/.
 */

/**
 * shosetsu-services
 * 03 / June / 2019
 *
 * @author github.com/doomsdayrs;github.com/TechnoJo4
 *
 * In IDEA, The Classpath should be shosetsu-services but
 * the Working directory should be shosetsu-extensions.
 */
object Test {
	// CONFIG
	private const val SEARCH_VALUE = "world"
	private const val PRINT_LISTINGS = true
	private const val PRINT_LIST_STATS = true
	private const val PRINT_NOVELS = false
	private const val PRINT_NOVEL_STATS = true
	private const val PRINT_PASSAGES = true
	private const val PRINT_REPO_INDEX = true
	private const val PRINT_META_DATA = true
	private const val PRINT_PASSAGES = false
	private const val PRINT_REPO_INDEX = false
	private const val PRINT_METADATA = false
	private const val REPEAT = false

	private val SOURCES: List<String> = arrayOf<String>(
			//"en/BestLightNovel",
			//"en/BoxNovel",
			//"en/CreativeNovels",
			//"en/FastNovel",
			//"en/Foxaholic", // TODO: Investigate
			//"en/KissLightNovels",
			//"en/MNovelFree", //Doesn't seem to be a novelfull
			//"en/MTLNovel",
			//"en/NovelFull",
			//"en/NovelTrench",
			//"en/ReadLightNovel",
			//"en/ReadNovelFull",
			//"en/VipNovel",
			//"en/VolareNovels",
			//"en/WuxiaWorld",
			//"jp/Syosetsu",
			//"pt/SaikaiScan",
			//"zn/15doc",
			//"zn/Tangsanshu"
	).map { "src/main/resources/src/$it.lua" }

	// END CONFIG

	private val globals = shosetsuGlobals()
	private fun loadScript(file: File, source_pre: String = "ext"): LuaValue {
		val l = try {
			globals.load(file.readText(), "$source_pre(${file.name})")!!
		} catch (e: Error) {
			throw e
		}

		return l.call()!!
	}

	@Suppress("ConstantConditionIf")
	private fun showListing(fmt: IExtension, novels: Array<Novel.Listing>) {
		if (PRINT_LISTINGS) {
			println("$CPURPLE[")
			print(novels.joinToString(", ") { it.toString() })
			println("]$CRESET")
		}

		println("${novels.size} novels.")
		if (PRINT_LIST_STATS) {
			print("${novels.count { it.title == "" }} with no title, ")
			print("${novels.count { it.link == "" }} with no link, ")
			print("${novels.count { it.imageURL == "" }} with no image url.")
			println()
		}

		println()

		var selectedNovel = 0
		println(novels[selectedNovel].link)
		var novel = fmt.parseNovel(novels[selectedNovel].link, true)
		while (novel.chapters.isEmpty()) {
			println("$CRED Chapters are empty, trying next novel $CRESET")
			selectedNovel++
			novel = fmt.parseNovel(novels[selectedNovel].link, true)
		}

		if (PRINT_NOVELS)
			println(novel)

		if (PRINT_NOVEL_STATS)
			println("${novel.title} - ${novel.chapters.size} chapters.")

		println()
		val passage = fmt.getPassage(novel.chapters[0].link)
		if (PRINT_PASSAGES)
			println("Passage:\t$passage")
		else
			println(with(passage) {
				if (length < 25) "Result: $this"
				else "$length chars long result: " +
						"${take(10)} [...] ${takeLast(10)}"
			})
	}

	@Suppress("UNCHECKED_CAST")
	fun Array<Filter<*>>.printOut(indent: Int = 0) {
		forEach {
			val tabs = StringBuilder("\t").apply { for (i in 0 until indent) this.append("\t") }
			val name = it.javaClass.simpleName.let {
				if (it.length > 7)
					it.substring(0, 6)
				else it
			}
			println("$tabs>${name}\t[${it.id}]\t${it.name}\t={${it.state?.javaClass?.simpleName}}")
			when (it) {
				is Filter.List -> {
					it.filters.printOut(indent + 1)
				}
				is Filter.Group<*> -> {
					(it.filters as Array<Filter<*>>).printOut(indent + 1)
        }
				else -> { }
			}
		}
	}

	/** Resets the color of a line */
	private const val CRESET: String = "\u001B[0m"
	private const val CCYAN: String = "\u001B[36m"
	private const val CPURPLE: String = "\u001B[35m"
	private const val CRED: String = "\u001B[31m"

	@JvmStatic
	@Throws(java.io.IOException::class, InterruptedException::class)
	fun main(args: Array<String>) {
		try {
			ShosetsuLuaLib.libLoader = {
				loadScript(File("src/main/resources/lib/$it.lua"), "lib")
			}
			ShosetsuLuaLib.httpClient = OkHttpClient.Builder().addInterceptor {
				it.proceed(it.request().also { request ->
					println(request.url.toUrl().toString())
				})
			}.build()

			if (PRINT_REPO_INDEX)
				println(RepoIndex(
						File("src/main/resources/index.json").readText()
				))

			for (extensionPath in SOURCES) {
				println("\n\n========== $extensionPath ==========")

				val extension = LuaExtension(File(extensionPath))

				val settingsModel: Map<Int, *> =
						extension.settingsModel.also {
							println("Settings model:")
							it.printOut()
						}.mapify()
				val searchFiltersModel: Map<Int, *> =
						extension.searchFiltersModel.also {
							println("SearchFilters Model:")
							it.printOut()
						}.mapify()

				println(CCYAN)
				println("ID       : ${extension.formatterID}")
				println("Name     : ${extension.name}")
				println("BaseURL  : ${extension.baseURL}")
				println("Image    : ${extension.imageURL}")
				println("Settings : $settingsModel")
				println("Filters  : $searchFiltersModel")
				if (PRINT_META_DATA)
					println("MetaData : ${extension.exMetaData}")
				println(CRESET)

				extension.listings.forEach { l ->
					with(l) {
						print("\n-------- Listing \"${name}\" ")
						print(if (isIncrementing) "(incrementing)" else "")
						println(" --------")

						var novels = getListing(
								HashMap(searchFiltersModel).apply {
									this[PAGE_INDEX] =
											if (isIncrementing) 1 else null

								}
						)

						if (isIncrementing)
							novels += getListing(HashMap(searchFiltersModel)
									.apply {
										this[PAGE_INDEX] = 2
									})

						if (REPEAT) {
							novels = getListing(
									HashMap(searchFiltersModel).apply {
										this[PAGE_INDEX] =
												if (isIncrementing) 1 else null

									}
							)

							if (isIncrementing)
								novels += getListing(HashMap(searchFiltersModel)
										.apply {
											this[PAGE_INDEX] = 2
										})
						}


						showListing(extension, novels)
						try {
							MILLISECONDS.sleep(500)
						} catch (e: InterruptedException) {
							e.printStackTrace()
						}
					}
				}

				if (extension.hasSearch) {
					println("\n-------- Search --------")
					showListing(
							extension,
							extension.search(
									HashMap(searchFiltersModel).apply {
										set(QUERY_INDEX, SEARCH_VALUE)
										set(PAGE_INDEX, 0)
									}
							)
					)
					if (extension.isSearchIncrementing) {
						showListing(
								extension,
								extension.search(
										HashMap(searchFiltersModel).apply {
											set(QUERY_INDEX, SEARCH_VALUE)
											set(PAGE_INDEX, 2)
										}
								)
						)
					}
				}

				MILLISECONDS.sleep(500)
			}
			println("\n\tTESTS COMPLETE")
		} catch (e: Exception) {
			e.printStackTrace()
			e.message?.let {
				print(CRED)
				print(it.substring(it.lastIndexOf("}") + 1))
				println(CRESET)
			}
		}
	}

}
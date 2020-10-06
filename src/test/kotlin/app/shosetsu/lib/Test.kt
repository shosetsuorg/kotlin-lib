package app.shosetsu.lib

import app.shosetsu.lib.lua.LuaExtension
import app.shosetsu.lib.lua.ShosetsuLuaLib
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
 * In IDEA, The Classpath should be shosetsu-services but the Working directory should be shosetsu-extensions.
 */
object Test {
	// CONFIG
	private const val SEARCH_VALUE = "world"
	private const val PRINT_LISTINGS = true
	private const val PRINT_LIST_STATS = true
	private const val PRINT_NOVELS = true
	private const val PRINT_NOVEL_STATS = true
	private const val PRINT_PASSAGES = true

	private val SOURCES: List<String> = arrayOf(
			//"en/BestLightNovel",
			//"en/BoxNovel",
			//"en/CreativeNovels",
			//"en/FastNovel", -- line 54, nil
			//"en/Foxaholic", //Needs to use ajax to get chapters, Investigate `action=manga_get_chapters&manga=######`
			//"en/KissLightNovels",
			//"en/MNovelFree", //Doesn't seem to be a novelfull
			//"en/MTLNovel",
			//"en/NovelFull",
			//"en/NovelTrench", // --:70 attempt to concatenate string and boolean
			//"en/ReadNovelForLife", // Ded site
			//"en/ReadNovelFull", // Meta offset issue?
			//"en/VipNovel",
			//"en/VolareNovels",
			//"en/WuxiaWorld",
			//"jp/Syosetsu",
			//"pt/SaikaiScan", -- Removed search query
			//"zn/15doc",
			//"zn/Tangsanshu"
	).map { "src/main/resources/src/$it.lua" }

	private val REPORTER: (String) -> Unit = { println("Progress: $it") }
	// END CONFIG

	private val globals = shosetsuGlobals()
	private fun loadScript(file: File): LuaValue {
		val l = try {
			globals.load(file.readText())!!
		} catch (e: Error) {
			throw e
		}

		return l.call()!!
	}

	@Suppress("ConstantConditionIf")
	private fun showListing(fmt: IExtension, novels: Array<Novel.Listing>) {
		if (PRINT_LISTINGS)
			println("[" + novels.joinToString(", ") { it.toString() } + "]")

		println("${novels.size} novels.")
		if (PRINT_LIST_STATS)
			println("${novels.count { it.title == "" }} with no title, ${novels.count { it.link == "" }} with no link, ${novels.count { it.imageURL == "" }} with no image url.")

		println()

		val novel = fmt.parseNovel(novels[0].link, true, REPORTER)
		if (PRINT_NOVELS) println(novel)
		if (PRINT_NOVEL_STATS) println("${novel.title} - ${novel.chapters.size} chapters.")

		println()

		val passage = fmt.getPassage(novel.chapters[0].link)
		if (PRINT_PASSAGES)
			println("Passage:\t$passage")
		else
			println(with(passage) {
				if (length < 25) "Result: $this"
				else "$length chars long result: ${take(10)} [...] ${takeLast(10)}"
			})
	}

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
					it.filters.map { it as Filter<*> }.toTypedArray().printOut(indent + 1)
				}
				else -> {
				}
			}
		}
	}

	@JvmStatic
	@Throws(java.io.IOException::class, InterruptedException::class)
	fun main(args: Array<String>) {
		try {
			ShosetsuLuaLib.libLoader = { loadScript(File("src/main/resources/lib/$it.lua")) }
			ShosetsuLuaLib.httpClient = OkHttpClient.Builder().addInterceptor {
				it.proceed(it.request().also { request -> println(request.url.toUrl().toString()) })
			}.build()

			for (format in SOURCES) {
				println("\n\n========== $format ==========")

				val formatter = LuaExtension(File(format))
				val settingsModel: Map<Int, *> = formatter.settingsModel.also {
					println("Settings model:")
					it.printOut()
				}.mapify()
				val searchFiltersModel: Map<Int, *> = formatter.searchFiltersModel.also {
					println("SearchFilters Model:")
					it.printOut()
				}.mapify()

				println("ID       : ${formatter.formatterID}")
				println("Name     : ${formatter.name}")
				println("BaseURL  : ${formatter.baseURL}")
				println("Image    : ${formatter.imageURL}")
				println("Settings : $settingsModel")
				println("Filters  : $searchFiltersModel")
				formatter.listings.forEach { l ->
					with(l) {
						println("\n-------- Listing \"${name}\" ${if (isIncrementing) "(incrementing)" else ""} --------")
						var novels = getListing(
								HashMap(searchFiltersModel).apply {
									this[PAGE_INDEX] = if (isIncrementing) 1 else null
								}
						)

						if (isIncrementing)
							novels += getListing(HashMap(searchFiltersModel).apply {
								this[PAGE_INDEX] = 2
							})

						showListing(formatter, novels)
						try {
							MILLISECONDS.sleep(500)
						} catch (e: InterruptedException) {
							e.printStackTrace()
						}
					}
				}

				if (formatter.hasSearch) {
					println("\n-------- Search --------")
					showListing(
							formatter,
							formatter.search(
									HashMap(searchFiltersModel).apply {
										set(QUERY_INDEX, SEARCH_VALUE)
										set(PAGE_INDEX, 0)
									}
							)
					)
					if (formatter.isSearchIncrementing) {
						showListing(
								formatter,
								formatter.search(
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
				print("\n\u001B[31m${it.substring(it.lastIndexOf("}") + 1)}\n")
			}
		}
	}

}
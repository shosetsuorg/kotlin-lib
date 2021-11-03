package app.shosetsu.lib

import org.junit.Before
import org.junit.Test
import kotlin.system.measureNanoTime

/**
 * shosetsu-kotlin-lib
 * 03 / 11 / 2021
 */
class EnumerationSpeed {
	data class SpeedResult(
		val timings: Map<Int, Long>,
		val average: Long
	)

	private fun singleNovelStatusSpeed(): SpeedResult {
		val resultTimes = HashMap<Int, Long>()
		for (i in -1..2) {
			resultTimes[i] = measureNanoTime { Novel.Status.fromInt(i) }
		}

		val averageTime = resultTimes.values.let { it.sum() / it.size }
		return SpeedResult(resultTimes, averageTime)
	}

	private fun singleChapterTypeSpeed(): SpeedResult {
		val resultTimes = HashMap<Int, Long>()
		for (i in -1..2) {
			resultTimes[i] = measureNanoTime { Novel.Status.fromInt(i) }
		}

		val averageTime = resultTimes.values.let { it.sum() / it.size }
		return SpeedResult(resultTimes, averageTime)
	}

	@Before
	fun warmUp() {
		println("Warming compiler up")
		singleNovelStatusSpeed()
		singleChapterTypeSpeed()
	}

	@Test
	fun speedTest() {
		run {
			println("Novel status speed test, single")
			val result = singleNovelStatusSpeed()
			println("Find times: ${result.timings}")
			println("Average find time: ${result.average}ns")
			println()
		}

		run {
			println("Chapter type speed test, single")
			val result = singleChapterTypeSpeed()
			println("Find times: ${result.timings}")
			println("Average find time: ${result.average}ns")
			println()
		}
	}

	@Test
	fun repeatSpeedTest() {
		println("Beginning repeat tests")
		val repeatCount = 100
		println("Repeating each $repeatCount times")
		println()

		run {
			println("Novel status speed test, multi")
			val results = ArrayList<SpeedResult>()

			for (i in 0..repeatCount) {
				results.add(singleNovelStatusSpeed())
			}

			println("Accumulated timings: ${results.map { it.timings }}")
			println("Total averages: ${results.let { list -> list.sumOf { it.average } / list.size }}ns")
			println()
		}

		run {
			println("Chapter type speed test, multi")
			val results = ArrayList<SpeedResult>()

			for (i in 0..repeatCount) {
				results.add(singleChapterTypeSpeed())
			}

			println("Accumulated timings: ${results.map { it.timings }}")
			println("Total averages: ${results.let { list -> list.sumOf { it.average } / list.size }}ns")
			println()
		}
	}
}
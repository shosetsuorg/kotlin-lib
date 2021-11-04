package app.shosetsu.lib.kts
/*
import app.shosetsu.lib.ShosetsuSharedLib.httpClient
import app.shosetsu.lib.exceptions.HTTPException
import app.shosetsu.lib.lua.ShosetsuLuaLib.LibFunctions.RequestDocument
import okhttp3.*
import okhttp3.internal.closeQuietly
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.concurrent.TimeUnit

/**
 * shosetsu-kotlin-lib
 * 06 / 10 / 2020
 */
object ShosetsuKtsLib {
	val defaultCacheControl: CacheControl
		get() = CacheControl.Builder().maxAge(10, TimeUnit.MINUTES).build()

	val defaultHeaders: Headers
		get() = Headers.Builder().build()

	val defaultBody: RequestBody
		get() = FormBody.Builder().build()


	// For normal extensions, these simple functions are sufficient.
	fun get(url: String, headers: Headers, cacheControl: CacheControl): Request =
		Request.Builder().url(url).headers(headers).cacheControl(cacheControl).build()

	fun post(url: String, headers: Headers, body: RequestBody, cacheControl: CacheControl): Request =
		Request.Builder().url(url).post(body).headers(headers).cacheControl(cacheControl).build()


	fun parseDocument(content: String): Document = Jsoup.parse(content)!!

	fun requestResponse(request: Request): Response = httpClient.newCall(request).execute()

	fun requestDocument(request: Request): Document = Document(
		requestResponse(request).let { r ->
			r.takeIf { it.code == 200 }?.body?.string() ?: run {
				r.closeQuietly()
				throw HTTPException(r.code)
			}
		}
	)

	fun getDocument(url: String): Document = RequestDocument(get(url, defaultHeaders, defaultCacheControl))
}

 */
package subsonic

import org.http4k.client.JavaHttpClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.format.ConfigurableKotlinxSerialization
import org.http4k.format.KotlinxSerialization.asA
import org.http4k.lens.LensFailure
import subsonic.api.SearchResult2Response
import subsonic.api.SubsonicBareResponse
import subsonic.api.SubsonicChild
import subsonic.api.SubsonicResponse
import util.Result

class SubsonicClient(subsonicUrl: String, subsonicToken: String) {
    private val applicationName = "PlexRatingToMB/0.1.0 ( defvs.daniel@gmail.com )"
    private val subsonicVersion = "1.16.0"

    private val baseURL = "$subsonicUrl/rest"
    private val getClient = JavaHttpClient()
    private val json = ConfigurableKotlinxSerialization({
        ignoreUnknownKeys = true
        explicitNulls = false
    })
    private val baseQueries = listOf(
        "f" to "json",
        "apiKey" to subsonicToken,
        "v" to subsonicVersion,
        "c" to applicationName,
    )

    private inline fun <reified R : Any> get(subUrl: String, vararg queries: Pair<String, String>) = getClient(
        Request(Method.GET, "$baseURL$subUrl")
            .let {
                (queries.toList() + baseQueries)
                    .fold(it) { request, query -> request.query(query.first, query.second) }
            }
            .header("Accept", "application/json")
            .header("User-Agent", applicationName)
    ).run {
        if (status != Status.OK) Result.ErrorResult(status, this.bodyString())
        else try {
            Result.DataResult(json.autoBody<SubsonicResponse>().toLens()(this).subsonicResponse.asA<R>())
        } catch (e: LensFailure) {
            Result.ExceptionResult(e, this.bodyString())
        }
    }.resolve<R>()

    fun getAllTracks(): ArrayList<SubsonicChild> {
        val allTracks = arrayListOf<SubsonicChild>()
        var offset = 0

        do {
            val (status, result) = get<SearchResult2Response>(
                "/search2",
                "query" to "",
                "artistCount" to "0",
                "albumCount" to "0",
                "songCount" to "200",
                "songOffset" to "$offset",
            )
                .let { it.status to it.searchResult2 }
                .also { (_, result) -> result?.song?.let { allTracks.addAll(it) } }
            println("Offset: $offset")
            offset += 200
        } while (status == "ok" && result?.song?.isNotEmpty() == true)

        return allTracks
    }

    fun submitRatings(ratings: Map<String, Int>) {
        ratings.forEach { (id, rating) ->
            get<SubsonicBareResponse>(
                "/setRating",
                "id" to id,
                "rating" to "$rating",
            )
        }
    }
}

package musicbrainz

import musicbrainz.api.Release
import org.http4k.client.JavaHttpClient
import org.http4k.core.*
import org.http4k.filter.ClientFilters
import org.http4k.filter.DigestAuth
import org.http4k.format.ConfigurableKotlinxSerialization
import org.http4k.lens.LensFailure
import org.http4k.lens.contentType
import util.Result

class MusicBrainzClient(
    private val username: String,
    private val password: String,
) {
    private val baseURL = "https://musicbrainz.org/ws/2"
    private val getClient = JavaHttpClient()
    private val postClient = ClientFilters.DigestAuth(Credentials(username, password)).then(JavaHttpClient())
    private val json = ConfigurableKotlinxSerialization({
        ignoreUnknownKeys = true
        explicitNulls = false
//        isLenient = true
    })

    private inline fun <reified R : Any> get(subUrl: String, vararg queries: Pair<String, String>) = getClient(
        Request(Method.GET, "$baseURL$subUrl")
            .let {
                queries.fold(it) { request, query -> request.query(query.first, query.second) }
            }
            .header("Accept", "application/json")
            .header("User-Agent", "PlexRatingToMB/0.1.0 ( defvs.daniel@gmail.com )")
    ).run {
        if (status != Status.OK) Result.ErrorResult(status, this.bodyString())
        else try {
            Result.DataResult(json.autoBody<R>().toLens()(this))
        } catch (e: LensFailure) {
            Result.ExceptionResult(e, this.bodyString())
        }
    }.resolve<R>()

    private fun post(subUrl: String, body: String, contentType: ContentType) = postClient(
        Request(Method.POST, "$baseURL$subUrl")
            .query("client", "PlexRatingToMB-0.1.0")
            .header("User-Agent", "PlexRatingToMB/0.1.0 ( defvs.daniel@gmail.com )")
            .contentType(contentType)
            .body(body)
    ).status

    fun trackToRecordingMBID(trackMbid: String) =
        get<Release.ReleaseResponse>("/release", Pair("track", trackMbid)).releases.single()
            .media.flatMap { it.tracks }.single { it.id == trackMbid }.recording.id

    fun submitRatings(ratings: Map<String, Int>) =
        """
            <metadata xmlns="http://musicbrainz.org/ns/mmd-2.0#"><recording-list>
                ${ratings.entries.joinToString (separator = "") {
                    """<recording id="${it.key}"><user-rating>${it.value}</user-rating></recording>"""
                }}
            </recording-list></metadata>
        """.let { post("/rating", it, ContentType.APPLICATION_XML) }
}
package plex

import plex.api.Libraries
import plex.api.LibrarySectionResponse
import plex.api.LibraryTypes
import plex.api.TrackResponse
import org.http4k.client.JavaHttpClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.format.ConfigurableKotlinxSerialization
import org.http4k.lens.LensFailure
import util.Result

class PlexClient(
    private val baseURL: String,
    private val token: String,
) {
    private val json = ConfigurableKotlinxSerialization({
        ignoreUnknownKeys = true
        explicitNulls = false
//        isLenient = true
    })
    private val client = JavaHttpClient()

    private inline fun <reified R : Any> get(subUrl: String, vararg queries: Pair<String, String>) = client(
        Request(Method.GET, "$baseURL$subUrl")
            .query("X-Plex-Token", token)
            .let {
                queries.fold(it) { request, query -> request.query(query.first, query.second) }
            }
            .header("Accept", "application/json")
    ).run {
        if (status != Status.OK) Result.ErrorResult(status, this.bodyString())
        else try {
            Result.DataResult(json.autoBody<R>().toLens()(this))
        } catch (e: LensFailure) {
            Result.ExceptionResult(e, this.bodyString())
        }
    }.resolve<R>()

    fun getLibraries(vararg libraryTypes: LibraryTypes = LibraryTypes.entries.toTypedArray()) =
        get<Libraries.LibrariesWrapper>("/library/sections").libraries.directories.filter { library ->
            library.type in libraryTypes.map { it.typeString }
        }

    private fun getLibraryTracksPaged(libraryId: String, limit: Int, offset: Int) =
        get<LibrarySectionResponse.LibrarySectionResponseWrapper>(
            "/library/sections/$libraryId/all",
            Pair("X-Plex-Container-Size", limit.toString()),
            Pair("X-Plex-Container-Start", offset.toString()),
            Pair("type", "10")
        ).response

    fun getLibraryTracks(
        libraryId: String,
        startupCallback: (totalSize: Int) -> Unit = {},
        runningCallback: (processed: Int) -> Unit = {},
    ): List<TrackResponse.Track> {
        val limit = 100
        var offset = 0
        val tracks = mutableListOf<TrackResponse.Track>()

        val totalSize = getLibraryTracksPaged(libraryId, 1, 0).totalSize
        startupCallback(totalSize)

        while (offset < totalSize) {
            val response = getLibraryTracksPaged(libraryId, limit, offset)
            runningCallback(offset)
            tracks.addAll(response.tracks)
            offset += limit
        }

        return tracks
    }

    fun getTrack(ratingKey: String) =
        get<TrackResponse.TrackResponseWrapper>("/library/metadata/$ratingKey").response.tracks.single()
}
package plex.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LibrarySectionResponse(
    val size: Int,
    val totalSize: Int,
    val offset: Int,
    val librarySectionID: Int,
    val title1: String,
    @SerialName("Metadata") val tracks: List<TrackResponse.Track>,
) {
    @Serializable
    data class LibrarySectionResponseWrapper(@SerialName("MediaContainer") val response: LibrarySectionResponse)
}

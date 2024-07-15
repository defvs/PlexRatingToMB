package musicbrainz.api

import kotlinx.serialization.Serializable

@Serializable
data class Release(
    val media: List<Media>,
) {
    @Serializable
    data class ReleaseResponse(
        val releases: List<Release>,
    )

    @Serializable
    data class Media(
        val tracks: List<Track>,
    ) {
        @Serializable
        data class Track(
            val id: String,
            val recording: Recording,
        ) {
            @Serializable
            data class Recording(
                val id: String,
            )
        }
    }
}

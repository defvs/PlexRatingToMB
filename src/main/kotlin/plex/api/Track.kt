package plex.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class TrackResponse(
    val size: Int,
    val allowSync: Boolean,
    val identifier: String,
    val librarySectionID: Int,
    val librarySectionTitle: String,
    val librarySectionUUID: String,
    val mediaTagPrefix: String,
    val mediaTagVersion: Int,
    @SerialName("Metadata") val tracks: List<Track>,
) {
    @Serializable
    data class TrackResponseWrapper(@SerialName("MediaContainer") val response: TrackResponse)

    @Serializable
    data class Track(
        val ratingKey: String? = null,
        val key: String? = null,
        val parentRatingKey: String? = null,
        val grandparentRatingKey: String? = null,
        val guid: String? = null,
        val parentGuid: String? = null,
        val grandparentGuid: String? = null,
        val parentStudio: String? = null,
        val type: String? = null,
        val title: String? = null,
        val grandparentKey: String? = null,
        val parentKey: String? = null,
        val librarySectionTitle: String? = null,
        val librarySectionID: Int? = null,
        val librarySectionKey: String? = null,
        val grandparentTitle: String? = null,
        val parentTitle: String? = null,
        val summary: String? = null,
        val index: Int? = null,
        val parentIndex: Int? = null,
        val ratingCount: Int? = null,
        val userRating: Double? = null,
        val lastRatedAt: Long? = null,
        val parentYear: Int? = null,
        val thumb: String? = null,
        val parentThumb: String? = null,
        val grandparentThumb: String? = null,
        val duration: Int? = null,
        val addedAt: Long? = null,
        val updatedAt: Long? = null,
        val musicAnalysisVersion: String? = null,
        @SerialName("Media") val medias: List<Media>? = null,
        @SerialName("Guid") val guids: List<Guid>? = null,
    ) {
        @Serializable
        data class Media(
            val id: Int? = null,
            val duration: Int? = null,
            val bitrate: Int? = null,
            val audioChannels: Int? = null,
            val audioCodec: String? = null,
            val container: String? = null,
            @SerialName("Part") val parts: List<Part>? = null,
        ) {
            @Serializable
            data class Part(
                val id: Int? = null,
                val key: String? = null,
                val duration: Int? = null,
                val file: String? = null,
                val size: Long? = null,
                val container: String? = null,
                @SerialName("Stream") val streams: List<Stream>? = null,
            ) {
                @Serializable
                data class Stream(
                    val id: Int? = null,
                    val streamType: Int? = null,
                    val selected: Boolean? = null,
                    val codec: String? = null,
                    val index: Int? = null,
                    val channels: Int? = null,
                    val bitrate: Int? = null,
                    val albumGain: String? = null,
                    val albumPeak: String? = null,
                    val albumRange: String? = null,
                    val audioChannelLayout: String? = null,
                    val bitDepth: Int? = null,
                    val gain: String? = null,
                    val loudness: String? = null,
                    val lra: String? = null,
                    val peak: String? = null,
                    val samplingRate: Int? = null,
                    val displayTitle: String? = null,
                    val extendedDisplayTitle: String? = null,
                )
            }
        }

        @Serializable
        data class Guid(
            val id: String,
        )

        @Transient
        val trackMbid = this.guids?.firstOrNull { it.id.startsWith("mbid://") }?.id?.removePrefix("mbid://")

        @Transient
        val path = this.medias?.singleOrNull()?.parts?.singleOrNull()?.file
    }
}

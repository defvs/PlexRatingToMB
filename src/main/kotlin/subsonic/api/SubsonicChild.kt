package subsonic.api

import kotlinx.serialization.Serializable

@Serializable
data class SubsonicChild(
    val id: String,
    val isDir: Boolean,
    val title: String,
    val userRating: Int = 0,
    val musicBrainzId: String? = null,
)

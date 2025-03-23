package subsonic.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
data class SearchResult2(
    val artist: JsonArray? = null,
    val album: JsonArray? = null,
    val song: List<SubsonicChild> = emptyList(),
)


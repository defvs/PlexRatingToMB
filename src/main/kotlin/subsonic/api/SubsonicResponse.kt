package subsonic.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SubsonicResponse(
    @SerialName("subsonic-response") val subsonicResponse: JsonObject,
)

@Serializable
data class SearchResult2Response(
    val status: String,
    val version: String,
    val type: String,
    val openSubsonic: Boolean,
    val searchResult2: SearchResult2? = null,
)

@Serializable
data class SubsonicBareResponse(
    val status: String,
    val version: String,
    val type: String,
    val openSubsonic: Boolean,
)

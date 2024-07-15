package plex.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Libraries(
    @SerialName("Directory") val directories: List<Directory>,
    val allowSync: Boolean,
    val size: Int,
    val title1: String,
) {
    @Serializable
    data class LibrariesWrapper(@SerialName("MediaContainer") val libraries: Libraries)

    @Serializable
    data class Directory(
        @SerialName("Location") val locations: List<Location>,
        val agent: String,
        val allowSync: Boolean,
        val art: String,
        val composite: String,
        val content: Boolean,
        val contentChangedAt: Long,
        val createdAt: Long,
        val directory: Boolean,
        val filters: Boolean,
        val hidden: Int,
        val key: String,
        val language: String,
        val refreshing: Boolean,
        val scannedAt: Long,
        val scanner: String,
        val thumb: String,
        val title: String,
        val type: String,
        val updatedAt: Long,
        val uuid: String,
    ) {
        @Serializable
        data class Location(
            val id: Int,
            val path: String,
        )
    }
}
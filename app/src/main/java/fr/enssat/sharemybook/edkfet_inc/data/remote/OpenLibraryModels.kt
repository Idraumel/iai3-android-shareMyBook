package fr.enssat.sharemybook.edkfet_inc.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenLibraryResponse(
    val records: Map<String, OpenLibraryRecord>
)

@Serializable
data class OpenLibraryRecord(
    @SerialName("thumbnail_url")
    val thumbnailUrl: String?,
    val details: OpenLibraryBookDetails
)

@Serializable
data class OpenLibraryBookDetails(
    val title: String,
    val authors: List<OpenLibraryAuthor> = emptyList()
)

@Serializable
data class OpenLibraryAuthor(
    val name: String
)

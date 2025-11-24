package fr.enssat.sharemybook.edkfet_inc.data.remote

import android.util.Log
import fr.enssat.sharemybook.edkfet_inc.model.Book
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class OpenLibraryService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getBookByIsbn(isbn: String, ownerUuid: String): Book? {
        return try {
            val response: OpenLibraryResponse = client.get("https://openlibrary.org/api/volumes/brief/isbn/$isbn.json").body()
            val record = response.records.values.firstOrNull()
            if (record != null) {
                val details = record.details
                Book(
                    ownerUuid = ownerUuid,
                    isbn = isbn,
                    title = details.title,
                    authors = details.authors.joinToString(", ") { it.name },
                    coverUrl = record.thumbnailUrl
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("OpenLibraryService", "Failed to fetch book data", e)
            null
        }
    }
}

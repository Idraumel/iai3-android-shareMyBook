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
            Log.d("OpenLibraryService", "Fetching book data for ISBN: $isbn")
            val response: OpenLibraryResponse = client.get("https://openlibrary.org/api/volumes/brief/isbn/$isbn.json").body()
            val record = response.records.values.firstOrNull()
            if (record != null) {
                val details = record.details
                // Convert HTTP cover URLs to HTTPS for better compatibility
                val coverUrl = record.thumbnailUrl?.replace("http://", "https://") ?: ""
                Log.d("OpenLibraryService", "Book found: ${details.title}, Cover URL: $coverUrl")
                Book(
                    ownerUuid = ownerUuid,
                    isbn = isbn,
                    title = details.title ?: "Titre inconnu", // Ensure title is never null
                    authors = details.authors.joinToString(", ") { it.name }.ifEmpty { "Auteur inconnu" }, // Ensure authors is never empty
                    coverUrl = coverUrl
                )
            } else {
                Log.w("OpenLibraryService", "No records found for ISBN: $isbn")
                null
            }
        } catch (e: Exception) {
            Log.e("OpenLibraryService", "Failed to fetch book data for ISBN: $isbn", e)
            null
        }
    }
}

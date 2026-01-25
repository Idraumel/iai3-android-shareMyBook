package fr.enssat.sharemybook.edkfet_inc.data.remote

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BackendService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                encodeDefaults = true // Ensure all fields are sent
                prettyPrint = true
                isLenient = true
            })
        }

        engine {
            requestTimeout = 30000 // 30 seconds
        }
    }

    private val baseUrl = "https://europe-west9-mythic-cocoa-442917-i7.cloudfunctions.net/shareMyBook"

    suspend fun initTransaction(request: TransactionInitRequest): String? {
        return try {
            Log.d("BackendService", "Sending init request to: $baseUrl/init")
            Log.d("BackendService", "Request body: ${Json.encodeToString(TransactionInitRequest.serializer(), request)}")

            val response = client.post("$baseUrl/init") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val responseBody = response.bodyAsText()
            Log.d("BackendService", "Response status: ${response.status}")
            Log.d("BackendService", "Response body: $responseBody")

            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                // Extract only the shareId string from the JSON response
                val shareId = Json.decodeFromString<ShareResponse>(responseBody).shareId
                Log.d("BackendService", "Init successful: $shareId")
                shareId
            } else {
                Log.e("BackendService", "Init failed: ${response.status} - $responseBody")
                null
            }
        } catch (e: Exception) {
            Log.e("BackendService", "Init exception: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    suspend fun acceptTransaction(shareId: String, request: TransactionAcceptRequest): TransactionResponse {
        return try {
            val response = client.post("$baseUrl/accept/$shareId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                val errorBody = response.bodyAsText()
                Log.e("BackendService", "Accept failed: ${response.status} - $errorBody")
                throw Exception("Code invalide ou transaction expirée (${response.status})")
            }
        } catch (e: Exception) {
            Log.e("BackendService", "Accept exception: ${e.message}", e)
            throw e
        }
    }

    suspend fun getResult(shareId: String): TransactionResponse {
        return try {
            val response = client.get("$baseUrl/result/$shareId")

            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                val errorBody = response.bodyAsText()
                Log.e("BackendService", "GetResult failed: ${response.status} - $errorBody")
                throw Exception("Transaction non trouvée ou expirée")
            }
        } catch (e: Exception) {
            Log.e("BackendService", "GetResult exception: ${e.message}", e)
            throw e
        }
    }
}

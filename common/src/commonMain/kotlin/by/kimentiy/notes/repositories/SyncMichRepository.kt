package by.kimentiy.notes.repositories

import by.kimentiy.notes.network.httpClient
import com.google.gson.Gson
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class SyncMichRepository : SyncRepository {

    private val address = Url("https://mmich.online:3000/fullsync")
    private val client = httpClient()
    private val gson = Gson()

    override suspend fun syncNotes(): List<Note> = withContext(Dispatchers.IO) {
        val json = client.post {
            contentType(ContentType.Application.Json)
            setBody("{}")
            url(address.toString())
        }.bodyAsText()

        println(json)

        val response = gson.fromJson(json, MichResponse::class.java)

        response.news.map {
            Note(
                id = Id(it.id),
                title = it.name,
                description = it.text,
                scn = it.scn,
                lastModified = Clock.System.now()
            )
        }
    }
}

data class MichResponse(
    val news: List<RemoteNote>
)

data class RemoteNote(
    val id: Long,
    val name: String,
    val text: String,
    val scn: Long,
    val action: String,
    val modifiedAt: String
)

package by.kimentiy.notes.repositories

import by.kimentiy.notes.network.httpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface SyncRepository {

    suspend fun createNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun getNotes(): Result<List<Note>>

    suspend fun deleteNote()
}

suspend fun makeSomeRequest(): String = withContext(Dispatchers.IO) {
    val address = Url("https://cors-test.appspot.com/test")
    val client = httpClient()

    client.get {
        url(address.toString())
    }.bodyAsText()
}

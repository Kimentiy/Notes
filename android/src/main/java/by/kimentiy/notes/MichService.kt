package by.kimentiy.notes

import retrofit2.Call
import retrofit2.http.GET

interface MichService {

    @GET("notes")
    fun getNotes(): Call<List<RemoteNote>>
}

data class RemoteNote(
    val id: String,
    val name: String,
    val description: String
)

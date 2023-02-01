package by.kimentiy.notes

import by.kimentiy.notes.repositories.Id
import by.kimentiy.notes.repositories.Note
import by.kimentiy.notes.repositories.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*


class SyncHttpRepository : SyncRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://mmich.online:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(getUnsafeOkHttpClient())
        .build()

    private val service = retrofit.create(MichService::class.java)

    override suspend fun createNote(note: Note) {
        TODO("Not yet implemented")
    }

    override suspend fun updateNote(note: Note) {
        TODO("Not yet implemented")
    }

    override suspend fun getNotes(): Result<List<Note>> = withContext(Dispatchers.IO) {
        val response = service.getNotes().execute()

        if (response.isSuccessful) {
            val notes = response.body()?.map {
                Note(
                    id = Id(it.id.toLong()),
                    title = it.name,
                    description = it.description
                )
            }

            if (notes == null) {
                Result.failure(java.lang.Exception("Body is null"))
            } else {
                Result.success(notes)
            }
        } else {
            Result.failure(
                Exception(
                    response.errorBody()?.string() ?: "Failed response with no error body"
                )
            )
        }
    }

    override suspend fun deleteNote() {
        TODO("Not yet implemented")
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) = Unit

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) = Unit

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL").apply {
                init(null, trustAllCerts, SecureRandom())
            }
            // Create an ssl socket factory with our all-trusting manager
            OkHttpClient.Builder().apply {
                sslSocketFactory(sslContext.socketFactory)
                hostnameVerifier { _, _ -> true }
            }.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
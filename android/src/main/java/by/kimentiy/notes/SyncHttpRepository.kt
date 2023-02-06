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


}
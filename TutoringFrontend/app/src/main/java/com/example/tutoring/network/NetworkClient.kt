package com.example.tutoring.network

import android.content.Context
import com.example.tutoring.utils.ErrorNotifier
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    private lateinit var appContext: Context

    // Call this method to initialize NetworkClient, which is called in onCreate of MainActivity
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }
//    private const val BASE_URL = "http://10.126.72.228:8080" // wifi ipv4 address for real phone
    //    private const val BASE_URL = "http://localhost:8080"
    private const val BASE_URL = "http://10.0.2.2:8080" // The emulator needs to be mapped to localhost using 10.0.2.2

    // Provide a place for external token setup
    var tokenProvider: () -> String? = {
        val sharedPrefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPrefs.getString("token", null)
    }

    // This command is used to handle global error messages
    var errorHandler: (String) -> Unit = { msg -> ErrorNotifier.showError(msg) }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor { tokenProvider() })
            .addInterceptor(ResponseInterceptor { errorHandler(it) })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            // Add a log blocker，output to Logcat
            .addInterceptor(logging)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())  // Parse JSON
            .build()
    }

    // Provides methods for creating API services
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}

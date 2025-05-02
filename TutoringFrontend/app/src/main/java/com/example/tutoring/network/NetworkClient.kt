package com.example.tutoring.network

import android.content.Context
import com.example.tutoring.utils.ErrorNotifier
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    lateinit var appContext: Context

    // Call this method to initialize NetworkClient, which is called in onCreate of MainActivity
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }
    //    private const val BASE_URL = "http://10.126.72.228:8080" // wifi ipv4 address for real phone
    //    private const val BASE_URL = "http://localhost:8080"
    //    private const val BASE_URL = "http://10.0.2.2:8080" // The emulator needs to be mapped to localhost using 10.0.2.2
    // ① Change the BASE_URL to variable
    private var baseUrl: String = "http://10.0.2.2:8080"

    // ② Expose a method externally to override the URL in the test
    fun overrideBaseUrl(newUrl: String) {
        baseUrl = newUrl
        // If you want it to take effect immediately, rebuild the retrofit instance as well
        retrofit = createRetrofit()
    }
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
    //    private val okHttpClient by lazy {
    //        OkHttpClient.Builder()
    //            .addInterceptor(TokenInterceptor { tokenProvider() })
    //            .addInterceptor(ResponseInterceptor { errorHandler(it) })
    ////            .connectTimeout(30, TimeUnit.SECONDS)
    ////            .readTimeout(30, TimeUnit.SECONDS)
    //            // The timeout for establishing the connection (default: 10 seconds)
    //            .connectTimeout(600, TimeUnit.SECONDS)
    //            // The read timeout when the server starts to respond after processing (default: 10 seconds)
    //            .readTimeout(600, TimeUnit.SECONDS)
    //            // Write timeout when sending the request body (default: 10 seconds)
    //            .writeTimeout(600, TimeUnit.SECONDS)
    //            // The timeout period of the entire call, 0 indicates no limit
    //            .callTimeout(0, TimeUnit.MILLISECONDS)
    //            // Add a log blocker，output to Logcat
    //            .addInterceptor(logging)
    //            .build()
    //    }
    // ③ don't initialize both retrofit and okHttpClient at once
    private fun createOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(TokenInterceptor { tokenProvider() })
        .addInterceptor(ResponseInterceptor { errorHandler(it) })
        // The timeout for establishing the connection (default: 10 seconds)
        .connectTimeout(600, TimeUnit.SECONDS)
        // The read timeout when the server starts to respond after processing (default: 10 seconds)
        .readTimeout(600, TimeUnit.SECONDS)
        // Write timeout when sending the request body (default: 10 seconds)
        .writeTimeout(600, TimeUnit.SECONDS)
        // The timeout period of the entire call, 0 indicates no limit
        .callTimeout(0, TimeUnit.MILLISECONDS)
        // Add a log blocker，output to Logcat
        .addInterceptor(logging)
    .build()

    private fun createRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(createOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    //    private val retrofit by lazy {
    //        Retrofit.Builder()
    //            .baseUrl(BASE_URL)
    //            .client(okHttpClient)
    //            .addConverterFactory(GsonConverterFactory.create())  // Parse JSON
    //            .build()
    //    }

    // ④ Change the original 'retrofit' to 'var' and initialize it here
    private var retrofit: Retrofit = createRetrofit()
    // Provides methods for creating API services
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}

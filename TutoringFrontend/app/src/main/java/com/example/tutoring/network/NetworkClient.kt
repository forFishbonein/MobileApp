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

    // 调用这个方法初始化 NetworkClient，一般在 Application 或 MainActivity 的 onCreate 中调用
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }
    // baseURL 可配置，也可写死
//    private const val BASE_URL = "http://10.126.72.228:8080" // 假设你的Spring Boot后端跑在本地
//    private const val BASE_URL = "http://localhost:8080" // 假设你的Spring Boot后端跑在本地
    private const val BASE_URL = "http://10.0.2.2:8080" // 假设你的Spring Boot后端跑在本地

    // 提供给外部设置 token 的地方
    // 每次调用时都会从 SharedPreferences 读取最新的 "token" 值
    var tokenProvider: () -> String? = {
        val sharedPrefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPrefs.getString("token", null)
    }

    // 用于处理全局错误提示
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
//             添加日志拦截器
            .addInterceptor(logging)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())  // 解析 JSON
            .build()
    }

    // 提供创建 API Service 的方法
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}

package com.example.tutoring.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 如果有 token，则在请求头加上 Authorization: Bearer {token}
 */
class TokenInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = tokenProvider()
        // 如果有 token 并且原请求头里没加 Authorization，就加上
        val newRequest = if (!token.isNullOrEmpty() && originalRequest.header("Authorization") == null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}

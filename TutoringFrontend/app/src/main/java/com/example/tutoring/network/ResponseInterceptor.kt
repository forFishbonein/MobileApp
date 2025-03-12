package com.example.tutoring.network

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import java.io.IOException

// Global processing Response
class ResponseInterceptor(
    private val onError: (String) -> Unit
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        // Read the body content (JSON)
        val rawBody = response.body?.string() ?: ""

        // change to JSONObject
        val json = try {
            JSONObject(rawBody)
        } catch (e: Exception) {
            throw IOException("The response is not valid JSON.")
        }

        val code = json.optInt("code", -1)
        val message = json.optString("message", "Unknown Error.")

        // If code! = 200 throws an exception
        if (code != 200) {
            // like errorNotifier.showError(...)
            onError(message)
            throw IOException("The back end returned an error，code=$code, message=$message")
        }

        // If code=200, replace the native response.body with the one we recreated
        // So that subsequent Retrofit converters can continue parsing the data
        val newBody = rawBody.toResponseBody(response.body?.contentType())
        return response.newBuilder()
            .body(newBody)
            .build()
    }
}

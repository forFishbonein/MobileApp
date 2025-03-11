package com.example.tutoring.network

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import java.io.IOException

/**
 * 全局处理后端返回，如果 code != 200 就抛出异常
 */
class ResponseInterceptor(
    private val onError: (String) -> Unit
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        // 读出 body 内容（一般是 JSON）
        val rawBody = response.body?.string() ?: ""

        // 你可能有自己的 JSON 结构，比如 { code:200, message:"xx", data:{} }
        // 这里做个简单解析
        val json = try {
            JSONObject(rawBody)
        } catch (e: Exception) {
            throw IOException("响应不是有效的 JSON")
        }

        val code = json.optInt("code", -1)
        val message = json.optString("message", "未知错误")

        if (code != 200) {
            // 仿 axios 中 errorNotifier.showError(...)
            onError(message)
            throw IOException("后端返回错误，code=$code, message=$message")
        }

        // 如果 code=200，就把原生 response.body 替换成我们重新创建的
        // 以便后续 Retrofit 的 Converter 继续解析 data
        val newBody = rawBody.toResponseBody(response.body?.contentType())
        return response.newBuilder()
            .body(newBody)
            .build()
    }
}

package com.example.tutoring.network

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

//data class LoginRequest(val username: String, val password: String)
//data class CommonResponse<T>(val code: Int, val message: String, val data: T)
// 定义一个通用响应结构，不需要为 data 部分单独建类
data class CommonResponse(
    val code: Int,
    val message: String,
//    val data: Map<String, Any>?
    val data: Any?
)

interface ApiService {

    @POST("/api/login")
    suspend fun login(@Body req: Map<String, String>): CommonResponse

    @GET("/api/courses")
    suspend fun getCourses(): CommonResponse

    @Multipart
    @POST("/api/upload") // 根据后端实际接口地址修改
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): CommonResponse
}

package com.example.tutoring.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
    @POST("/user/send-code")
    suspend fun sendCode(@Body request: Map<String, String>): CommonResponse

    @POST("/user/register")
    suspend fun register(@Body request: Map<String, String>): CommonResponse

    @POST("/user/login")
    suspend fun login(@Body request: Map<String, String>): CommonResponse

    @GET("/user/me")
    suspend fun getMyProfile(@Header("Authorization") token: String): CommonResponse
//    val token = "your_token_here" // 如 "Bearer xxx"
//    val response = apiService.getMyProfile("Bearer $token")
    @PUT("/user/me")
    suspend fun updateMyProfile(@Body request: Map<String, Any>): CommonResponse

    @Multipart
    @POST("/user/me/avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part
    ): CommonResponse



    @GET("/course/list")
    suspend fun listCourses(
        @Query("name") name: String?,
        @Query("subject") subject: String?
    ): CommonResponse
    // val response = apiService.listCourses(name = "Java", subject = "Backend")

    @POST("/course/register")
    suspend fun registerCourse(@Body request: Map<String, Any>): CommonResponse

    @GET("/lesson/course/{courseId}")
    suspend fun listLessons(
        @Path("courseId") courseId: Long
    ): CommonResponse

    @POST("/lesson/create")
    suspend fun createLesson(@Body request: Map<String, Any>): CommonResponse

    @GET("/lesson/{lessonId}")
    suspend fun getLesson(
        @Path("lessonId") lessonId: Long
    ): CommonResponse

    @PUT("/lesson/{lessonId}")
    suspend fun updateLesson(@Body request: Map<String, Any>): CommonResponse

    @POST("/lessonProgress/{lessonId}/complete")
    suspend fun completeLesson(
        @Path("lessonId") lessonId: Int
    ): CommonResponse


    @Multipart
    @POST("/oss/uploadImage") // 根据后端实际接口地址修改
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): CommonResponse

    @Multipart
    @POST("/oss/uploadPdf") // 根据后端实际接口地址修改
    suspend fun uploadPdf(
        @Part file: MultipartBody.Part
    ): CommonResponse


    //data science
    @GET("/course/{courseId}/progress")
    suspend fun getCourseProgress(
        @Path("courseId") courseId: Long
    ): CommonResponse
}

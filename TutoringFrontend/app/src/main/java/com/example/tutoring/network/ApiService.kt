package com.example.tutoring.network

import com.example.tutoring.data.AvailabilitySlotDetail
import com.example.tutoring.data.Course
import com.example.tutoring.data.CourseProgress
import com.example.tutoring.data.Lesson
import com.example.tutoring.data.LessonsProcess
import com.example.tutoring.data.Meeting
import com.example.tutoring.data.Registration
import com.example.tutoring.data.TutorInfo
import com.example.tutoring.ui.screens.student.BookMeetingRequest
import com.example.tutoring.ui.screens.tutor.AvailabilityRequest
import com.example.tutoring.ui.screens.tutor.AvailabilitySlot
import com.example.tutoring.ui.screens.tutor.CourseRegistration
import com.example.tutoring.ui.screens.tutor.LessonRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

// Define a common response structure
data class CommonResponse(
    val code: Int,
    val message: String,
// val data: Map<String, Any>?
    val data: Any?
)
data class SpecialResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
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

    @PUT("/user/me")
    suspend fun updateMyProfile(@Body request: Map<String, String>): CommonResponse

    @Multipart
    @POST("/user/me/avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part
    ): CommonResponse

    @GET("/course/list")
    suspend fun listCourses(
        @Query("name") name: String?,
        @Query("subject") subject: String?
    ): SpecialResponse<List<Course>>

    @POST("/course/create")
    suspend fun createCourse(@Body request: Map<String, String>): CommonResponse

    @PUT("/course/{courseId}")
    suspend fun updateCourse(@Body request: Map<String, String>, @Path("courseId") courseId: Int?): CommonResponse

    @DELETE("/course/{courseId}")
    suspend fun deleteCourse(@Path("courseId") courseId: Int?): CommonResponse

    @GET("/course/{courseId}")
    suspend fun getCourseDetail(
        @Path("courseId") courseId: Int
    ): SpecialResponse<Course>

    @GET("/course/tutor/list")
    suspend fun listTutorCourses(): SpecialResponse<List<CourseRegistration>>

    @GET("/course/registrations/student")
    suspend fun listStudentRegistrations(): SpecialResponse<List<Registration>>

    //below is for tutor
    @GET("/course/registrations")
    suspend fun listAllRegistrations(): SpecialResponse<List<Registration>>

    @POST("/course/register")
    suspend fun registerCourse(@Body request: Map<String, Int>): CommonResponse

    @PUT("/course/registrations/{registrationId}")
    suspend fun updateRegistration(@Body request: Map<String, String>, @Path("registrationId") registrationId: Int?): CommonResponse

    @GET("/lesson/course/{courseId}")
    suspend fun listLessons(
        @Path("courseId") courseId: Int?
    ): SpecialResponse<List<Lesson>>

    @GET("/lessonProgress/course/{courseId}/student/{studentId}")
    suspend fun getLessonProgressByCourseAndStudent(
        @Path("courseId") courseId: Int?,
        @Path("studentId") studentId: Int?
    ): SpecialResponse<List<LessonsProcess>>


    @POST("/lesson/create")
    suspend fun createLesson(@Body request: Lesson): CommonResponse

    @GET("/lesson/{lessonId}")
    suspend fun getLessonDetail(
        @Path("lessonId") lessonId: Long
    ): CommonResponse

    @PUT("/lesson/{lessonId}")
    suspend fun updateLesson(@Body request: LessonRequest, @Path("lessonId") lessonId: Int?): CommonResponse

//    @PUT("/lesson/{lessonId}/complete")
//    suspend fun completeLesson(
//        @Path("lessonId") lessonId: Int
//    ): CommonResponse

    @POST("/lessonProgress/{lessonId}/{courseId}/completeSelf")
    suspend fun completeLessonForSelf(
        @Path("lessonId") lessonId: Int,@Path("courseId") courseId: Int
    ): CommonResponse

    @Multipart
    @POST("/oss/uploadImage")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): CommonResponse

    @Multipart
    @POST("/oss/uploadPdf")
    suspend fun uploadPdf(
        @Part file: MultipartBody.Part
    ): CommonResponse

    //Related to the meeting
    @GET("/tutor/meeting/availability")
    suspend fun getSlotsAvailabilityTutor(): SpecialResponse<List<AvailabilitySlotDetail>>

    @POST("/tutor/meeting/availability")
    suspend fun updateSlotsAvailability(@Body request: AvailabilityRequest): CommonResponse

    @GET("/tutor/meeting/requests/pending")
    suspend fun getAllMeetingsTutor(): SpecialResponse<List<Meeting>>

    @PUT("/tutor/meeting/requests/{id}/approve")
    suspend fun approveMeeting(@Path("id") id: Int?): CommonResponse

    @PUT("/tutor/meeting/requests/{id}/reject")
    suspend fun rejectMeeting(@Path("id") id: Int?): CommonResponse

    @GET("/student/meeting/tutor/{tutorId}/free-slots")
    suspend fun getSlotsAvailabilityStudent(@Path("tutorId") tutorId: Int?): SpecialResponse<List<AvailabilitySlotDetail>>

    @GET("/student/meeting/bookings")
    suspend fun getAllMeetingsStudent(): SpecialResponse<List<Meeting>>

    @POST("/student/meeting/book")
    suspend fun bookAMeeting(@Body request: BookMeetingRequest): CommonResponse

    @GET("/student/meeting/tutors")
    suspend fun getAllBookableTutors(): SpecialResponse<List<TutorInfo>>

    //data science
    @GET("/tutor/dashboard")
    suspend fun getTutorDashboardInfo(
        @Query("courseId") courseId: Int? = null
    ): SpecialResponse<CourseProgress>


    @POST("/user/forgot-password")
    suspend fun forgotPassword(@Body request: Map<String, String>): CommonResponse

    @POST("/user/reset-password")
    suspend fun resetPassword(@Body request: Map<String, String>): CommonResponse
}

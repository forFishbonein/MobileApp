package com.tutoring.controller;

import com.tutoring.dto.CourseRegisterRequest;
import com.tutoring.dto.CreateCourseRequest;
import com.tutoring.dto.UpdateCourseRequest;
import com.tutoring.entity.Course;
import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.result.RestResult;
import com.tutoring.service.CourseRegistrationService;
import com.tutoring.service.CourseService;
import com.tutoring.util.SecurityUtils;
import com.tutoring.vo.CourseDetailResponse;
import com.tutoring.vo.CourseListResponse;
import com.tutoring.vo.CourseProgressResponse;
import com.tutoring.vo.TutorCourseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/course")
@Slf4j
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRegistrationService courseRegistrationService;

    /**
     * GET /course/list
     * 列表查询课程，可通过 name 和 subject 筛选，并返回包含教师名字的自定义响应对象
     */
    @GetMapping("/list")
    public RestResult<List<CourseListResponse>> listCourses(@RequestParam(required = false) String name,
                                                            @RequestParam(required = false) String subject) {
        List<CourseListResponse> courses = courseService.findCourses(name, subject);
        return RestResult.success(courses, "Courses retrieved successfully.");
    }

    /**
     * POST /course/register
     * 学生请求加入课程
     */
    @PostMapping("/register")
    public RestResult<?> registerCourse(@Valid @RequestBody CourseRegisterRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        if (SecurityUtils.getCurrentUserRole() != User.Role.student) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only students can register for courses.");
        }
        courseRegistrationService.registerCourse(currentUserId, request.getCourseId());
        return RestResult.success(null, "Course registration request submitted.");
    }

    /**
     * GET /course/{courseId}/progress
     * 学生查看指定 Course 下的学习进度
     */
    @GetMapping("/{courseId}/progress")
    public RestResult<CourseProgressResponse> getCourseProgress(@PathVariable Long courseId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        if (SecurityUtils.getCurrentUserRole() != User.Role.student) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only students can view course progress.");
        }
        CourseProgressResponse progress = courseRegistrationService.getCourseProgress(currentUserId, courseId);
        return RestResult.success(progress, "Course progress retrieved successfully.");
    }

    /**
     * POST /course/create
     * 导师创建课程
     */
    @PostMapping("/create")
    public RestResult<CourseDetailResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        if (SecurityUtils.getCurrentUserRole() != User.Role.tutor) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only tutors can create courses.");
        }
        Course newCourse = courseService.createCourse(currentUserId, request);
        CourseDetailResponse response = convertToDetailResponse(newCourse);
        return RestResult.success(response, "Course created successfully.");
    }

    /**
     * GET /course/{courseId}
     * 获取课程详情
     */
    @GetMapping("/{courseId}")
    public RestResult<CourseDetailResponse> getCourseDetail(@PathVariable Long courseId) {
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "Course not found.");
        }
        CourseDetailResponse response = convertToDetailResponse(course);
        return RestResult.success(response, "Course details retrieved successfully.");
    }

    /**
     * PUT /course/{courseId}
     * 导师更新课程信息
     */
    @PutMapping("/{courseId}")
    public RestResult<CourseDetailResponse> updateCourse(@PathVariable Long courseId,
                                                         @Valid @RequestBody UpdateCourseRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "Course not found.");
        }
        if (!course.getTutorId().equals(currentUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "You are not authorized to update this course.");
        }
        // 更新课程信息
        course.setCourseName(request.getName());
        course.setDescription(request.getDescription());
        course.setSubject(request.getSubject());
        courseService.updateById(course);
        CourseDetailResponse response = convertToDetailResponse(course);
        return RestResult.success(response, "Course updated successfully.");
    }

    /**
     * DELETE /course/{courseId}
     * 导师删除课程
     */
    @DeleteMapping("/{courseId}")
    public RestResult<?> deleteCourse(@PathVariable Long courseId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "Course not found.");
        }
        if (!course.getTutorId().equals(currentUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "You are not authorized to delete this course.");
        }
        courseService.removeById(courseId);
        return RestResult.success(null, "Course deleted successfully.");
    }

    /**
     * GET /course/tutor/list
     * 导师查询自己所教的所有课程，返回优化后的格式（不包含教师名字）
     */
    @GetMapping("/tutor/list")
    public RestResult<List<TutorCourseResponse>> listTutorCourses() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "User is not authenticated.");
        }
        if (SecurityUtils.getCurrentUserRole() != User.Role.tutor) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Only tutors can view their courses.");
        }
        List<TutorCourseResponse> courses = courseService.findCoursesByTutor(currentUserId);
        return RestResult.success(courses, "Tutor courses retrieved successfully.");
    }

    /**
     * Helper method: 将 Course 实体转换为 CourseDetailResponse
     */
    private CourseDetailResponse convertToDetailResponse(Course course) {
        CourseDetailResponse response = new CourseDetailResponse();
        response.setCourseId(course.getCourseId());
        response.setTutorId(course.getTutorId());
        response.setCourseName(course.getCourseName());
        response.setDescription(course.getDescription());
        response.setSubject(course.getSubject());
        response.setCreatedAt(course.getCreatedAt());
        response.setUpdatedAt(course.getUpdatedAt());
        return response;
    }
}
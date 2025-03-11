package com.tutoring.controller;

import com.tutoring.dto.CourseRegisterRequest;
import com.tutoring.dto.CreateCourseRequest;
import com.tutoring.entity.Course;
import com.tutoring.entity.User;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.result.RestResult;
import com.tutoring.service.CourseRegistrationService;
import com.tutoring.service.CourseService;
import com.tutoring.util.SecurityUtils;
import com.tutoring.vo.CourseProgressResponse;
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
     * 列表查询 Course，可通过 name 和 subject 筛选
     */
    @GetMapping("/list")
    public RestResult<List<Course>> listCourses(@RequestParam(required = false) String name,
                                                @RequestParam(required = false) String subject) {
        List<Course> courses = courseService.findCourses(name, subject);
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
}
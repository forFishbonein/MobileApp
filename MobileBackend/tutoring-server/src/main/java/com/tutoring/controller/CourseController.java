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

    @GetMapping("/list")
    public RestResult<List<CourseListResponse>> listCourses(@RequestParam(required = false) String name,
                                                            @RequestParam(required = false) String subject) {
        List<CourseListResponse> courses = courseService.findCourses(name, subject);
        return RestResult.success(courses, "Courses retrieved successfully.");
    }

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

    @GetMapping("/{courseId}")
    public RestResult<CourseDetailResponse> getCourseDetail(@PathVariable Long courseId) {
        Course course = courseService.getById(courseId);
        if (course == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "Course not found.");
        }
        CourseDetailResponse response = convertToDetailResponse(course);
        return RestResult.success(response, "Course details retrieved successfully.");
    }

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
        course.setCourseName(request.getName());
        course.setDescription(request.getDescription());
        course.setSubject(request.getSubject());
        courseService.updateById(course);
        CourseDetailResponse response = convertToDetailResponse(course);
        return RestResult.success(response, "Course updated successfully.");
    }

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
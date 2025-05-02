package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.dto.CreateCourseRequest;
import com.tutoring.entity.Course;
import com.tutoring.vo.CourseListResponse;
import com.tutoring.vo.TutorCourseResponse;

import java.util.List;

public interface CourseService extends IService<Course> {

    Course createCourse(Long tutorId, CreateCourseRequest request);

    List<CourseListResponse> findCourses(String name, String subject);

    List<TutorCourseResponse> findCoursesByTutor(Long tutorId);
}


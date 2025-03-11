package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.dto.CreateCourseRequest;
import com.tutoring.entity.Course;
import com.tutoring.vo.CourseListResponse;
import com.tutoring.vo.TutorCourseResponse;

import java.util.List;

public interface CourseService extends IService<Course> {

    /**
     * 导师创建课程
     * @param tutorId 当前登录导师的ID
     * @param request 创建课程的请求体
     * @return 创建后的 Course 实体
     */
    Course createCourse(Long tutorId, CreateCourseRequest request);

    /**
     * 列表查询课程，并返回包含教师名字的自定义响应对象
     */
    List<CourseListResponse> findCourses(String name, String subject);

    /**
     * 查询指定 tutor 所教的所有课程，返回优化后的 TutorCourseResponse（不包含教师名字）
     */
    List<TutorCourseResponse> findCoursesByTutor(Long tutorId);
}


package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutoring.dao.CourseDao;
import com.tutoring.dao.UserDao;
import com.tutoring.dto.CreateCourseRequest;
import com.tutoring.entity.Course;
import com.tutoring.entity.User;
import com.tutoring.service.CourseService;
import com.tutoring.vo.CourseListResponse;
import com.tutoring.vo.TutorCourseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CourseServiceImpl extends ServiceImpl<CourseDao, Course> implements CourseService {

    @Autowired
    private UserDao userDao; // 用于查询教师信息

    @Override
    public Course createCourse(Long tutorId, CreateCourseRequest request) {
        // 1. 构造新的 Course 实体
        Course course = new Course();
        course.setTutorId(tutorId);
        course.setCourseName(request.getName());
        course.setDescription(request.getDescription());
        course.setSubject(request.getSubject());

        // 2. 保存到数据库
        this.save(course);

        log.info("New course created: {}", course);
        return course;
    }

    @Override
    public List<CourseListResponse> findCourses(String name, String subject) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like("course_name", name);
        }
        if (subject != null && !subject.trim().isEmpty()) {
            queryWrapper.like("subject", subject);
        }
        // 按创建时间降序排序：创建时间越近的排在前面
        queryWrapper.orderByDesc("created_at");
        List<Course> courses = this.list(queryWrapper);
        List<CourseListResponse> responses = new ArrayList<>();
        for (Course course : courses) {
            // 通过 tutor_id 查询教师信息
            User teacher = userDao.selectById(course.getTutorId());
            String teacherName = teacher != null ? teacher.getNickname() : "Unknown";
            CourseListResponse dto = CourseListResponse.builder()
                    .courseId(course.getCourseId())
                    .courseName(course.getCourseName())
                    .description(course.getDescription())
                    .subject(course.getSubject())
                    .teacherName(teacherName)
                    .createdAt(course.getCreatedAt())
                    .updatedAt(course.getUpdatedAt())
                    .build();
            responses.add(dto);
        }
        return responses;
    }

    @Override
    public List<TutorCourseResponse> findCoursesByTutor(Long tutorId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tutor_id", tutorId)
                .orderByDesc("created_at"); // 按创建时间降序排序
        List<Course> courses = this.list(queryWrapper);
        List<TutorCourseResponse> responses = new ArrayList<>();
        for (Course course : courses) {
            TutorCourseResponse dto = TutorCourseResponse.builder()
                    .courseId(course.getCourseId())
                    .courseName(course.getCourseName())
                    .description(course.getDescription())
                    .subject(course.getSubject())
                    .createdAt(course.getCreatedAt())
                    .updatedAt(course.getUpdatedAt())
                    .build();
            responses.add(dto);
        }
        return responses;
    }
}

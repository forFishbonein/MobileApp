package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutoring.dao.CourseDao;
import com.tutoring.dto.CreateCourseRequest;
import com.tutoring.entity.Course;
import com.tutoring.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CourseServiceImpl extends ServiceImpl<CourseDao, Course> implements CourseService {

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
    public List<Course> findCourses(String name, String subject) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like("course_name", name);
        }
        if (subject != null && !subject.trim().isEmpty()) {
            queryWrapper.like("subject", subject);
        }
        List<Course> courses = this.list(queryWrapper);
        return courses;
    }
}

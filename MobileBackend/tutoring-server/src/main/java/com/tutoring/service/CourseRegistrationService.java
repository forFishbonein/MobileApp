package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.entity.Course;
import com.tutoring.entity.CourseRegistration;
import com.tutoring.vo.CourseProgressResponse;
import com.tutoring.vo.RegistrationResponseDTO;

import java.util.List;

public interface CourseRegistrationService extends IService<CourseRegistration> {
    void registerCourse(Long studentId, Long courseId);

    List<RegistrationResponseDTO> findRegistrationsByTutorWithUserInfo(Long tutorId);


    void updateRegistrationStatus(Long registrationId, String decision, Long tutorId);

    CourseProgressResponse getCourseProgress(Long studentId, Long courseId);

    List<CourseRegistration> findRegistrationsByStudent(Long studentId);
}

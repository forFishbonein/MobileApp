package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.entity.Course;
import com.tutoring.entity.CourseRegistration;
import com.tutoring.vo.CourseProgressResponse;
import com.tutoring.vo.RegistrationResponseDTO;

import java.util.List;

public interface CourseRegistrationService extends IService<CourseRegistration> {
    /**
     * 学生请求加入课程（状态默认为 pending）
     */
    void registerCourse(Long studentId, Long courseId);

    /**
     * 获取当前导师所教授课程的所有注册请求
     */
    // 新方法：返回带昵称的详细信息
    List<RegistrationResponseDTO> findRegistrationsByTutorWithUserInfo(Long tutorId);


    /**
     * 导师审批某个注册请求
     * @param registrationId 待审批记录ID
     * @param decision 审批结果，取值 "approved" 或 "rejected"
     * @param tutorId 当前审批的导师ID（用于验证归属）
     */
    void updateRegistrationStatus(Long registrationId, String decision, Long tutorId);

    /**
     * 获取当前学生在指定课程中各 Lesson 的完成进度
     */
    CourseProgressResponse getCourseProgress(Long studentId, Long courseId);

    /**
     * 获取当前学生所有的注册订单
     */
    List<CourseRegistration> findRegistrationsByStudent(Long studentId);
}

package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutoring.dao.CourseDao;
import com.tutoring.dao.CourseRegistrationDao;
import com.tutoring.dao.UserDao;
import com.tutoring.entity.Course;
import com.tutoring.entity.CourseRegistration;
import com.tutoring.entity.User;
import com.tutoring.service.StudentMeetingService;
import com.tutoring.vo.TutorBasicInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudentMeetingServiceImpl implements StudentMeetingService {

    @Autowired
    private CourseRegistrationDao regDao;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private UserDao userDao;

    @Override
    public List<TutorBasicInfoVO> listBookableTutors(Long studentId) {
        // 1. 查询所有该学生已批准的课程注册
        List<CourseRegistration> regs = regDao.selectList(
                new LambdaQueryWrapper<CourseRegistration>()
                        .eq(CourseRegistration::getStudentId, studentId)
                        .eq(CourseRegistration::getStatus, CourseRegistration.RegistrationStatus.approved)
        );

        if (regs.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 根据 course_id 拉出对应的 tutor_id
        Set<Long> tutorIds = regs.stream()
                .map(CourseRegistration::getCourseId)
                .map(courseId -> {
                    Course c = courseDao.selectById(courseId);
                    if (c == null) {
                        log.warn("Course [{}] not found for student {}", courseId, studentId);
                        return null;
                    }
                    return c.getTutorId();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (tutorIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 批量查询老师的用户信息
        List<User> tutors = userDao.selectBatchIds(new ArrayList<>(tutorIds));
        if (tutors.isEmpty()) {
            return Collections.emptyList();
        }

        // 4. 过滤 role 为 tutor，构造 VO 返回
        return tutors.stream()
                .filter(u -> u.getRole() == User.Role.tutor)
                .map(u -> TutorBasicInfoVO.builder()
                        .tutorId(u.getUserId())
                        .tutorNickname(u.getNickname())
                        .tutorEmail(u.getEmail())
                        .build()
                )
                .collect(Collectors.toList());
    }
}

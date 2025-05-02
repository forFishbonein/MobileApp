package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutoring.dao.CourseDao;
import com.tutoring.dao.CourseRegistrationDao;
import com.tutoring.dao.LessonProgressDao;
import com.tutoring.dao.UserDao;
import com.tutoring.entity.*;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.service.CourseRegistrationService;
import com.tutoring.service.LessonService;
import com.tutoring.vo.CourseProgressResponse;
import com.tutoring.vo.LessonProgressItem;
import com.tutoring.vo.RegistrationResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseRegistrationServiceImpl extends ServiceImpl<CourseRegistrationDao, CourseRegistration>
        implements CourseRegistrationService {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private LessonProgressDao lessonProgressDao;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private UserDao userDao;

    @Override
    public void registerCourse(Long studentId, Long courseId) {
        CourseRegistration registration = CourseRegistration.builder()
                .courseId(courseId)
                .studentId(studentId)
                .status(CourseRegistration.RegistrationStatus.pending)
                .build();
        this.baseMapper.insert(registration);
        log.info("Student {} registered for course {}", studentId, courseId);
    }

    @Override
    public List<RegistrationResponseDTO> findRegistrationsByTutorWithUserInfo(Long tutorId) {
        List<Course> tutorCourses = getCoursesByTutor(tutorId);
        if (tutorCourses.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> courseIds = tutorCourses.stream()
                .map(Course::getCourseId)
                .collect(Collectors.toList());

        QueryWrapper<CourseRegistration> regQuery = new QueryWrapper<>();
        regQuery.in("course_id", courseIds)
                .orderByDesc("created_at");

        List<CourseRegistration> registrations = this.baseMapper.selectList(regQuery);
        if (registrations.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> studentIds = registrations.stream()
                .map(CourseRegistration::getStudentId)
                .collect(Collectors.toSet());

        if (studentIds.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.in("user_id", studentIds);
        List<User> userList = userDao.selectList(userQuery);

        Map<Long, String> userIdToNickname = userList.stream()
                .collect(Collectors.toMap(User::getUserId, user -> user.getNickname() != null ? user.getNickname() : ""));

        return registrations.stream().map(reg -> {
            return RegistrationResponseDTO.builder()
                    .registrationId(reg.getRegistrationId())
                    .courseId(reg.getCourseId())
                    .studentId(reg.getStudentId())
                    .studentNickname(userIdToNickname.getOrDefault(reg.getStudentId(), ""))
                    .status(reg.getStatus())
                    .createdAt(reg.getCreatedAt())
                    .updatedAt(reg.getUpdatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    private List<Course> getCoursesByTutor(Long tutorId) {
        QueryWrapper<Course> courseQuery = new QueryWrapper<>();
        courseQuery.eq("tutor_id", tutorId);
        return courseDao.selectList(courseQuery);
    }

    @Override
    public void updateRegistrationStatus(Long registrationId, String decision, Long tutorId) {
        CourseRegistration registration = this.baseMapper.selectById(registrationId);
        if (registration == null) {
            throw new CustomException(ErrorCode.NOT_FOUND, "Registration record not found.");
        }
        Course course = courseDao.selectById(registration.getCourseId());
        if (course == null || !course.getTutorId().equals(tutorId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "You are not authorized to update this registration.");
        }
        if ("approved".equalsIgnoreCase(decision)) {
            registration.setStatus(CourseRegistration.RegistrationStatus.approved);
        } else if ("rejected".equalsIgnoreCase(decision)) {
            registration.setStatus(CourseRegistration.RegistrationStatus.rejected);
        } else {
            throw new CustomException(ErrorCode.BAD_REQUEST, "Invalid decision value.");
        }
        this.baseMapper.updateById(registration);
        log.info("Registration {} updated to {} by tutor {}", registrationId, registration.getStatus(), tutorId);
    }

    @Override
    public CourseProgressResponse getCourseProgress(Long studentId, Long courseId) {
        QueryWrapper<Lesson> lessonQuery = new QueryWrapper<>();
        lessonQuery.eq("course_id", courseId);
        List<Lesson> lessons = lessonService.list(lessonQuery);

        List<LessonProgressItem> progressItems = new ArrayList<>();
        for (Lesson lesson : lessons) {
            QueryWrapper<LessonProgress> progressQuery = new QueryWrapper<>();
            progressQuery.eq("lesson_id", lesson.getLessonId())
                    .eq("student_id", studentId);
            LessonProgress progress = lessonProgressDao.selectOne(progressQuery);

            LessonProgressItem item = new LessonProgressItem();
            item.setLessonId(lesson.getLessonId());
            item.setLessonTitle(lesson.getTitle());
            if (progress == null) {
                item.setProgress(LessonProgress.ProgressStatus.not_started.name());
            } else {
                item.setProgress(progress.getStatus().name());
            }
            progressItems.add(item);
        }
        CourseProgressResponse response = new CourseProgressResponse();
        response.setCourseId(courseId);
        response.setLessonProgressList(progressItems);
        return response;
    }

    @Override
    public List<CourseRegistration> findRegistrationsByStudent(Long studentId) {
        QueryWrapper<CourseRegistration> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId)
                .orderByDesc("created_at");
        return this.baseMapper.selectList(queryWrapper);
    }
}

package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutoring.dao.CourseDao;
import com.tutoring.dao.LessonProgressDao;
import com.tutoring.entity.Course;
import com.tutoring.entity.Lesson;
import com.tutoring.entity.LessonProgress;
import com.tutoring.enumeration.ErrorCode;
import com.tutoring.exception.CustomException;
import com.tutoring.service.CourseService;
import com.tutoring.service.LessonProgressService;
import com.tutoring.service.LessonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LessonProgressServiceImpl extends ServiceImpl<LessonProgressDao, LessonProgress>
        implements LessonProgressService {

    @Autowired
    private LessonService lessonService; // 用于根据 lessonId 获取 lesson 对象，从而获取 courseId

    @Override
    public void markLessonCompletedForStudent(Long studentId, Long lessonId, Long courseId) {

        // 若需要校验该 lesson 属于该 course（可选，可根据业务需求增加检查）
        // 例如：根据 lessonId 查到 Lesson，并判断其 courseId 是否与传入一致。
        // Lesson lesson = lessonService.getById(lessonId);
        // if (lesson == null || !lesson.getCourseId().equals(courseId)) {
        //     throw new CustomException(ErrorCode.BAD_REQUEST, "Course ID and Lesson ID do not match.");
        // }

        QueryWrapper<LessonProgress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId)
                .eq("lesson_id", lessonId);

        LessonProgress existingProgress = this.getOne(queryWrapper);

        if (existingProgress == null) {
            // 若无记录则插入
            LessonProgress newProgress = new LessonProgress();
            newProgress.setStudentId(studentId);
            newProgress.setLessonId(lessonId);
            newProgress.setCourseId(courseId);
            newProgress.setStatus(LessonProgress.ProgressStatus.completed);
            this.save(newProgress);
            log.info("Student {} completes lesson {} (new record inserted), courseId = {}.", studentId, lessonId, courseId);
        } else {
            // 若有记录则更新
            existingProgress.setStatus(LessonProgress.ProgressStatus.completed);
            // 同时更新 courseId，避免之前插入时未写入或写错
            existingProgress.setCourseId(courseId);
            this.updateById(existingProgress);
            log.info("Student {} completes lesson {} (existing record updated), courseId = {}.", studentId, lessonId, courseId);
        }
    }

    @Override
    public List<LessonProgress> getLessonProgressByCourseAndStudent(Long courseId, Long studentId) {
        QueryWrapper<LessonProgress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId)
                .eq("student_id", studentId);
        return this.list(queryWrapper);
    }
}


package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.entity.Lesson;
import com.tutoring.entity.LessonProgress;

import java.util.List;

public interface LessonProgressService extends IService<LessonProgress> {

    void markLessonCompletedForStudent(Long studentId, Long lessonId, Long courseId);

    List<LessonProgress> getLessonProgressByCourseAndStudent(Long courseId, Long studentId);
}

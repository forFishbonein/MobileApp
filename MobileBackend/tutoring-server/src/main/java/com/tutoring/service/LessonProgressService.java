package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.entity.Lesson;
import com.tutoring.entity.LessonProgress;

public interface LessonProgressService extends IService<LessonProgress> {
    /**
     * Teacher 标记指定 Lesson 下所有学生的进度为已完成
     *
     * @param teacherId 当前教师ID
     * @param lessonId  Lesson ID
     */
    void markLessonCompletedForAllStudents(Long teacherId, Long lessonId);
}

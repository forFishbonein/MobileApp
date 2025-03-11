package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.entity.Lesson;
import com.tutoring.entity.LessonProgress;

public interface LessonProgressService extends IService<LessonProgress> {
    /**
     * 学生标记指定 Lesson 为已完成
     *
     * @param studentId 当前学生ID
     * @param lessonId  Lesson ID
     */
    void markLessonCompleted(Long studentId, Long lessonId);
}

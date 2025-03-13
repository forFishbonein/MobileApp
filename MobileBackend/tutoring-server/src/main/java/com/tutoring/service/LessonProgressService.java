package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.entity.Lesson;
import com.tutoring.entity.LessonProgress;

import java.util.List;

public interface LessonProgressService extends IService<LessonProgress> {

    /**
     * 学生标记自己某个 Lesson 的进度为已完成（若无记录则插入，若有则更新）
     *
     * @param studentId 学生ID
     * @param lessonId  Lesson ID
     * @param courseId  课程ID（前端直接传递）
     */
    void markLessonCompletedForStudent(Long studentId, Long lessonId, Long courseId);

    /**
     * 根据 courseId + studentId 查询该学生在此课程下所有 Lesson 的进度
     *
     * @param courseId  课程ID
     * @param studentId 学生ID
     * @return 进度列表
     */
    List<LessonProgress> getLessonProgressByCourseAndStudent(Long courseId, Long studentId);
}

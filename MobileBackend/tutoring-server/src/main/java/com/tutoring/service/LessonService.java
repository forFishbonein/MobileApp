package com.tutoring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tutoring.dto.CreateLessonRequest;
import com.tutoring.entity.Lesson;

public interface LessonService extends IService<Lesson> {

    Lesson createLesson(Long tutorId, CreateLessonRequest request);
}
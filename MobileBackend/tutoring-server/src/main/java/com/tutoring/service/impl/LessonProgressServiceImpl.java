package com.tutoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutoring.dao.CourseDao;
import com.tutoring.dao.LessonProgressDao;
import com.tutoring.entity.Course;
import com.tutoring.entity.LessonProgress;
import com.tutoring.service.CourseService;
import com.tutoring.service.LessonProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LessonProgressServiceImpl extends ServiceImpl<LessonProgressDao, LessonProgress>
        implements LessonProgressService {

    @Override
    public void markLessonCompleted(Long studentId, Long lessonId) {
        // 构造查询条件：查找指定学生和 lesson 的记录
        QueryWrapper<LessonProgress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId)
                .eq("lesson_id", lessonId);

        // 查询当前记录
        LessonProgress progress = this.baseMapper.selectOne(queryWrapper);

        if (progress == null) {
            // 若没有记录，则创建一条新记录，状态设为 completed
            progress = LessonProgress.builder()
                    .studentId(studentId)
                    .lessonId(lessonId)
                    .status(LessonProgress.ProgressStatus.completed)
                    .build();
            this.baseMapper.insert(progress);
        } else {
            // 如果记录已存在，则更新状态为 completed
            progress.setStatus(LessonProgress.ProgressStatus.completed);
            this.baseMapper.updateById(progress);
        }
        log.info("Lesson marked as completed: studentId={}, lessonId={}", studentId, lessonId);
    }
}

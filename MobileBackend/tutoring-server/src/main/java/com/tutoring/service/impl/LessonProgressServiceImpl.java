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
    public void markLessonCompletedForAllStudents(Long teacherId, Long lessonId) {
        // 构造查询条件：查找指定 lesson 的所有记录
        QueryWrapper<LessonProgress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lesson_id", lessonId);

        // 构造更新对象，将状态设置为 completed
        LessonProgress updateProgress = new LessonProgress();
        updateProgress.setStatus(LessonProgress.ProgressStatus.completed);

        // 执行批量更新
        int updatedCount = this.baseMapper.update(updateProgress, queryWrapper);

        log.info("Teacher {} marked lesson {} as completed for {} students", teacherId, lessonId, updatedCount);
    }
}

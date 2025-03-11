package com.tutoring.vo;

import lombok.Data;
import java.util.List;

@Data
public class TutorDashboardResponse {
    /**
     * 当前导师所教授的所有课程仪表盘数据列表
     */
    private List<CourseDashboardItem> courses;

    @Data
    public static class CourseDashboardItem {
        private Long courseId;
        private String courseName;
        /**
         * 当前课程审批通过的注册学生数
         */
        private Integer registrationCount;
        /**
         * 每个注册学生在该课程中的学习进度数据
         */
        private List<StudentProgress> studentProgressList;
    }

    @Data
    public static class StudentProgress {
        private Long studentId;
        private String studentNickname;
        /**
         * 每个Lesson的进度状态，若没有记录则默认为"not_started"
         */
        private List<LessonProgressItem> lessonProgressItems;
    }
}

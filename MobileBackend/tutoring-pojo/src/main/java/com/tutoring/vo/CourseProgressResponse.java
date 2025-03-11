package com.tutoring.vo;

import lombok.Data;
import java.util.List;

@Data
public class CourseProgressResponse {
    private Long courseId;
    private List<LessonProgressItem> lessonProgressList;

    @Data
    public static class LessonProgressItem {
        private Long lessonId;
        private String title;
        private String status; // 如 "not_started", "in_progress", "completed"
    }
}

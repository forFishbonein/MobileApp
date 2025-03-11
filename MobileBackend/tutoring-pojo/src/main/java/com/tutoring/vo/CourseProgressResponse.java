package com.tutoring.vo;

import lombok.Data;
import java.util.List;

@Data
public class CourseProgressResponse {
    private Long courseId;
    private List<TutorDashboardResponse.LessonProgressItem> lessonProgressList;
}

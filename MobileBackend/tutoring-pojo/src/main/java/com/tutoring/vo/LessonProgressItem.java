package com.tutoring.vo;

import lombok.Data;

@Data
public class LessonProgressItem {
    private Long lessonId;
    private String lessonTitle;
    private String progress;
}

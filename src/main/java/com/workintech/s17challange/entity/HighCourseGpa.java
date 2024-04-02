package com.workintech.s17challange.entity;

import org.springframework.stereotype.Component;

@Component
public class HighCourseGpa implements CourseGpa{
    @Override
    public int getGpa() {
        return 10;
    }
}

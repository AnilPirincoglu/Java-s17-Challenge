package com.workintech.s17challange.validation;

import com.workintech.s17challange.entity.Course;
import com.workintech.s17challange.exceptions.ApiException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

public class CourseValidation {
    public static void checkName(String name) {
        if (name == null || name.isEmpty()) {
            throw new ApiException("name cannot be null or empty!", HttpStatus.BAD_REQUEST);
        }
    }

    public static void checkCredit(Integer credit) {
        if (credit == null || credit < 0 || credit > 4)
            throw new ApiException("credit is null or not between 0-4", HttpStatus.BAD_REQUEST);
    }

    public static void checkNameExist(List<Course> courses, Course course) {
        Optional<Course> optionalCourse = courses.stream()
                .filter(c -> c.getName().equalsIgnoreCase(course.getName()))
                .findFirst();
        if (optionalCourse.isPresent())
            throw new ApiException("course already exist with name: " + course.getName(), HttpStatus.BAD_REQUEST);
    }

    public static void checkId(Integer id) {
        if (id == null | id < 0)
            throw new ApiException("id cannot be null or less than zero! ", HttpStatus.BAD_REQUEST);
    }
}

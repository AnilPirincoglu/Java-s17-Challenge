package com.workintech.s17challange.controller;

import com.workintech.s17challange.entity.ApiResponse;
import com.workintech.s17challange.entity.Course;
import com.workintech.s17challange.entity.CourseGpa;
import com.workintech.s17challange.entity.LowCourseGpa;
import com.workintech.s17challange.exceptions.ApiException;
import com.workintech.s17challange.validation.CourseValidation;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private List<Course> courses;

    private final CourseGpa higCourseGpa;
    private final CourseGpa mediumCourseGpa;
    private final CourseGpa lowCourseGpa;

    public CourseController(@Qualifier("highCourseGpa") CourseGpa higCourseGpa,
                            @Qualifier("mediumCourseGpa") CourseGpa mediumCourseGpa,
                            @Qualifier("lowCourseGpa") CourseGpa lowCourseGpa) {
        this.higCourseGpa = higCourseGpa;
        this.mediumCourseGpa = mediumCourseGpa;
        this.lowCourseGpa = lowCourseGpa;
    }

    @PostConstruct
    public void init() {
        courses = new ArrayList<>();
    }

    @GetMapping
    public List<Course> getAll() {
        return this.courses;
    }

    @GetMapping("/{name}")
    public Course getByName(@PathVariable("name") String name) {
        CourseValidation.checkName(name);
        return courses.stream().filter(course -> course.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new ApiException("course not found with name: " + name, HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Course course) {
        CourseValidation.checkName((course.getName()));
        CourseValidation.checkCredit(course.getCredit());
        CourseValidation.checkNameExist(courses, course);
        courses.add(course);
        Integer totalGpa = getTotalGpa(course);
        ApiResponse apiResponse = new ApiResponse(course, totalGpa);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody Course course) {
        CourseValidation.checkId(id);
        CourseValidation.checkCredit(course.getCredit());
        CourseValidation.checkName(course.getName());

        Course existingCourse = courses.stream()
                .filter(c -> c.getName().equalsIgnoreCase(course.getName()))
                .findFirst()
                .orElseThrow(() -> new ApiException("record not found with id: " + id, HttpStatus.NOT_FOUND));
        int indexOfExistingCourse = courses.indexOf(existingCourse);
        course.setId(id);
        courses.set(indexOfExistingCourse, course);
        Integer totalGpa = getTotalGpa(course);
        ApiResponse apiResponse = new ApiResponse(courses.get(indexOfExistingCourse), totalGpa);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void delete (@PathVariable Integer id){
        CourseValidation.checkId(id);
        Course course = courses.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ApiException("record not found with id: " + id, HttpStatus.NOT_FOUND));
        courses.remove(course);
    }

    private Integer getTotalGpa(Course course) {
        if (course.getCredit() <= 2)
            return course.getGrade().getCoefficient() * course.getCredit() * lowCourseGpa.getGpa();
        else if (course.getCredit() == 3)
            return course.getGrade().getCoefficient() * course.getCredit() * mediumCourseGpa.getGpa();
        else
            return course.getGrade().getCoefficient() * course.getCredit() * higCourseGpa.getGpa();
    }
}

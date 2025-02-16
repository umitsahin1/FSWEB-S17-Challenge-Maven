package com.workintech.spring17challenge.util;

import com.workintech.spring17challenge.entity.Course;
import com.workintech.spring17challenge.exceptions.*;
import org.springframework.http.HttpStatus;

import java.util.List;

public class CourseValidation {
    public static void validateCourse(Course course, List<Course> existingCourses) {
        if (course == null) {
            throw new ApiException("Course cannot be null.", HttpStatus.BAD_REQUEST);
        }
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new ApiException("Course name cannot be empty.", HttpStatus.BAD_REQUEST);
        }
        if (course.getCredit() < 1 || course.getCredit() > 4) {
            throw new ApiException("Course credit must be between 1 and 4.", HttpStatus.BAD_REQUEST);
        }
        if (course.getGrade() == null) {
            throw new ApiException("Grade cannot be null.", HttpStatus.BAD_REQUEST);
        }
        if (course.getGrade().getCoefficient() < 0 || course.getGrade().getCoefficient() > 4) {
            throw new ApiException("Grade coefficient must be between 0 and 4.", HttpStatus.BAD_REQUEST);
        }
        checkDuplicateName(course.getName(), existingCourses);
    }

    public static void checkDuplicateName(String name, List<Course> existingCourses) {
        boolean isDuplicate = existingCourses.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));
        if (isDuplicate) {
            throw new DuplicateCourseException("A course with the same name already exists: " + name);
        }
    }

    public static void checkCourseExists(Integer id, List<Course> courses) {
        if (courses.stream().noneMatch(c -> c.getId().equals(id))) {
            throw new CourseNotFoundException("Course not found with id: " + id);
        }
    }
}

package com.workintech.spring17challenge.controller;

import com.workintech.spring17challenge.entity.*;
import com.workintech.spring17challenge.exceptions.*;
import com.workintech.spring17challenge.util.CourseValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


@RequiredArgsConstructor
@RestController
@RequestMapping("/courses")
public class CourseController {

    private final List<Course> courses = new CopyOnWriteArrayList<>();
    private final LowCourseGpa lowCourseGpa;
    private final MediumCourseGpa mediumCourseGpa;
    private final HighCourseGpa highCourseGpa;



    @GetMapping
    public List<Course> getAllCourses() {
        return courses;
    }

    @GetMapping("/{name}")
    public Course getCourseByName(@PathVariable String name) {
        return courses.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new CourseNotFoundException("Course not found: " + name));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, Object>> addCourse(@RequestBody Course course) {
        if (course.getName() == null || course.getCredit() == null || course.getGrade() == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", "Geçersiz ders verisi");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        courses.add(course);
        Map<String, Object> response = new HashMap<>();
        response.put("course", course);
        response.put("totalGpa", calculateTotalGpa(course));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{id}")
    public Map<String, Object> updateCourse(@PathVariable Integer id, @RequestBody Course newCourse) {
        CourseValidation.checkCourseExists(Math.toIntExact(id), courses);
        Course existingCourse = courses.stream().filter(c -> c.getId().equals(id)).findFirst().get();

        if (!existingCourse.getName().equalsIgnoreCase(newCourse.getName())) {
            CourseValidation.checkDuplicateName(newCourse.getName(), courses);
        }

        existingCourse.setName(newCourse.getName());
        existingCourse.setCredit(newCourse.getCredit());
        existingCourse.setGrade(newCourse.getGrade());

        return Map.of("course", existingCourse, "totalGpa", calculateTotalGpa(existingCourse));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCourse(@PathVariable Integer id) {
        CourseValidation.checkCourseExists(id, courses);
        courses.removeIf(c -> c.getId().equals(id));
    }

    private double calculateTotalGpa(Course course) {
        int credit = course.getCredit();
        int coefficient = course.getGrade().getCoefficient();
        return switch (credit) {
            case 1, 2 -> coefficient * credit * lowCourseGpa.getGpa();
            case 3 -> coefficient * credit * mediumCourseGpa.getGpa();
            case 4 -> coefficient * credit * highCourseGpa.getGpa();
            default -> throw new ApiException("Invalid credit value", HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}


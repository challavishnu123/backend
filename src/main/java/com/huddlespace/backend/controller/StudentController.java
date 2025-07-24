package com.huddlespace.backend.controller;

import com.huddlespace.backend.entity.Student;
import com.huddlespace.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Student student) {
        Student existing = service.getStudentByRollNumber(student.getRollNumber());
        if (existing != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .body(Map.of("message", "Student already exists"));
        }
        Student saved = service.register(student);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Student student) {
        Student existing = service.getStudentByRollNumber(student.getRollNumber());

        if (existing != null) {
            if (existing.getPassword().equals(student.getPassword())) {
                return ResponseEntity.ok(Map.of("message", "Login successful", "data", existing));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(Map.of("message", "Invalid password"));
            }
        } else {
            Student saved = service.register(student);
            return ResponseEntity.ok(Map.of("message", "User not found, registered and logged in", "data", saved));
        }
    }

    @PutMapping("/update-password")
    public Student updatePassword(@RequestParam String rollNumber, @RequestParam String newPassword) {
        return service.updatePassword(rollNumber, newPassword);
    }

    @DeleteMapping("/delete/{rollNumber}")
    public void delete(@PathVariable String rollNumber) {
        service.delete(rollNumber);
    }

    @GetMapping("/all")
    public List<Student> getAllStudents() {
        return service.getAllStudents();
    }
}

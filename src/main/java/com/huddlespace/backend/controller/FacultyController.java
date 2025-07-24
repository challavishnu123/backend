package com.huddlespace.backend.controller;

import com.huddlespace.backend.entity.Faculty;
import com.huddlespace.backend.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    @Autowired
    private FacultyService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Faculty faculty) {
        Faculty existing = service.getFacultyByFacultyId(faculty.getFacultyId());
        if (existing != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .body(Map.of("message", "Faculty already exists"));
        }
        Faculty saved = service.register(faculty);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Faculty faculty) {
        Faculty existing = service.getFacultyByFacultyId(faculty.getFacultyId());

        if (existing != null) {
            if (existing.getPassword().equals(faculty.getPassword())) {
                return ResponseEntity.ok(Map.of("message", "Login successful", "data", existing));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body(Map.of("message", "Invalid password"));
            }
        } else {
            Faculty saved = service.register(faculty);
            return ResponseEntity.ok(Map.of("message", "User not found, registered and logged in", "data", saved));
        }
    }

    @PutMapping("/update-password")
    public Faculty updatePassword(@RequestParam String facultyId, @RequestParam String newPassword) {
        return service.updatePassword(facultyId, newPassword);
    }

    @DeleteMapping("/delete/{facultyId}")
    public void delete(@PathVariable String facultyId) {
        service.delete(facultyId);
    }

    @GetMapping("/all")
    public List<Faculty> getAllFaculties() {
        return service.getAllFaculties();
    }
}

package com.huddlespace.backend.controller;

import com.huddlespace.backend.entity.Faculty;
import com.huddlespace.backend.entity.Student;
import com.huddlespace.backend.repository.FacultyRepository;
import com.huddlespace.backend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // Search for students whose roll number contains the query
        Stream<Student> studentStream = studentRepository.findAll().stream()
                .filter(student -> student.getRollNumber().toLowerCase().contains(query.toLowerCase()));

        // Search for faculty whose ID contains the query
        Stream<Faculty> facultyStream = facultyRepository.findAll().stream()
                .filter(faculty -> faculty.getFacultyId().toLowerCase().contains(query.toLowerCase()));

        // Combine the results and map them to a simple structure
        List<Object> results = Stream.concat(
                studentStream.map(s -> Map.of("username", s.getRollNumber(), "userType", "STUDENT")),
                facultyStream.map(f -> Map.of("username", f.getFacultyId(), "userType", "FACULTY"))
        )
        // Exclude the current user from the search results
        .filter(userMap -> !userMap.get("username").equals(currentUsername))
        .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }
}
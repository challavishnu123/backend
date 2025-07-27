package com.huddlespace.backend.controller;

import com.huddlespace.backend.entity.Student;
import com.huddlespace.backend.security.JwtUtil;
import com.huddlespace.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/student")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService service;

    @Autowired
    private JwtUtil jwtUtil;

    // Registration endpoint - No duplicates allowed
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Student student) {
        try {
            // Check if student already exists
            Student existing = service.getStudentByRollNumber(student.getRollNumber());
            if (existing != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                            "message", "Student already exists with roll number: " + student.getRollNumber(),
                            "error", "DUPLICATE_USER"
                        ));
            }
            
            // Register new student
            Student saved = service.register(student);
            
            // Generate JWT token for immediate login
            String token = jwtUtil.generateToken(saved.getRollNumber(), "STUDENT");
            
            return ResponseEntity.ok(Map.of(
                "message", "Registration successful. You are now logged in.",
                "token", token,
                "student", saved
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Registration failed: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    // Login endpoint - Only validate existing users
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Student student) {
        try {
            // Check if student exists
            Student existing = service.getStudentByRollNumber(student.getRollNumber());

            if (existing == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                            "message", "User not found. Please register first.",
                            "error", "USER_NOT_FOUND"
                        ));
            }
            
            // Validate password
            if (!existing.getPassword().equals(student.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                            "message", "Invalid password",
                            "error", "INVALID_CREDENTIALS"
                        ));
            }
            
            // Generate JWT token for successful login
            String token = jwtUtil.generateToken(existing.getRollNumber(), "STUDENT");
            
            return ResponseEntity.ok(Map.of(
                "message", "Login successful", 
                "token", token,
                "data", existing
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Login failed: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    // JWT Protected endpoints
    @PutMapping("/update-password")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        try {
            String rollNumber = request.get("rollNumber");
            String newPassword = request.get("newPassword");
            
            // Validate required fields
            if (rollNumber == null || rollNumber.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                            "message", "Roll number is required",
                            "error", "MISSING_ROLL_NUMBER"
                        ));
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                            "message", "New password is required",
                            "error", "MISSING_PASSWORD"
                        ));
            }
            
            // Verify the authenticated user is updating their own password
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUser = auth.getName();
            
            if (!authenticatedUser.equals(rollNumber)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                            "message", "You can only update your own password",
                            "error", "UNAUTHORIZED_ACCESS"
                        ));
            }
            
            // Check if student exists
            Student existing = service.getStudentByRollNumber(rollNumber);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "message", "Student not found",
                            "error", "USER_NOT_FOUND"
                        ));
            }
            
            // Update password
            Student updated = service.updatePassword(rollNumber, newPassword);
            
            return ResponseEntity.ok(Map.of(
                "message", "Password updated successfully",
                "student", updated
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Failed to update password: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> request) {
        try {
            String rollNumber = request.get("rollNumber");
            
            // Validate required field
            if (rollNumber == null || rollNumber.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                            "message", "Roll number is required",
                            "error", "MISSING_ROLL_NUMBER"
                        ));
            }
            
            // Verify the authenticated user is deleting their own account
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUser = auth.getName();
            
            if (!authenticatedUser.equals(rollNumber)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                            "message", "You can only delete your own account",
                            "error", "UNAUTHORIZED_ACCESS"
                        ));
            }
            
            // Check if student exists
            Student existing = service.getStudentByRollNumber(rollNumber);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "message", "Student not found",
                            "error", "USER_NOT_FOUND"
                        ));
            }
            
            // Delete account
            service.delete(rollNumber);
            
            return ResponseEntity.ok(Map.of(
                "message", "Account deleted successfully",
                "deletedUser", rollNumber
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Failed to delete account: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students = service.getAllStudents();
            return ResponseEntity.ok(Map.of(
                "message", "Students retrieved successfully",
                "count", students.size(),
                "students", students
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Failed to fetch students: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String rollNumber = auth.getName();
            
            Student student = service.getStudentByRollNumber(rollNumber);
            if (student != null) {
                return ResponseEntity.ok(Map.of(
                    "message", "Profile retrieved successfully",
                    "profile", student
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "message", "Student profile not found",
                            "error", "PROFILE_NOT_FOUND"
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Failed to fetch profile: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    // Additional endpoint to check if roll number exists
    @GetMapping("/check-exists/{rollNumber}")
    public ResponseEntity<?> checkStudentExists(@PathVariable String rollNumber) {
        try {
            Student student = service.getStudentByRollNumber(rollNumber);
            boolean exists = student != null;
            
            return ResponseEntity.ok(Map.of(
                "exists", exists,
                "rollNumber", rollNumber,
                "message", exists ? "Student exists" : "Student not found"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Failed to check student existence: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }
}
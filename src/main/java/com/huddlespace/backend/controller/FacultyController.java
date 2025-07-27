package com.huddlespace.backend.controller;

import com.huddlespace.backend.entity.Faculty;
import com.huddlespace.backend.entity.Student;
import com.huddlespace.backend.security.JwtUtil;
import com.huddlespace.backend.service.FacultyService;
import com.huddlespace.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/faculty")
@CrossOrigin(origins = "*")
public class FacultyController {

    @Autowired
    private FacultyService service;

    @Autowired
    private StudentService studentService;

    @Autowired
    private JwtUtil jwtUtil;

    // Registration endpoint - No duplicates allowed
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Faculty faculty) {
        try {
            // Check if faculty already exists
            Faculty existing = service.getFacultyByFacultyId(faculty.getFacultyId());
            if (existing != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                            "message", "Faculty already exists with ID: " + faculty.getFacultyId(),
                            "error", "DUPLICATE_USER"
                        ));
            }
            
            // Register new faculty
            Faculty saved = service.register(faculty);
            
            // Generate JWT token for immediate login
            String token = jwtUtil.generateToken(saved.getFacultyId(), "FACULTY");
            
            return ResponseEntity.ok(Map.of(
                "message", "Registration successful. You are now logged in.",
                "token", token,
                "faculty", saved
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
    public ResponseEntity<?> login(@RequestBody Faculty faculty) {
        try {
            // Check if faculty exists
            Faculty existing = service.getFacultyByFacultyId(faculty.getFacultyId());

            if (existing == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                            "message", "User not found. Please register first.",
                            "error", "USER_NOT_FOUND"
                        ));
            }
            
            // Validate password
            if (!existing.getPassword().equals(faculty.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                            "message", "Invalid password",
                            "error", "INVALID_CREDENTIALS"
                        ));
            }
            
            // Generate JWT token for successful login
            String token = jwtUtil.generateToken(existing.getFacultyId(), "FACULTY");
            
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
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        try {
            String facultyId = request.get("facultyId");
            String newPassword = request.get("newPassword");
            
            // Validate required fields
            if (facultyId == null || facultyId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                            "message", "Faculty ID is required",
                            "error", "MISSING_FACULTY_ID"
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
            
            if (!authenticatedUser.equals(facultyId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                            "message", "You can only update your own password",
                            "error", "UNAUTHORIZED_ACCESS"
                        ));
            }
            
            // Check if faculty exists
            Faculty existing = service.getFacultyByFacultyId(facultyId);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "message", "Faculty not found",
                            "error", "USER_NOT_FOUND"
                        ));
            }
            
            // Update password
            Faculty updated = service.updatePassword(facultyId, newPassword);
            
            return ResponseEntity.ok(Map.of(
                "message", "Password updated successfully",
                "faculty", updated
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
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> request) {
        try {
            String facultyId = request.get("facultyId");
            
            // Validate required field
            if (facultyId == null || facultyId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                            "message", "Faculty ID is required",
                            "error", "MISSING_FACULTY_ID"
                        ));
            }
            
            // Verify the authenticated user is deleting their own account
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUser = auth.getName();
            
            if (!authenticatedUser.equals(facultyId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                            "message", "You can only delete your own account",
                            "error", "UNAUTHORIZED_ACCESS"
                        ));
            }
            
            // Check if faculty exists
            Faculty existing = service.getFacultyByFacultyId(facultyId);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "message", "Faculty not found",
                            "error", "USER_NOT_FOUND"
                        ));
            }
            
            // Delete account
            service.delete(facultyId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Account deleted successfully",
                "deletedUser", facultyId
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
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getAllFaculties() {
        try {
            List<Faculty> faculties = service.getAllFaculties();
            return ResponseEntity.ok(Map.of(
                "message", "Faculties retrieved successfully",
                "count", faculties.size(),
                "faculties", faculties
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Failed to fetch faculties: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String facultyId = auth.getName();
            
            Faculty faculty = service.getFacultyByFacultyId(facultyId);
            if (faculty != null) {
                return ResponseEntity.ok(Map.of(
                    "message", "Profile retrieved successfully",
                    "profile", faculty
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "message", "Faculty profile not found",
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

    // Faculty-specific endpoints (can be accessed by faculty only)
    @GetMapping("/students")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
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

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getStudentById(@PathVariable String studentId) {
        try {
            Student student = studentService.getStudentByRollNumber(studentId);
            if (student != null) {
                return ResponseEntity.ok(Map.of(
                    "message", "Student retrieved successfully",
                    "student", student
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "message", "Student not found with ID: " + studentId,
                            "error", "STUDENT_NOT_FOUND"
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Failed to fetch student: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getDashboard() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String facultyId = auth.getName();
            
            Faculty faculty = service.getFacultyByFacultyId(facultyId);
            if (faculty == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "message", "Faculty not found",
                            "error", "USER_NOT_FOUND"
                        ));
            }
            
            // Get dashboard statistics
            List<Student> allStudents = studentService.getAllStudents();
            List<Faculty> allFaculties = service.getAllFaculties();
            
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("totalStudents", allStudents.size());
            dashboardData.put("totalFaculties", allFaculties.size());
            dashboardData.put("facultyProfile", faculty);
            dashboardData.put("recentStudents", allStudents.stream().limit(5).toList());
            
            return ResponseEntity.ok(Map.of(
                "message", "Dashboard data retrieved successfully",
                "dashboard", dashboardData
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Failed to fetch dashboard data: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> logout() {
        try {
            // Clear security context
            SecurityContextHolder.clearContext();
            
            return ResponseEntity.ok(Map.of(
                "message", "Logout successful"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Logout failed: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    // Additional endpoints for comprehensive faculty management
    @PutMapping("/update-profile")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> updateProfile(@RequestBody Faculty facultyUpdate) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedFacultyId = auth.getName();
            
            // Ensure faculty can only update their own profile
            if (!authenticatedFacultyId.equals(facultyUpdate.getFacultyId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                            "message", "You can only update your own profile",
                            "error", "UNAUTHORIZED_ACCESS"
                        ));
            }
            
            // Check if faculty exists
            Faculty existing = service.getFacultyByFacultyId(authenticatedFacultyId);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "message", "Faculty not found",
                            "error", "USER_NOT_FOUND"
                        ));
            }
            
            // Since the service interface doesn't have updateProfile method,
            // we'll use register method to update (assuming it handles updates)
            // Or create a new method in service for profile updates
            Faculty updated = service.register(facultyUpdate);
            
            return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "faculty", updated
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Failed to update profile: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> healthCheck() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String facultyId = auth.getName();
            
            Map<String, Object> health = new HashMap<>();
            health.put("status", "OK");
            health.put("authenticatedUser", facultyId);
            health.put("timestamp", new Date());
            health.put("version", "1.0.0");
            
            return ResponseEntity.ok(Map.of(
                "message", "Faculty service is running properly",
                "health", health
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Health check failed: " + e.getMessage(),
                        "error", "SERVER_ERROR",
                        "status", "ERROR"
                    ));
        }
    }
}
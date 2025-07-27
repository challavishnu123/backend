package com.huddlespace.backend.controller;

import com.huddlespace.backend.dto.AuthRequest;
import com.huddlespace.backend.dto.AuthResponse;
import com.huddlespace.backend.entity.Faculty;
import com.huddlespace.backend.entity.Student;
import com.huddlespace.backend.security.JwtUtil;
import com.huddlespace.backend.service.CustomUserDetailsService;
import com.huddlespace.backend.service.FacultyService;
import com.huddlespace.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StudentService studentService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/student/login")
    public ResponseEntity<?> loginStudent(@RequestBody AuthRequest authRequest) {
        try {
            // Check if student exists
            Student student = studentService.getStudentByRollNumber(authRequest.getUsername());
            
            if (student == null) {
                // User doesn't exist - return error, don't auto-register
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                            "message", "User not found. Please register first.",
                            "error", "USER_NOT_FOUND"
                        ));
            }
            
            // Validate existing student credentials
            if (!student.getPassword().equals(authRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                            "message", "Invalid password",
                            "error", "INVALID_CREDENTIALS"
                        ));
            }
            
            // Generate JWT token for successful login
            String token = jwtUtil.generateToken(student.getRollNumber(), "STUDENT");
            
            return ResponseEntity.ok(new AuthResponse(
                token,
                "Login successful",
                "STUDENT",
                student.getRollNumber(),
                student
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Login failed: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    @PostMapping("/faculty/login")
    public ResponseEntity<?> loginFaculty(@RequestBody AuthRequest authRequest) {
        try {
            // Check if faculty exists
            Faculty faculty = facultyService.getFacultyByFacultyId(authRequest.getUsername());
            
            if (faculty == null) {
                // User doesn't exist - return error, don't auto-register
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                            "message", "User not found. Please register first.",
                            "error", "USER_NOT_FOUND"
                        ));
            }
            
            // Validate existing faculty credentials
            if (!faculty.getPassword().equals(authRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                            "message", "Invalid password",
                            "error", "INVALID_CREDENTIALS"
                        ));
            }
            
            // Generate JWT token for successful login
            String token = jwtUtil.generateToken(faculty.getFacultyId(), "FACULTY");
            
            return ResponseEntity.ok(new AuthResponse(
                token,
                "Login successful",
                "FACULTY",
                faculty.getFacultyId(),
                faculty
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Login failed: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    @PostMapping("/student/register")
    public ResponseEntity<?> registerStudent(@RequestBody AuthRequest authRequest) {
        try {
            // Check if student already exists
            Student existingStudent = studentService.getStudentByRollNumber(authRequest.getUsername());
            if (existingStudent != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                            "message", "Student already exists with roll number: " + authRequest.getUsername(),
                            "error", "USER_EXISTS"
                        ));
            }
            
            // Create new student
            Student newStudent = new Student();
            newStudent.setRollNumber(authRequest.getUsername());
            newStudent.setPassword(authRequest.getPassword());
            
            Student savedStudent = studentService.register(newStudent);
            
            // Generate JWT token for immediate login after registration
            String token = jwtUtil.generateToken(savedStudent.getRollNumber(), "STUDENT");
            
            return ResponseEntity.ok(new AuthResponse(
                token,
                "Registration successful. You are now logged in.",
                "STUDENT",
                savedStudent.getRollNumber(),
                savedStudent
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Registration failed: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    @PostMapping("/faculty/register")
    public ResponseEntity<?> registerFaculty(@RequestBody AuthRequest authRequest) {
        try {
            // Check if faculty already exists
            Faculty existingFaculty = facultyService.getFacultyByFacultyId(authRequest.getUsername());
            if (existingFaculty != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                            "message", "Faculty already exists with ID: " + authRequest.getUsername(),
                            "error", "USER_EXISTS"
                        ));
            }
            
            // Create new faculty
            Faculty newFaculty = new Faculty();
            newFaculty.setFacultyId(authRequest.getUsername());
            newFaculty.setPassword(authRequest.getPassword());
            
            Faculty savedFaculty = facultyService.register(newFaculty);
            
            // Generate JWT token for immediate login after registration
            String token = jwtUtil.generateToken(savedFaculty.getFacultyId(), "FACULTY");
            
            return ResponseEntity.ok(new AuthResponse(
                token,
                "Registration successful. You are now logged in.",
                "FACULTY",
                savedFaculty.getFacultyId(),
                savedFaculty
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "message", "Registration failed: " + e.getMessage(),
                        "error", "SERVER_ERROR"
                    ));
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);
                String userType = jwtUtil.extractUserType(token);
                
                if (jwtUtil.isTokenValid(token)) {
                    return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "username", username,
                        "userType", userType,
                        "message", "Token is valid"
                    ));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "valid", false, 
                        "message", "Invalid token",
                        "error", "INVALID_TOKEN"
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "valid", false, 
                        "message", "Token validation failed: " + e.getMessage(),
                        "error", "TOKEN_VALIDATION_FAILED"
                    ));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);
                String userType = jwtUtil.extractUserType(token);
                
                if (jwtUtil.isTokenValid(token)) {
                    String newToken = jwtUtil.generateToken(username, userType);
                    return ResponseEntity.ok(Map.of(
                        "token", newToken,
                        "username", username,
                        "userType", userType,
                        "message", "Token refreshed successfully"
                    ));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "message", "Invalid token for refresh",
                        "error", "INVALID_TOKEN"
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "message", "Token refresh failed: " + e.getMessage(),
                        "error", "TOKEN_REFRESH_FAILED"
                    ));
        }
    }
}
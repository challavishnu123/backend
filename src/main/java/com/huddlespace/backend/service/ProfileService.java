package com.huddlespace.backend.service;

import com.huddlespace.backend.entity.Faculty;
import com.huddlespace.backend.entity.Student;
import com.huddlespace.backend.repository.FacultyRepository;
import com.huddlespace.backend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired private StudentRepository studentRepository;
    @Autowired private FacultyRepository facultyRepository;

    public Object getProfileByUsername(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isFaculty = auth.getAuthorities().stream()
                             .anyMatch(a -> a.getAuthority().equals("ROLE_FACULTY"));

        Object profileUser = findUser(username);
        if (profileUser == null) {
            throw new IllegalStateException("Profile not found.");
        }

        if (currentUsername.equals(username)) {
            return profileUser;
        }

        if (isFaculty && profileUser instanceof Student) {
            return profileUser;
        }
        
        Object currentUser = findUser(currentUsername);
        if (currentUser instanceof Student && ((Student) currentUser).getConnections().contains(username)) {
            return profileUser;
        }
        if (currentUser instanceof Faculty && ((Faculty) currentUser).getConnections().contains(username)) {
            return profileUser;
        }

        throw new IllegalStateException("You do not have permission to view this profile.");
    }

    public Object updateProfile(Map<String, Object> profileData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        
        Object user = findUser(currentUsername);

        if (user instanceof Student s) {
            s.setName((String) profileData.get("name"));
            s.setYear((String) profileData.get("year"));
            s.setDepartment((String) profileData.get("department"));
            s.setSection((String) profileData.get("section"));
            s.setEmail((String) profileData.get("email"));
            s.setLinkedinUrl((String) profileData.get("linkedinUrl"));
            s.setGithubUrl((String) profileData.get("githubUrl"));
            return studentRepository.save(s);
        } else if (user instanceof Faculty f) {
            f.setName((String) profileData.get("name"));
            f.setDepartment((String) profileData.get("department"));
            f.setEmail((String) profileData.get("email"));
            f.setLinkedinUrl((String) profileData.get("linkedinUrl"));
            f.setSubjects((List<String>) profileData.get("subjects"));
            return facultyRepository.save(f);
        }
        
        throw new RuntimeException("Could not find user to update.");
    }

    private Object findUser(String username) {
        Optional<Student> studentOptional = studentRepository.findByRollNumber(username);
        if (studentOptional.isPresent()) {
            return studentOptional.get();
        }
        return facultyRepository.findByFacultyId(username).orElse(null);
    }
}
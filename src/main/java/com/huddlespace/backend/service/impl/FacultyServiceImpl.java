package com.huddlespace.backend.service.impl;

import com.huddlespace.backend.entity.Faculty;
import com.huddlespace.backend.repository.FacultyRepository;
import com.huddlespace.backend.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyServiceImpl implements FacultyService {

    @Autowired
    private FacultyRepository repository;

    public Faculty register(Faculty faculty) {
        return repository.save(faculty);
    }

    public Faculty login(String facultyId, String password) {
        Faculty faculty = repository.findByFacultyId(facultyId);
        return (faculty != null && faculty.getPassword().equals(password)) ? faculty : null;
    }

    public Faculty updatePassword(String facultyId, String newPassword) {
        Faculty faculty = repository.findByFacultyId(facultyId);
        if (faculty != null) {
            faculty.setPassword(newPassword);
            return repository.save(faculty);
        }
        return null;
    }

    public void delete(String facultyId) {
        repository.deleteById(facultyId);
    }

    public List<Faculty> getAllFaculties() {
        return repository.findAll();
    }
    
    @Override
    public Faculty getFacultyByFacultyId(String facultyId) {
        return repository.findByFacultyId(facultyId);
    }

}

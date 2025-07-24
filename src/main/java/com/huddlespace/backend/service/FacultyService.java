package com.huddlespace.backend.service;

import com.huddlespace.backend.entity.Faculty;
import java.util.List;

public interface FacultyService {
    Faculty register(Faculty faculty);
    Faculty login(String facultyId, String password);
    Faculty updatePassword(String facultyId, String newPassword);
    void delete(String facultyId);
    List<Faculty> getAllFaculties();
    Faculty getFacultyByFacultyId(String facultyId);

}

package com.timetable.timetablebe.controllers;

import com.timetable.timetablebe.dtos.StudentGroupDto;
import com.timetable.timetablebe.services.StudentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student-groups")
public class StudentGroupController {
    @Autowired
    private StudentGroupService studentGroupService;

    @GetMapping
    public ResponseEntity<List<StudentGroupDto>> getAllStudentGroups() {
        return new ResponseEntity<>(studentGroupService.getAllStudentGroups(), null, HttpStatus.OK);
    }
}

package com.timetable.timetablebe.controllers;

import com.timetable.timetablebe.dtos.ProfessorDto;
import com.timetable.timetablebe.services.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {
    @Autowired
    private ProfessorService professorService;

    @GetMapping
    public ResponseEntity<List<ProfessorDto>> getAllProfessors() {
        return new ResponseEntity<>(professorService.getAllProfessors(), null, HttpStatus.OK);
    }
}

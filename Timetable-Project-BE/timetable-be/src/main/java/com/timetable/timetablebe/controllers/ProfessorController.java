package com.timetable.timetablebe.controllers;

import com.timetable.timetablebe.dtos.ProfessorDto;
import com.timetable.timetablebe.services.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<ProfessorDto> getAllProfessors() {
        return professorService.getAllProfessors();
    }
}

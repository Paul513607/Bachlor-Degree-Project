package com.timetable.timetablebe.services;

import com.timetable.timetablebe.dtos.ProfessorDto;
import com.timetable.timetablebe.entities.ProfessorEntity;
import com.timetable.timetablebe.repos.ProfessorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfessorService {
    @Autowired
    private ProfessorRepository professorRepo;

    @Autowired
    private ModelMapper mapper;

    private List<ProfessorDto> mapEntityListToDtoList(List<ProfessorEntity> professorList) {
        List<ProfessorDto> professorDtos = new ArrayList<>();
        for (ProfessorEntity professor : professorList) {
            ProfessorDto professorDto = mapper.map(professor, ProfessorDto.class);
            professorDtos.add(professorDto);
        }

        return professorDtos;
    }

    public List<ProfessorDto> getAllProfessors() {
        List<ProfessorEntity> professorList = professorRepo.findAllByOrderByAbbr();
        return mapEntityListToDtoList(professorList);
    }
}

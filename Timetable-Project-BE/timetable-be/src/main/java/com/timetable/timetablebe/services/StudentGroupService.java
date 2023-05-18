package com.timetable.timetablebe.services;

import com.timetable.timetablebe.dtos.StudentGroupDto;
import com.timetable.timetablebe.entities.StudentGroupEntity;
import com.timetable.timetablebe.repos.StudentGroupRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentGroupService {
    @Autowired
    private StudentGroupRepository studentGroupRepo;

    @Autowired
    private ModelMapper mapper;

    private List<StudentGroupDto> mapEntityListToDtoList(List<StudentGroupEntity> studentGroupList) {
        List<StudentGroupDto> studentGroupDtos = new ArrayList<>();
        for (StudentGroupEntity studentGroup : studentGroupList) {
            StudentGroupDto studentGroupDto = mapper.map(studentGroup, StudentGroupDto.class);
            studentGroupDtos.add(studentGroupDto);
        }

        return studentGroupDtos;
    }

    public List<StudentGroupDto> getAllStudentGroups() {
        List<StudentGroupEntity> studentGroupList = studentGroupRepo.findAllByOrderByAbbr();
        return mapEntityListToDtoList(studentGroupList);
    }
}

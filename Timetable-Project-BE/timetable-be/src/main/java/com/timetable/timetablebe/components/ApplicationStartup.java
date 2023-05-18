package com.timetable.timetablebe.components;

import com.timetable.timetablebe.entities.EventEntity;
import com.timetable.timetablebe.entities.ProfessorEntity;
import com.timetable.timetablebe.entities.ResourceEntity;
import com.timetable.timetablebe.entities.StudentGroupEntity;
import com.timetable.timetablebe.repos.EventRepository;
import com.timetable.timetablebe.repos.ProfessorRepository;
import com.timetable.timetablebe.repos.ResourceRepository;
import com.timetable.timetablebe.repos.StudentGroupRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.timetable.pojo.Timetable;

import java.util.ArrayList;
import java.util.List;

import static org.timetable.Main.loadTimetable;

@Component
public class ApplicationStartup implements ApplicationRunner {
    public static final String XML_FILEPATH = "src/main/resources/data/export_2022-2023_semestrul_1.xml";

    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private ResourceRepository resourceRepo;
    @Autowired
    private StudentGroupRepository studentGroupRepo;
    @Autowired
    private ProfessorRepository professorRepo;

    @Autowired
    private ModelMapper mapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Timetable timetable = loadTimetable(XML_FILEPATH);

        List<EventEntity> eventEntities = timetable.getEvents().stream()
                .map(group -> mapper.map(group, EventEntity.class))
                .toList();
        eventRepo.saveAll(eventEntities);
        List<ResourceEntity> resourceEntities = timetable.getResources().stream()
                .map(resource -> mapper.map(resource, ResourceEntity.class))
                .toList();
        resourceRepo.saveAll(resourceEntities);
        List<StudentGroupEntity> studentGroupEntities = timetable.getGroups().stream()
                .map(group -> mapper.map(group, StudentGroupEntity.class))
                .toList();
        studentGroupRepo.saveAll(studentGroupEntities);
        List<ProfessorEntity> professorEntities = timetable.getProfs().stream()
                .map(prof -> mapper.map(prof, ProfessorEntity.class))
                .toList();
        professorRepo.saveAll(professorEntities);
    }
}

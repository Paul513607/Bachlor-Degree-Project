package com.timetable.timetablebe.components;

import com.timetable.timetablebe.entities.*;
import com.timetable.timetablebe.repos.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.timetable.algorithm.wraps.ColorDayTimeWrap;
import org.timetable.generic_model.TimetableNode;
import org.timetable.pojo.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.timetable.Main.loadTimetable;

@Component
public class ApplicationStartup implements ApplicationRunner, ApplicationStartupBase {
    public static final String XML_FILEPATH = "src/main/resources/data/export_2022-2023_semestrul_1.xml";
    public static File XML_FILE = new File(XML_FILEPATH);

    @Autowired
    private AssignedEventRepository assignedEventRepo;
    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private ResourceRepository resourceRepo;
    @Autowired
    private StudentGroupRepository studentGroupRepo;
    @Autowired
    private ProfessorRepository professorRepo;
    @Autowired
    private TimetableFileRepository timetableFileRepo;

    @Autowired
    private ModelMapper mapper;

    private List<AssignedEventEntity> mapAlgorithmResultsToEntities(Map<TimetableNode, ColorDayTimeWrap> timetable) {
        return getAssignedEventEntities(timetable, mapper);
    }

    public static List<AssignedEventEntity> getAssignedEventEntities(Map<TimetableNode, ColorDayTimeWrap> timetable, ModelMapper mapper) {
        List<AssignedEventEntity> assignedEventEntities = new ArrayList<>();
        for (Map.Entry<TimetableNode, ColorDayTimeWrap> entry : timetable.entrySet()) {
            TimetableNode timetableNode = entry.getKey();
            ColorDayTimeWrap colorDayTimeWrap = entry.getValue();

            EventEntity eventEntity = mapper.map(timetableNode.getEvent(), EventEntity.class);
            for (Group group : timetableNode.getEvent().getGroupList()) {
                eventEntity.addStudentGroup(mapper.map(group, StudentGroupEntity.class));
            }
            for (Prof prof : timetableNode.getEvent().getProfList()) {
                eventEntity.addProf(mapper.map(prof, ProfessorEntity.class));
            }

            ResourceEntity resourceEntity = mapper.map(colorDayTimeWrap.getColor().getResource(), ResourceEntity.class);
            int day = colorDayTimeWrap.getDay();
            LocalTime time = LocalTime.of(colorDayTimeWrap.getTime(), 0);

            AssignedEventEntity assignedEventEntity = new AssignedEventEntity(eventEntity, resourceEntity, day, time);

            assignedEventEntities.add(assignedEventEntity);
        }

        return assignedEventEntities;
    }

    @Override
    public void initializeDatabase() throws IOException {
        List<TimetableFileEntity> timetableFileEntities = timetableFileRepo.findAll();

        if (timetableFileEntities.isEmpty()) {
            File file = new File(XML_FILEPATH);
            TimetableFileEntity timetableFileEntity = new TimetableFileEntity();
            timetableFileEntity.setFile(file);
            timetableFileEntity.setName(file.getName());
            timetableFileEntity.setTimestampAdded(System.currentTimeMillis());
            timetableFileRepo.save(timetableFileEntity);

            XML_FILE = file;
        }

        Timetable timetable = loadTimetable(Files.readAllBytes(XML_FILE.toPath()));
        assignedEventRepo.deleteAll();
        resourceRepo.deleteAll();
        studentGroupRepo.deleteAll();
        professorRepo.deleteAll();
        eventRepo.deleteAll();

        if (resourceRepo.findAll().isEmpty()) {
            List<ResourceEntity> resourceEntities = timetable.getResources().stream()
                    .map(resource -> mapper.map(resource, ResourceEntity.class))
                    .toList();
            resourceRepo.saveAll(resourceEntities);
        }

        if (!eventRepo.findAll().isEmpty()) {
            return;
        }

        List<EventEntity> eventEntities = timetable.getEvents().stream()
                .map(event -> {
                    EventEntity eventEntity = mapper.map(event, EventEntity.class);
                    for (Group group : event.getGroupList()) {
                        eventEntity.addStudentGroup(mapper.map(group, StudentGroupEntity.class));
                    }
                    for (Prof prof : event.getProfList()) {
                        eventEntity.addProf(mapper.map(prof, ProfessorEntity.class));
                    }
                    return eventEntity;
                })
                .toList();

        List<StudentGroupEntity> studentGroupEntities = timetable.getGroups().stream()
                .map(group -> {
                    StudentGroupEntity studentGroupEntity = mapper.map(group, StudentGroupEntity.class);

                    for (EventEntity eventEntity : eventEntities) {
                        if (eventEntity.getStudentGroups().stream()
                                .anyMatch(studentGroup ->
                                        studentGroup.getAbbr().equals(studentGroupEntity.getAbbr()))) {
                            studentGroupEntity.addEvent(eventEntity);
                        }
                    }
                    return studentGroupEntity;
                })
                .toList();
        List<ProfessorEntity> professorEntities = timetable.getProfs().stream()
                .map(prof -> {
                    ProfessorEntity professorEntity = mapper.map(prof, ProfessorEntity.class);

                    for (EventEntity eventEntity : eventEntities) {
                        if (eventEntity.getProfessors().stream()
                                .anyMatch(professor ->
                                        professor.getAbbr().equals(professorEntity.getAbbr()))) {
                            professorEntity.addEvent(eventEntity);
                        }
                    }
                    return professorEntity;
                })
                .toList();
        eventRepo.saveAll(eventEntities);
        studentGroupRepo.saveAll(studentGroupEntities);
        professorRepo.saveAll(professorEntities);

        if (assignedEventRepo.findAll().isEmpty()) {
            List<Assignment> assignments = timetable.getAssignments();

            for (Assignment assignment : assignments) {
                String eventAbbr = assignment.getEvent();
                String resourceAbbr = assignment.getResources().split(",")[0];
                int day = assignment.getDay();
                LocalTime time = assignment.getStartTime();

                if (eventAbbr.equals("") || resourceAbbr.equals("")) {
                    continue;
                }

                Optional<EventEntity> eventEntityOpt = eventRepo.findByAbbr(eventAbbr);
                Optional<ResourceEntity> resourceEntityOpt = resourceRepo.findByAbbr(resourceAbbr);

                if (eventEntityOpt.isEmpty() || resourceEntityOpt.isEmpty()) {
                    continue;
                }

                assignedEventRepo.save(
                        new AssignedEventEntity(eventEntityOpt.get(), resourceEntityOpt.get(), day, time));
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeDatabase();
    }
}

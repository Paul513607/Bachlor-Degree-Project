package com.timetable.timetablebe.services;

import com.timetable.timetablebe.components.ApplicationStartup;
import com.timetable.timetablebe.dtos.AssignedEventDto;
import com.timetable.timetablebe.dtos.EventDto;
import com.timetable.timetablebe.dtos.ResourceDto;
import com.timetable.timetablebe.entities.*;
import com.timetable.timetablebe.repos.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timetable.algorithm.wraps.ColorDayTimeWrap;
import org.timetable.model.TimetableNode;
import org.timetable.pojo.Group;
import org.timetable.pojo.Prof;

import java.time.LocalTime;
import java.util.*;

import static org.timetable.Main.*;

@Service
public class AssignedEventService {
    private static final String DEFAULT_ALGORITHM_OPTION = "1";
    private String cachedAlgorithmOption = "";

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
    private ModelMapper mapper;

    private List<AssignedEventDto> mapEntityListToDtoList(List<AssignedEventEntity> assignedEvents) {
        List<AssignedEventDto> assignedEventDtos = new ArrayList<>();
        for (AssignedEventEntity assignedEvent : assignedEvents) {
            EventDto eventDto = mapper.map(assignedEvent.getEvent(), EventDto.class);
            eventDto.setStudentGroupAbbrs(assignedEvent.getEvent().getStudentGroups().stream()
                    .map(StudentGroupEntity::getAbbr).toList());
            eventDto.setProfAbbrs(assignedEvent.getEvent().getProfessors().stream()
                    .map(ProfessorEntity::getAbbr).toList());

            ResourceDto resourceDto = mapper.map(assignedEvent.getResource(), ResourceDto.class);

            AssignedEventDto assignedEventDto =
                    new AssignedEventDto(eventDto, resourceDto, assignedEvent.getDay(), assignedEvent.getTime());
            assignedEventDtos.add(assignedEventDto);
        }

        return assignedEventDtos;
    }

    private List<AssignedEventEntity> mapAlgorithmResultsToEntities(Map<TimetableNode, ColorDayTimeWrap> timetable) {
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

    public List<AssignedEventDto> getAssignedEvents() {
        List<AssignedEventEntity> assignedEvents = assignedEventRepo.findAll();

        return mapEntityListToDtoList(assignedEvents);
    }

    private void resetDatabase(List<AssignedEventEntity> assignedEvents) {
        // if the option was changed, reset the database
        assignedEventRepo.deleteAll();
        studentGroupRepo.deleteAll();
        professorRepo.deleteAll();
        eventRepo.deleteAll();
        resourceRepo.deleteAll();

        Set<StudentGroupEntity> studentGroups = new HashSet<>();
        Set<ProfessorEntity> professors = new HashSet<>();
        for (AssignedEventEntity assignedEvent : assignedEvents) {
            studentGroups.addAll(assignedEvent.getEvent().getStudentGroups());
            professors.addAll(assignedEvent.getEvent().getProfessors());
        }

        studentGroupRepo.saveAll(studentGroups);
        professorRepo.saveAll(professors);
        eventRepo.saveAll(assignedEvents.stream().map(AssignedEventEntity::getEvent).toList());
        resourceRepo.saveAll(assignedEvents.stream().map(AssignedEventEntity::getResource).toList());
        assignedEventRepo.saveAll(assignedEvents);
    }

    public List<AssignedEventDto> getAssignedEventsByAlgorithm(String algorithmOption) {
        // if the option was changed, reset the database
        assignedEventRepo.deleteAll();
        studentGroupRepo.deleteAll();
        professorRepo.deleteAll();
        eventRepo.deleteAll();
        resourceRepo.deleteAll();

        List<AssignedEventEntity> assignedEvents;
        Map<TimetableNode, ColorDayTimeWrap> timetable;

        if (cachedAlgorithmOption.equals(algorithmOption)) {
            assignedEvents = assignedEventRepo.findAll();
            return mapEntityListToDtoList(assignedEvents);
        }

        switch (algorithmOption) {
            case "1" -> {
                timetable = roomOnlyColoring(ApplicationStartup.XML_FILEPATH);
                assignedEvents = mapAlgorithmResultsToEntities(timetable);

                cachedAlgorithmOption = algorithmOption;
                resetDatabase(assignedEvents);
            }
            case "2" -> {
                timetable = intervalRoomColoringGreedy(ApplicationStartup.XML_FILEPATH);
                assignedEvents = mapAlgorithmResultsToEntities(timetable);

                cachedAlgorithmOption = algorithmOption;
                resetDatabase(assignedEvents);
            }
            case "3" -> {
                timetable = intervalRoomColoringDSatur(ApplicationStartup.XML_FILEPATH);
                assignedEvents = mapAlgorithmResultsToEntities(timetable);

                cachedAlgorithmOption = algorithmOption;
                resetDatabase(assignedEvents);
            }
            default -> assignedEvents = assignedEventRepo.findAll();
        }

        return mapEntityListToDtoList(assignedEvents);
    }

    public List<AssignedEventDto> getAssignedEventsByStudentGroup(String abbr) {
        List<AssignedEventEntity> assignedEvents = assignedEventRepo.findAllByStudentGroupAbbr(abbr);
        // if the list is empty, we need to generate
        if (assignedEvents.isEmpty()) {
            Map<TimetableNode, ColorDayTimeWrap> timetable; timetable =
                    roomOnlyColoring(ApplicationStartup.XML_FILEPATH);
            assignedEvents = mapAlgorithmResultsToEntities(timetable);

            cachedAlgorithmOption = DEFAULT_ALGORITHM_OPTION;
            resetDatabase(assignedEvents);
        }

        return mapEntityListToDtoList(assignedEvents);
    }
}

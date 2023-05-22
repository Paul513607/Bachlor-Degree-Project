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
        return ApplicationStartup.getAssignedEventEntities(timetable, mapper);
    }

    public List<AssignedEventDto> getAssignedEvents() {
        List<AssignedEventEntity> assignedEvents = assignedEventRepo.findAll();

        return mapEntityListToDtoList(assignedEvents);
    }

    private void resetDatabase(List<AssignedEventEntity> assignedEvents) {
        // if the option was changed, reset the database
        assignedEventRepo.deleteAll();

        for (AssignedEventEntity assignedEvent : assignedEvents) {
            EventEntity event = assignedEvent.getEvent();
            ResourceEntity resource = assignedEvent.getResource();

            Optional<EventEntity> eventOptional = eventRepo.findByAbbr(event.getAbbr());
            if (eventOptional.isEmpty()) {
                continue;
            }

            Optional<ResourceEntity> resourceOptional = resourceRepo.findByAbbr(resource.getAbbr());
            if (resourceOptional.isEmpty()) {
                continue;
            }
            event = eventOptional.get();
            assignedEvent.setEvent(event);
            resource = resourceOptional.get();
            assignedEvent.setResource(resource);

            assignedEventRepo.save(assignedEvent);
            event.setAssignedEvent(assignedEvent);
            eventRepo.save(event);
            resource.addAssignedEvent(assignedEvent);
            resourceRepo.save(resource);
        }
    }

    public List<AssignedEventDto> getAssignedEventsByAlgorithm(String algorithmOption) {
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

    private void regenerateTimetable() {
        System.out.println("Generating timetable");
        Map<TimetableNode, ColorDayTimeWrap> timetable =
                roomOnlyColoring(ApplicationStartup.XML_FILEPATH);
        List<AssignedEventEntity> assignedEvents = mapAlgorithmResultsToEntities(timetable);

        cachedAlgorithmOption = DEFAULT_ALGORITHM_OPTION;
        resetDatabase(assignedEvents);
    }

    public List<AssignedEventDto> getAssignedEventsByStudentGroup(String abbr) {
        List<AssignedEventEntity> assignedEvents = assignedEventRepo.findAllByStudentGroupAbbr(abbr);
        // if the list is empty, we need to generate
        if (assignedEvents.isEmpty()) {
            regenerateTimetable();
        }
        assignedEvents = assignedEventRepo.findAllByStudentGroupAbbr(abbr);

        return mapEntityListToDtoList(assignedEvents);
    }

    public List<AssignedEventDto> getAssignedEventsByProfessor(String abbr) {
        List<AssignedEventEntity> assignedEvents = assignedEventRepo.findAllByProfAbbr(abbr);
        // if the list is empty, we need to generate
        if (assignedEvents.isEmpty()) {
            regenerateTimetable();
        }
        assignedEvents = assignedEventRepo.findAllByProfAbbr(abbr);
        return mapEntityListToDtoList(assignedEvents);
    }

    public List<AssignedEventDto> getAssignedEventsByRoom(String abbr) {
        List<AssignedEventEntity> assignedEvents = assignedEventRepo.findAllByRoomAbbr(abbr);
        // if the list is empty, we need to generate
        if (assignedEvents.isEmpty()) {
            regenerateTimetable();
        }
        assignedEvents = assignedEventRepo.findAllByRoomAbbr(abbr);

        return mapEntityListToDtoList(assignedEvents);
    }
}

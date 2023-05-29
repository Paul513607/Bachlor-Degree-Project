package com.timetable.timetablebe.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.timetable.timetablebe.components.ApplicationStartup;
import com.timetable.timetablebe.dtos.AssignedEventDto;
import com.timetable.timetablebe.dtos.EventDto;
import com.timetable.timetablebe.dtos.ResourceDto;
import com.timetable.timetablebe.entities.*;
import com.timetable.timetablebe.exceptions.AssignedEventNotFoundException;
import com.timetable.timetablebe.exceptions.EventNotFoundException;
import com.timetable.timetablebe.exceptions.ResourceNotFoundException;
import com.timetable.timetablebe.repos.*;
import com.timetable.timetablebe.util.AssignedEventPojo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.timetable.algorithm.wraps.ColorDayTimeWrap;
import org.timetable.model.TimetableNode;
import org.timetable.pojo.Group;
import org.timetable.pojo.Prof;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalTime;
import java.util.*;

import static org.timetable.Main.*;

@Service
public class AssignedEventService {
    private final Integer DEFAULT_START_HOUR = 8;
    private static final String DEFAULT_ALGORITHM_OPTION = "2";
    public static String cachedAlgorithmOption = "";

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

    public List<AssignedEventDto> getAssignedEventsByAlgorithm(String algorithmOption) throws IOException {
        List<AssignedEventEntity> assignedEvents;
        Map<TimetableNode, ColorDayTimeWrap> timetable;

        if (cachedAlgorithmOption.equals(algorithmOption)) {
            assignedEvents = assignedEventRepo.findAll();
            return mapEntityListToDtoList(assignedEvents);
        }

        switch (algorithmOption) {
            case "1" -> {
                timetable = roomOnlyColoringFileContent(
                        Files.readAllBytes(ApplicationStartup.XML_FILE.toPath()));
                assignedEvents = mapAlgorithmResultsToEntities(timetable);

                cachedAlgorithmOption = algorithmOption;
                resetDatabase(assignedEvents);
            }
            case "2" -> {
                timetable = intervalRoomColoringGreedyFileContent(
                        Files.readAllBytes(ApplicationStartup.XML_FILE.toPath()));
                assignedEvents = mapAlgorithmResultsToEntities(timetable);

                cachedAlgorithmOption = algorithmOption;
                resetDatabase(assignedEvents);
            }
            case "3" -> {
                timetable = intervalRoomColoringDSaturFileContent(
                        Files.readAllBytes(ApplicationStartup.XML_FILE.toPath()));
                assignedEvents = mapAlgorithmResultsToEntities(timetable);

                cachedAlgorithmOption = algorithmOption;
                resetDatabase(assignedEvents);
            }
            default -> assignedEvents = assignedEventRepo.findAll();
        }

        return mapEntityListToDtoList(assignedEvents);
    }

    private void regenerateTimetable() throws IOException {
        System.out.println("Generating timetable");
        Map<TimetableNode, ColorDayTimeWrap> timetable =
                intervalRoomColoringGreedyFileContent(Files.readAllBytes(ApplicationStartup.XML_FILE.toPath()));
        List<AssignedEventEntity> assignedEvents = mapAlgorithmResultsToEntities(timetable);

        cachedAlgorithmOption = DEFAULT_ALGORITHM_OPTION;
        resetDatabase(assignedEvents);
    }

    public List<AssignedEventDto> getAssignedEventsByStudentGroup(String abbr) throws IOException {
        List<AssignedEventEntity> assignedEvents = assignedEventRepo.findAllByStudentGroupAbbr(abbr);
        // if the list is empty, we need to generate
        if (assignedEvents.isEmpty()) {
            regenerateTimetable();
        }
        assignedEvents = assignedEventRepo.findAllByStudentGroupAbbr(abbr);

        return mapEntityListToDtoList(assignedEvents);
    }

    public List<AssignedEventDto> getAssignedEventsByProfessor(String abbr) throws IOException {
        List<AssignedEventEntity> assignedEvents = assignedEventRepo.findAllByProfAbbr(abbr);
        // if the list is empty, we need to generate
        if (assignedEvents.isEmpty()) {
            regenerateTimetable();
        }
        assignedEvents = assignedEventRepo.findAllByProfAbbr(abbr);
        return mapEntityListToDtoList(assignedEvents);
    }

    public List<AssignedEventDto> getAssignedEventsByRoom(String abbr) throws IOException {
        List<AssignedEventEntity> assignedEvents = assignedEventRepo.findAllByRoomAbbr(abbr);
        // if the list is empty, we need to generate
        if (assignedEvents.isEmpty()) {
            regenerateTimetable();
        }
        assignedEvents = assignedEventRepo.findAllByRoomAbbr(abbr);

        return mapEntityListToDtoList(assignedEvents);
    }

    public AssignedEventDto addNewAssignment(AssignedEventDto assignedEventDto) {
        Optional<EventEntity> eventOpt = eventRepo.findByAbbr(assignedEventDto.getEvent().getAbbr());
        if (eventOpt.isEmpty()) {
            throw new EventNotFoundException("Event with abbr " +
                    assignedEventDto.getEvent().getAbbr() + " not found");
        }

        Optional<ResourceEntity> resourceOpt = resourceRepo.findByAbbr(assignedEventDto.getResource().getAbbr());
        if (resourceOpt.isEmpty()) {
            throw new ResourceNotFoundException("Resource with abbr " +
                    assignedEventDto.getResource().getAbbr() + " not found");
        }

        AssignedEventEntity assignedEvent = new AssignedEventEntity();
        assignedEvent.setEvent(eventOpt.get());
        assignedEvent.setResource(resourceOpt.get());
        assignedEvent.setDay(assignedEventDto.getDay());
        assignedEvent.setTime(assignedEventDto.getTime());

        AssignedEventEntity createdEvent = assignedEventRepo.save(assignedEvent);
        return mapper.map(createdEvent, AssignedEventDto.class);
    }

    public AssignedEventDto updateAssignment(AssignedEventDto assignedEventDto) {
        Optional<EventEntity> eventOpt = eventRepo.findByAbbr(assignedEventDto.getEvent().getAbbr());
        if (eventOpt.isEmpty()) {
            throw new EventNotFoundException("Event with abbr " +
                    assignedEventDto.getEvent().getAbbr() + " not found");
        }

        Optional<AssignedEventEntity> assignedEventOpt = assignedEventRepo.findFirstByEvent(eventOpt.get());
        if (assignedEventOpt.isEmpty()) {
            throw new AssignedEventNotFoundException("Assigned event for event with abbr " +
                    assignedEventDto.getEvent().getAbbr() + " not found");
        }

        Optional<ResourceEntity> resourceOpt = resourceRepo.findByAbbr(assignedEventDto.getResource().getAbbr());
        if (resourceOpt.isEmpty()) {
            throw new ResourceNotFoundException("Resource with abbr " +
                    assignedEventDto.getResource().getAbbr() + " not found");
        }

        assignedEventRepo.delete(assignedEventOpt.get());

        AssignedEventEntity assignedEvent = new AssignedEventEntity();
        assignedEvent.setEvent(eventOpt.get());
        assignedEvent.setResource(resourceOpt.get());
        assignedEvent.setDay(assignedEventDto.getDay());
        assignedEvent.setTime(assignedEventDto.getTime());

        AssignedEventEntity updatedEvent = assignedEventRepo.save(assignedEvent);
        return mapper.map(updatedEvent, AssignedEventDto.class);
    }

    public Long getAssignmentId(AssignedEventDto assignedEventDto) {
        Optional<EventEntity> eventOpt = eventRepo.findByAbbr(assignedEventDto.getEvent().getAbbr());
        if (eventOpt.isEmpty()) {
            throw new EventNotFoundException("Event with abbr " +
                    assignedEventDto.getEvent().getAbbr() + " not found");
        }

        Optional<AssignedEventEntity> assignedEventOpt = assignedEventRepo.findFirstByEvent(eventOpt.get());
        if (assignedEventOpt.isEmpty()) {
            throw new AssignedEventNotFoundException("Assigned event for event with abbr " +
                    assignedEventDto.getEvent().getAbbr() + " not found");
        }

        return assignedEventOpt.get().getId();
    }

    public void deleteAssignment(Long id) {
        Optional<AssignedEventEntity> assignedEventOpt = assignedEventRepo.findById(id);
        if (assignedEventOpt.isEmpty()) {
            throw new AssignedEventNotFoundException("Assigned event with id " + id + " not found");
        }

        assignedEventRepo.delete(assignedEventOpt.get());
    }

    private String formatTimeAsString(LocalTime time) {
        int currentHour = time.getHour();
        int currentMinute = time.getMinute();
        StringBuilder timeStringBuilder = new StringBuilder();
        if (currentHour < 10) {
            timeStringBuilder.append("0");
        }
        timeStringBuilder.append(currentHour).append(":");
        if (currentMinute < 10) {
            timeStringBuilder.append("0");
        }
        timeStringBuilder.append(currentMinute);
        return timeStringBuilder.toString();
    }

    public byte[] downloadTimetableConfiguration() {
        List<AssignedEventEntity> assignedEvents = assignedEventRepo.findAllOrderByDayAndTime();
        StringBuilder xmlDataBuilder = new StringBuilder();
        xmlDataBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlDataBuilder.append("<assignments>\n");
        int startCounter = 0;
        int previousDay = 0;
        int previousHour = DEFAULT_START_HOUR;
        XmlMapper xmlMapper = new XmlMapper();

        for (AssignedEventEntity assignedEvent : assignedEvents) {
            if (previousDay != assignedEvent.getDay()) {
                startCounter += 2;
                previousDay = assignedEvent.getDay();
                previousHour = DEFAULT_START_HOUR;
            } else {
                if (previousHour != assignedEvent.getTime().getHour()) {
                    startCounter += 2;
                    previousHour = assignedEvent.getTime().getHour();
                }
            }

            String startTime = formatTimeAsString(assignedEvent.getTime());
            String endTime = formatTimeAsString(assignedEvent.getTime()
                                .plusHours(assignedEvent.getEvent().getDuration()));
            int start = startCounter;
            int end = startCounter + 2;
            int week = 0;

            AssignedEventPojo assignedEventPojo = new AssignedEventPojo(assignedEvent.getDay(), startTime, endTime,
                    assignedEvent.getEvent().getAbbr(), assignedEvent.getEvent().getNotes(),
                    assignedEvent.getResource().getAbbr(), start, end, week);
            String xml = null;
            try {
                xml = xmlMapper.writeValueAsString(assignedEventPojo);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if (xml != null) {
                xmlDataBuilder.append('\t');
                xmlDataBuilder.append(xml);
                xmlDataBuilder.append('\n');
            }
        }
        xmlDataBuilder.append("</assignments>\n");

        return xmlDataBuilder.toString().getBytes();
    }
}

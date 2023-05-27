package com.timetable.timetablebe.services;

import com.timetable.timetablebe.controllers.AvailabilitySlotController;
import com.timetable.timetablebe.dtos.AvailabilitySlotDto;
import com.timetable.timetablebe.dtos.EventDto;
import com.timetable.timetablebe.dtos.ResourceDto;
import com.timetable.timetablebe.entities.*;
import com.timetable.timetablebe.exceptions.EventNotFoundException;
import com.timetable.timetablebe.repos.AssignedEventRepository;
import com.timetable.timetablebe.repos.EventRepository;
import com.timetable.timetablebe.repos.ResourceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AvailabilitySlotService {
    private static final LocalTime START_TIME = LocalTime.of(8, 0);
    private static final LocalTime END_TIME = LocalTime.of(20, 0);
    private static final Integer GENERAL_DURATION = 2;

    private static final Map<String, String> eventTypeToResourceType = new HashMap<>();
    static {
        eventTypeToResourceType.put("C", "curs");
        eventTypeToResourceType.put("L", "lab");
        eventTypeToResourceType.put("S", "sem");
    }

    @Autowired
    private AssignedEventRepository assignedEventRepo;
    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private ResourceRepository resourceRepo;

    @Autowired
    private ModelMapper modelMapper;

    private Set<ResourceEntity> filterResources(EventEntity eventEntity, int day, LocalTime time,
                                                Set<ResourceEntity> resources,
                                                List<AssignedEventEntity> assignedEvents) {
        assignedEvents = assignedEvents.stream()
                .filter(assignedEvent -> assignedEvent.getDay() == day)
                .filter(assignedEvent -> assignedEvent.getTime().equals(time))
                .collect(Collectors.toList());

        // filter the resources (rooms) based on the student groups and professors of the current event
        for (AssignedEventEntity assignedEvent : assignedEvents) {
            for (StudentGroupEntity studentGroup : assignedEvent.getEvent().getStudentGroups()) {
                if (eventEntity.getStudentGroups().contains(studentGroup)) {
                    return new HashSet<>();
                }
            }
            for (ProfessorEntity professor : assignedEvent.getEvent().getProfessors()) {
                if (eventEntity.getProfessors().contains(professor)) {
                    return new HashSet<>();
                }
            }
        }

        // get the current resources (rooms) from the assigned events
        Set<ResourceEntity> currentEventResources = assignedEvents.stream()
                .map(AssignedEventEntity::getResource)
                .collect(Collectors.toSet());

        // filter the resources (rooms) that are available for the current event
        Set<ResourceEntity> availableRoomsSet = new HashSet<>(resources);
        availableRoomsSet.removeAll(currentEventResources);
        availableRoomsSet = availableRoomsSet.stream()
                .filter(resource -> resource.getCapacity() > 0)
                .filter(resource -> resource.getType()
                        .equals(eventTypeToResourceType.get(eventEntity.getType())))
                .collect(Collectors.toSet());
        return availableRoomsSet;
    }

    public List<AvailabilitySlotDto> getAvailableSlots(EventDto eventDto) {
        Optional<EventEntity> eventEntityOpt = eventRepo.findByAbbr(eventDto.getAbbr());

        if (eventEntityOpt.isEmpty()) {
            throw new EventNotFoundException("Event with abbr " + eventDto.getAbbr() + " not found");
        }
        EventEntity eventEntity = eventEntityOpt.get();
        List<AssignedEventEntity> assignedEvents = assignedEventRepo.findAll();
        Set<ResourceEntity> resources = new HashSet<>(resourceRepo.findAll());

        List<AvailabilitySlotDto> availabilitySlots = new ArrayList<>();

        for (int day = 0; day < 6; day++) {
            for (LocalTime time = START_TIME; time.isBefore(END_TIME); time = time.plusHours(GENERAL_DURATION)) {
                Set<ResourceEntity> availableRoomsSet =
                        filterResources(eventEntity, day, time, resources, assignedEvents);
                List<ResourceDto> availableRooms = availableRoomsSet.stream()
                        .map(resource -> modelMapper.map(resource, ResourceDto.class))
                        .sorted(Comparator.comparing(ResourceDto::getName))
                        .toList();
                availabilitySlots.add(new AvailabilitySlotDto(availableRooms, day, time));
            }
        }
        return availabilitySlots;
    }
}

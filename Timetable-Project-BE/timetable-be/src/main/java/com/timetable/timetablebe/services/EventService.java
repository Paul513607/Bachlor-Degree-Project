package com.timetable.timetablebe.services;

import com.timetable.timetablebe.dtos.EventDto;
import com.timetable.timetablebe.entities.EventEntity;
import com.timetable.timetablebe.entities.ProfessorEntity;
import com.timetable.timetablebe.entities.StudentGroupEntity;
import com.timetable.timetablebe.repos.EventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private ModelMapper mapper;

    private List<EventDto> mapEntityListToDtoList(List<EventEntity> events) {
        List<EventDto> eventDtos = new ArrayList<>();
        for (EventEntity event : events) {
            EventDto eventDto = mapper.map(event, EventDto.class);
            eventDto.setStudentGroupAbbrs(event.getStudentGroups().stream()
                    .map(StudentGroupEntity::getAbbr).toList());
            eventDto.setProfAbbrs(event.getProfessors().stream()
                    .map(ProfessorEntity::getAbbr).toList());
            eventDtos.add(eventDto);
        }

        return eventDtos;
    }

    public List<EventDto> getAllEvents() {
        List<EventEntity> events = eventRepo.findAll();
        return mapEntityListToDtoList(events);
    }

    public List<EventDto> getAllUnassignedEvents () {
        List<EventEntity> events = eventRepo.findAllUnassignedEvents();
        return mapEntityListToDtoList(events);
    }
}

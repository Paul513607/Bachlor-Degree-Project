package com.timetable.timetablebe.services;

import com.timetable.timetablebe.dtos.ResourceDto;
import com.timetable.timetablebe.entities.ResourceEntity;
import com.timetable.timetablebe.repos.ResourceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepo;

    @Autowired
    private ModelMapper mapper;

    public List<ResourceDto> getAllRooms() {
        List<ResourceEntity> rooms = resourceRepo.findAllSuitableRooms();
        return rooms.stream()
                .map(room -> mapper.map(room, ResourceDto.class))
                .toList();
    }
}

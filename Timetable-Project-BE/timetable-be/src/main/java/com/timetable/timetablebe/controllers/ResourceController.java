package com.timetable.timetablebe.controllers;

import com.timetable.timetablebe.dtos.ResourceDto;
import com.timetable.timetablebe.services.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @GetMapping("/rooms")
    public ResponseEntity<List<ResourceDto>> getAllRooms() {
        return new ResponseEntity<>(resourceService.getAllRooms(), null, HttpStatus.OK);
    }
}

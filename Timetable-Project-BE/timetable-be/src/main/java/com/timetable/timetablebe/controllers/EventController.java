package com.timetable.timetablebe.controllers;

import com.timetable.timetablebe.dtos.EventDto;
import com.timetable.timetablebe.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents() {
        return new ResponseEntity<>(eventService.getAllEvents(), null, HttpStatus.OK);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<EventDto>> getAllUnassignedEvents() {
        return new ResponseEntity<>(eventService.getAllUnassignedEvents(), null, HttpStatus.OK);
    }
}

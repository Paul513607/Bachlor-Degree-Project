package com.timetable.timetablebe.controllers;

import com.timetable.timetablebe.dtos.AssignedEventDto;
import com.timetable.timetablebe.services.AssignedEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assigned-events")
public class AssignedEventController {
    @Autowired
    private AssignedEventService assignedEventService;

    @GetMapping("/")
    public ResponseEntity<List<AssignedEventDto>> getAssignedEvents() {
        List<AssignedEventDto> assignedEventDtos = assignedEventService.getAssignedEvents();
        return new ResponseEntity<>(assignedEventDtos, null, HttpStatus.OK);
    }

    @GetMapping("/algorithm")
    public ResponseEntity<List<AssignedEventDto>> getAssignedEventsByAlgorithm(
                                        @RequestParam(name = "algorithmOption") String algorithmOption) {
        List<AssignedEventDto> assignedEventDtos = assignedEventService.getAssignedEventsByAlgorithm(algorithmOption);
        return new ResponseEntity<>(assignedEventDtos, null, HttpStatus.OK);
    }

    @GetMapping("/student-group")
    public ResponseEntity<List<AssignedEventDto>> getAssignedEventsByStudentGroup(
                                        @RequestParam(name = "abbr") String abbr) {
        List<AssignedEventDto> assignedEventDtos =
                assignedEventService.getAssignedEventsByStudentGroup(abbr);
        return new ResponseEntity<>(assignedEventDtos, null, HttpStatus.OK);
    }

    @GetMapping("/professor")
    public ResponseEntity<List<AssignedEventDto>> getAssignedEventsByProfessor(
                                        @RequestParam(name = "abbr") String abbr) {
        List<AssignedEventDto> assignedEventDtos =
                assignedEventService.getAssignedEventsByProfessor(abbr);
        return new ResponseEntity<>(assignedEventDtos, null, HttpStatus.OK);
    }

    @GetMapping("/room")
    public ResponseEntity<List<AssignedEventDto>> getAssignedEventsByRoom(
                                        @RequestParam(name = "abbr") String abbr) {
        List<AssignedEventDto> assignedEventDtos =
                assignedEventService.getAssignedEventsByRoom(abbr);
        return new ResponseEntity<>(assignedEventDtos, null, HttpStatus.OK);
    }
}

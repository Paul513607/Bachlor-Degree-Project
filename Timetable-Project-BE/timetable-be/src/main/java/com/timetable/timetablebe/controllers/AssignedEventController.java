package com.timetable.timetablebe.controllers;

import com.timetable.timetablebe.dtos.AssignedEventDto;
import com.timetable.timetablebe.dtos.EventDto;
import com.timetable.timetablebe.entities.EventEntity;
import com.timetable.timetablebe.exceptions.UnableToReadTimetableFileException;
import com.timetable.timetablebe.services.AssignedEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/assigned-events")
public class AssignedEventController {
    @Autowired
    private AssignedEventService assignedEventService;

    @GetMapping
    public ResponseEntity<List<AssignedEventDto>> getAssignedEvents() {
        List<AssignedEventDto> assignedEventDtos = assignedEventService.getAssignedEvents();
        return new ResponseEntity<>(assignedEventDtos, null, HttpStatus.OK);
    }

    @GetMapping("/algorithm")
    public ResponseEntity<List<AssignedEventDto>> getAssignedEventsByAlgorithm(
                                        @RequestParam(name = "algorithmOption") String algorithmOption,
                                        @RequestParam(name = "useSorting") Boolean useSorting,
                                        @RequestParam(name = "shuffle") Boolean shuffle) {
        List<AssignedEventDto> assignedEventDtos = new ArrayList<>();
        try {
            assignedEventDtos = assignedEventService.getAssignedEventsByAlgorithm(
                                            algorithmOption, useSorting, shuffle);
        } catch (IOException e) {
            throw new UnableToReadTimetableFileException("Unable to read timetable file");
        }
        return new ResponseEntity<>(assignedEventDtos, null, HttpStatus.OK);
    }

    @GetMapping("/student-group")
    public ResponseEntity<List<AssignedEventDto>> getAssignedEventsByStudentGroup(
                                        @RequestParam(name = "abbr") String abbr) {
        List<AssignedEventDto> assignedEventDtos = new ArrayList<>();
        try {
            assignedEventDtos = assignedEventService.getAssignedEventsByStudentGroup(abbr);
        } catch (IOException e) {
            throw new UnableToReadTimetableFileException("Unable to read timetable file");
        }
        return new ResponseEntity<>(assignedEventDtos, null, HttpStatus.OK);
    }

    @GetMapping("/professor")
    public ResponseEntity<List<AssignedEventDto>> getAssignedEventsByProfessor(
                                        @RequestParam(name = "abbr") String abbr) {
        List<AssignedEventDto> assignedEventDtos = new ArrayList<>();
        try {
            assignedEventDtos = assignedEventService.getAssignedEventsByProfessor(abbr);
        } catch (IOException e) {
            throw new UnableToReadTimetableFileException("Unable to read timetable file");
        }
        return new ResponseEntity<>(assignedEventDtos, null, HttpStatus.OK);
    }

    @GetMapping("/room")
    public ResponseEntity<List<AssignedEventDto>> getAssignedEventsByRoom(
                                        @RequestParam(name = "abbr") String abbr) {
        List<AssignedEventDto> assignedEventDtos = new ArrayList<>();
        try {
            assignedEventDtos = assignedEventService.getAssignedEventsByRoom(abbr);
        } catch (IOException e) {
            throw new UnableToReadTimetableFileException("Unable to read timetable file");
        }
        return new ResponseEntity<>(assignedEventDtos, null, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AssignedEventDto> addNewAssignment(
                                        @RequestBody AssignedEventDto assignedEventDto) {
        AssignedEventDto createdEvent = assignedEventService.addNewAssignment(assignedEventDto);
        return new ResponseEntity<>(createdEvent, null, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<AssignedEventDto> updateAssignment(
                                        @RequestBody AssignedEventDto assignedEventDto) {
        AssignedEventDto updatedEvent = assignedEventService.updateAssignment(assignedEventDto);
        return new ResponseEntity<>(updatedEvent, null, HttpStatus.OK);
    }

    @PostMapping("/id")
    public ResponseEntity<Long> getAssignmentId(@RequestBody AssignedEventDto assignedEventDto) {
        Long assignmentId = assignedEventService.getAssignmentId(assignedEventDto);
        return new ResponseEntity<>(assignmentId, null, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable(name = "id") Long id) {
        assignedEventService.deleteAssignment(id);
        return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadTimetableConfiguration() {
        return new ResponseEntity<>(assignedEventService.downloadTimetableConfiguration(), null, HttpStatus.OK);
    }
}

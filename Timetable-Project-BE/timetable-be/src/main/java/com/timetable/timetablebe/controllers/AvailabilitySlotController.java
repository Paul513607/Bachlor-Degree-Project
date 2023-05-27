package com.timetable.timetablebe.controllers;

import com.timetable.timetablebe.dtos.AvailabilitySlotDto;
import com.timetable.timetablebe.dtos.EventDto;
import com.timetable.timetablebe.services.AvailabilitySlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability-slots")
public class AvailabilitySlotController {
    @Autowired
    private AvailabilitySlotService availabilitySlotService;

    @PostMapping
    public ResponseEntity<List<AvailabilitySlotDto>> getAvailableSlots(@RequestBody EventDto eventDto) {
        List<AvailabilitySlotDto> availableSlots =
                availabilitySlotService.getAvailableSlots(eventDto);
        return new ResponseEntity<>(availableSlots, null, HttpStatus.OK);
    }
}

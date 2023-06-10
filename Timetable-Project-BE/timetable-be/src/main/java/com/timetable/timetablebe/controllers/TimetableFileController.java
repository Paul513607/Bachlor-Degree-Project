package com.timetable.timetablebe.controllers;

import com.timetable.timetablebe.dtos.TimetableFileDto;
import com.timetable.timetablebe.entities.TimetableFileEntity;
import com.timetable.timetablebe.services.TimetableFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/timetable-files")
public class TimetableFileController {
    @Autowired
    private TimetableFileService timetableFileService;

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllTimetableFileNames() {
        return new ResponseEntity<>(timetableFileService.getAllTimetableFileNames(), null, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TimetableFileDto> saveTimetableFile(@RequestParam(name = "file") MultipartFile file) {
        try {
            return new ResponseEntity<>(timetableFileService.saveTimetableFile(file), null, HttpStatus.CREATED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public ResponseEntity<Long> getTimetableFileIdByName(@RequestParam(name = "name") String name) {
        return new ResponseEntity<>(timetableFileService.getTimetableFileIdByName(name), null, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimetableFile(@PathVariable(name = "id") Long id) {
        timetableFileService.deleteTimetableFile(id);
        return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
    }

    @PostMapping("/set")
    public ResponseEntity<TimetableFileDto> setTimetableFile(@RequestParam(name = "name") String name) {
        return new ResponseEntity<>(timetableFileService.setTimetableFile(name), null, HttpStatus.OK);
    }
}

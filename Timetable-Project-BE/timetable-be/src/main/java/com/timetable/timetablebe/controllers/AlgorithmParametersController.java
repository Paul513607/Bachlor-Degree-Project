package com.timetable.timetablebe.controllers;

import com.timetable.timetablebe.dtos.AlgorithmParametersDto;
import com.timetable.timetablebe.services.AlgorithmParametersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/algorithm-parameters")
public class AlgorithmParametersController {
    @Autowired
    private AlgorithmParametersService algorithmParametersService;

    @GetMapping
    public ResponseEntity<AlgorithmParametersDto> getAlgorithmParameters() {
        return new ResponseEntity<>(algorithmParametersService.getAlgorithmParameters(), null, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<AlgorithmParametersDto> setAlgorithmParameters(
                                                    @RequestBody AlgorithmParametersDto algorithmParametersDto) {
        return new ResponseEntity<>(algorithmParametersService.setAlgorithmParameters(algorithmParametersDto), null, HttpStatus.OK);
    }
}

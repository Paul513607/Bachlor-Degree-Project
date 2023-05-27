package com.timetable.timetablebe.advice;

import com.timetable.timetablebe.exceptions.EventNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = EventNotFoundException.class)
    protected ResponseEntity<String> handleEventNotFoundException(EventNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), null, HttpStatus.NOT_FOUND);
    }
}

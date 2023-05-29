package com.timetable.timetablebe.advice;

import com.timetable.timetablebe.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = AssignedEventNotFoundException.class)
    protected ResponseEntity<String> handleAssignedEventNotFoundException(AssignedEventNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = EventNotFoundException.class)
    protected ResponseEntity<String> handleEventNotFoundException(EventNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    protected ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UnableToReadTimetableFileException.class)
    protected ResponseEntity<String> handleUnableToReadTimetableFileException(UnableToReadTimetableFileException e) {
        return new ResponseEntity<>(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = TimetableFileNotFoundException.class)
    protected ResponseEntity<String> handleTimetableFileNotFoundException(TimetableFileNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RuntimeException.class)
    protected ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package com.timetable.timetablebe.exceptions;

public class AssignedEventNotFoundException extends RuntimeException {
    public AssignedEventNotFoundException(String message) {
        super(message);
    }
}

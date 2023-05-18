package com.timetable.timetablebe;

import com.timetable.timetablebe.dtos.AssignedEventDto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.List;

@SpringBootApplication
public class TimetableBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimetableBeApplication.class, args);
	}

	public List<AssignedEventDto> getAssignedEvents() {
		return null;
	}
}

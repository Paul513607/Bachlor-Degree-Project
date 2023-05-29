package com.timetable.timetablebe.dtos;

import com.timetable.timetablebe.dtos.ResourceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilitySlotDto {
    private boolean isAvailable;
    private List<ResourceDto> rooms;
    private Integer day;
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime time;

    public void addRoom(ResourceDto room) {
        this.rooms.add(room);
    }
}

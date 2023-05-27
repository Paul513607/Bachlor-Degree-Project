package com.timetable.timetablebe.dtos;

import com.timetable.timetablebe.dtos.ResourceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilitySlotDto {
    private List<ResourceDto> rooms;
    private Integer day;
    private LocalTime time;

    public void addRoom(ResourceDto room) {
        this.rooms.add(room);
    }
}

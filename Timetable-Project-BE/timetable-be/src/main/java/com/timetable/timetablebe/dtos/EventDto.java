package com.timetable.timetablebe.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private String abbr;
    private String actors;
    private Integer duration;
    private Integer frequency;
    private String eventGroup;
    private String name;
    private String notes;
    private String type;

    private List<String> studentGroupAbbrs;
    private List<String> profAbbrs;
}

package org.timetable.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    @JacksonXmlProperty(isAttribute = true)
    private Integer day;
    @JacksonXmlProperty(isAttribute = true)
    private Integer start;
    @JacksonXmlProperty(isAttribute = true)
    private Integer end;
    @JacksonXmlProperty(isAttribute = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    @JacksonXmlProperty(isAttribute = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;
    @JacksonXmlProperty(isAttribute = true)
    private String event;
    @JacksonXmlProperty(isAttribute = true)
    private String notes;
    @JacksonXmlProperty(isAttribute = true)
    private String resources;
    @JacksonXmlProperty(isAttribute = true)
    private Integer week;

    private Event eventObject;
    private List<Resource> resourceList = new ArrayList<>();

    public Assignment(Integer day, Integer start, Integer end, String event,
                      String notes, String resources, Integer week) {
        this.day = day;
        this.start = start;
        this.end = end;
        this.event = event;
        this.notes = notes;
        this.resources = resources;
        this.week = week;
    }
}

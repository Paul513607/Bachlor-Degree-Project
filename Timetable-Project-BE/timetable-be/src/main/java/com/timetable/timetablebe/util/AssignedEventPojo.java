package com.timetable.timetablebe.util;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "assignment")
public class AssignedEventPojo {
    @JacksonXmlProperty(isAttribute = true, localName = "day")
    private int day;
    @JacksonXmlProperty(isAttribute = true, localName = "startTime")
    private String startTime;
    @JacksonXmlProperty(isAttribute = true, localName = "endTime")
    private String endTime;
    @JacksonXmlProperty(isAttribute = true, localName = "event")
    private String eventAbbr;
    @JacksonXmlProperty(isAttribute = true, localName = "notes")
    private String notes;
    @JacksonXmlProperty(isAttribute = true, localName = "resources")
    private String resource;
    @JacksonXmlProperty(isAttribute = true, localName = "start")
    private int start;
    @JacksonXmlProperty(isAttribute = true, localName = "end")
    private int end;
    @JacksonXmlProperty(isAttribute = true, localName = "week")
    private int week;
}

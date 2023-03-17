package org.timetable.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

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
}

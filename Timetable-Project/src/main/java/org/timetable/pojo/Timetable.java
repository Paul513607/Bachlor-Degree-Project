package org.timetable.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timetable {
    @JacksonXmlProperty(isAttribute = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate beginDate;
    @JacksonXmlProperty(isAttribute = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate endDate;
    @JacksonXmlProperty(isAttribute = true)
    private String homePage;
    @JacksonXmlProperty(isAttribute = true)
    private Integer hourLength;
    @JacksonXmlProperty(isAttribute = true)
    private Integer hoursPerDay;
    @JacksonXmlProperty(isAttribute = true)
    private String implementers;
    @JacksonXmlProperty(isAttribute = true)
    private String name;
    @JacksonXmlProperty(isAttribute = true)
    private String notes;
    @JacksonXmlProperty(isAttribute = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    @JacksonXmlProperty(isAttribute = true)
    private String title;

    @JacksonXmlElementWrapper(localName = "profs")
    private Profs profs;
    @JacksonXmlElementWrapper(localName = "students")
    private Students students;
    @JacksonXmlElementWrapper(localName = "resourceTypes")
    private ResourceTypes resourceTypes;
    @JacksonXmlElementWrapper(localName = "resources")
    private Resources resources;
    @JacksonXmlElementWrapper(localName = "eventTypes")
    private EventTypes eventTypes;
    @JacksonXmlElementWrapper(localName = "eventGroups")
    private EventGroups eventGroups;
    @JacksonXmlElementWrapper(localName = "events")
    private Events events;
    @JacksonXmlElementWrapper(localName = "assignments")
    private Assignments assignments;
    @JacksonXmlElementWrapper(localName = "announcements")
    private Announcements announcements;
}

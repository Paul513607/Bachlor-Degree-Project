package org.timetable.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    @JacksonXmlProperty(isAttribute = true)
    private String actor;
    @JacksonXmlProperty(isAttribute = true)
    private String event;
    @JacksonXmlProperty(isAttribute = true)
    private String eventGroup;
    @JacksonXmlProperty(isAttribute = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate expireDate;
    @JacksonXmlProperty(isAttribute = true)
    private String header;
    @JacksonXmlProperty(isAttribute = true)
    private String message;
    @JacksonXmlProperty(isAttribute = true)
    private String resource;
    @JacksonXmlProperty(isAttribute = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime timestamp;
}

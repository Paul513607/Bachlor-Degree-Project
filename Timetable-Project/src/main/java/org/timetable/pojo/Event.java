package org.timetable.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @JacksonXmlProperty(isAttribute = true)
    private String abbr;
    @JacksonXmlProperty(isAttribute = true)
    private String actors;
    @JacksonXmlProperty(isAttribute = true)
    private Integer duration;
    @JacksonXmlProperty(isAttribute = true)
    private Integer frequency;
    @JacksonXmlProperty(isAttribute = true)
    private String group;
    @JacksonXmlProperty(isAttribute = true)
    private String name;
    @JacksonXmlProperty(isAttribute = true)
    private String notes;
    @JacksonXmlProperty(isAttribute = true)
    private String type;
}

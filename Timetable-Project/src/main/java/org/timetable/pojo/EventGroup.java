package org.timetable.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventGroup {
    @JacksonXmlProperty(isAttribute = true)
    private String abbr;
    @JacksonXmlProperty(isAttribute = true)
    private String actors;
    @JacksonXmlProperty(isAttribute = true)
    private String code;
    @JacksonXmlProperty(isAttribute = true)
    private String homePage;
    @JacksonXmlProperty(isAttribute = true)
    private String name;
    @JacksonXmlProperty(isAttribute = true)
    private String notes;
    @JacksonXmlProperty(isAttribute = true)
    private String structure;
    @JacksonXmlProperty(isAttribute = true)
    private Integer pack;
}

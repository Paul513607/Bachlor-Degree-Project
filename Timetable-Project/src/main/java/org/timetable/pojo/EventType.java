package org.timetable.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventType {
    @JacksonXmlProperty(isAttribute = true)
    private String abbr;
    @JacksonXmlProperty(isAttribute = true)
    private String name;
}

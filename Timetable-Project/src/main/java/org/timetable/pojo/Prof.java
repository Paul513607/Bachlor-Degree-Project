package org.timetable.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prof {
    @JacksonXmlProperty(isAttribute = true)
    private String abbr;
    @JacksonXmlProperty(isAttribute = true)
    private String email;
    @JacksonXmlProperty(isAttribute = true)
    private String name;
    @JacksonXmlProperty(isAttribute = true)
    private String notes;
    @JacksonXmlProperty(isAttribute = true)
    private String parent;
    @JacksonXmlProperty(isAttribute = true)
    private String prefix;
}

package org.timetable.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventGroups {
    @JacksonXmlElementWrapper(localName = "eventGroups", useWrapping = false)
    @JacksonXmlProperty(localName = "eventGroup")
    private List<EventGroup> eventGroups;
}

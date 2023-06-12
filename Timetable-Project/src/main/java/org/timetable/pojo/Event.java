package org.timetable.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    private List<Group> groupList = new ArrayList<>();
    private List<Prof> profList = new ArrayList<>();
    private List<Group> groupListNoParents = new ArrayList<>();
    private EventType eventTypeObject;

    public Event(String abbr, String actors, Integer duration, Integer frequency,
                 String group, String name, String notes, String type) {
        this.abbr = abbr;
        this.actors = actors;
        this.duration = duration;
        this.frequency = frequency;
        this.group = group;
        this.name = name;
        this.notes = notes;
        this.type = type;
    }

    public Event(String abbr) {
        this.abbr = abbr;
    }


    public void setGroupList(Set<Group> groupSet) {
        groupList = new ArrayList<>(groupSet);
    }

    public void setProfList(Set<Prof> profSet) {
        profList = new ArrayList<>(profSet);
    }

    public void setGroupListNoParents(Set<Group> groupSet) {
        groupListNoParents = new ArrayList<>(groupSet);
    }
}

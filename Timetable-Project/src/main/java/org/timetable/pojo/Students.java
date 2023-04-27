package org.timetable.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Students {
    @JacksonXmlElementWrapper(localName = "students", useWrapping = false)
    @JacksonXmlProperty(localName = "group")
    private List<Group> groups;

    public Set<String> getGroupsAbbr() {
        return groups.stream()
                .map(Group::getAbbr)
                .collect(Collectors.toSet());
    }
}

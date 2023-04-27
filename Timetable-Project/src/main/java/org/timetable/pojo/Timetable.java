package org.timetable.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<Prof> getProfs() {
        return profs.getProfs();
    }

    public List<Group> getGroups() {
        return students.getGroups();
    }

    public List<Type> getResourceTypes() {
        return resourceTypes.getTypes();
    }

    public List<Resource> getResources() {
        return resources.getResources();
    }

    public List<EventType> getEventTypes() {
        return eventTypes.getEventTypes();
    }

    public List<EventGroup> getEventGroups() {
        return eventGroups.getEventGroups();
    }

    public List<Event> getEvents() {
        return events.getEvents();
    }

    public List<Assignment> getAssignments() {
        return assignments.getAssignments();
    }

    public List<Announcement> getAnnouncements() {
        return announcements.getAnnouncements();
    }

    public void setLinks() {
        for (Assignment assignment : assignments.getAssignments()) {
            assignment.setEventObject(events.getEvents().stream()
                    .filter(event -> event.getAbbr().equals(assignment.getEvent())).findFirst().orElse(null));
            List<String> assignmentResources = List.of(assignment.getResources().split("\s*,\s*"));
            for (String resource : assignmentResources) {
                assignment.getResourceList().add(resources.getResources().stream()
                        .filter(resource1 -> resource1.getAbbr().equals(resource)).findFirst().orElse(null));
            }
        }

        for (Event event : events.getEvents()) {
            List<String> eventActors = List.of(event.getActors().split("\s*,\s*"));
            Set<Group> tmpGroups = new HashSet<>();
            Set<Prof> tmpProfs = new HashSet<>();
            for (String actor : eventActors) {
                boolean isGroup = this.students.getGroups().stream()
                        .anyMatch(group -> group.getAbbr().equals(actor));
                if (isGroup) {
                    tmpGroups.add(this.students.getGroups().stream()
                            .filter(group -> group.getAbbr().equals(actor)).findFirst().orElse(null));
                } else {
                    tmpProfs.add(this.profs.getProfs().stream()
                            .filter(prof -> prof.getAbbr().equals(actor)).findFirst().orElse(null));
                }
            }

            boolean foundChildGroups;
            Set<Group> parentGroupSet = new HashSet<>(tmpGroups);
            do {
                foundChildGroups = false;
                Set<String> parentGroupAbbrSet = parentGroupSet.stream()
                        .map(Group::getAbbr)
                        .collect(Collectors.toSet());
                Set<Group> childGroups = new HashSet<>();
                for (Group group : getGroups()) {
                    if (parentGroupAbbrSet.contains(group.getParent())) {
                        childGroups.add(group);
                        foundChildGroups = true;
                    }
                }
                tmpGroups.addAll(childGroups);
                if (!childGroups.isEmpty()) {
                    parentGroupSet = new HashSet<>(childGroups);
                }
            } while (foundChildGroups);

            if (parentGroupSet.isEmpty()) {
                event.setGroupListNoParents(tmpGroups);
            } else {
                event.setGroupListNoParents(parentGroupSet);
            }

            event.setGroupList(tmpGroups);
            event.setProfList(tmpProfs);

            event.setEventTypeObject(eventTypes.getEventTypes().stream()
                    .filter(eventType -> eventType.getAbbr().equals(event.getType())).findFirst().orElse(null));
        }
    }
}

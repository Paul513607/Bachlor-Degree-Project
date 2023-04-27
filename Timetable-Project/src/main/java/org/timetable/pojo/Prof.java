package org.timetable.pojo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prof prof = (Prof) o;
        return Objects.equals(abbr, prof.abbr) && Objects.equals(email, prof.email) && Objects.equals(name, prof.name) && Objects.equals(notes, prof.notes) && Objects.equals(parent, prof.parent) && Objects.equals(prefix, prof.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(abbr, email, name, notes, parent, prefix);
    }
}

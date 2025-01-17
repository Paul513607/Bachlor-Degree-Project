package org.timetable.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.timetable.pojo.Timetable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Getter
@Setter
@NoArgsConstructor
public class Parser {
    private String fileToParsePath;
    private Timetable timetable;

    public Parser(String fileToParsePath) {
        this.fileToParsePath = fileToParsePath;
    }

    public void parse() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        File file = new File(this.fileToParsePath);
        this.timetable = xmlMapper.readValue(file, Timetable.class);
    }

    public void parse(InputStream inputStream) {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        try {
            this.timetable = xmlMapper.readValue(inputStream, Timetable.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLinksForTimetable() {
        this.timetable.setLinks();
    }
}

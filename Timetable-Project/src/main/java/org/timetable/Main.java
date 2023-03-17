package org.timetable;

import org.timetable.util.Parser;

import java.io.IOException;

public class Main {
    private static final String XML_FILEPATH = "src/main/resources/export_2022-2023_semestrul_1.xml";

    public static void main(String[] args) {
        Parser parser = new Parser(XML_FILEPATH);
        try {
            parser.parse();
        } catch (IOException e) {
            System.out.println("Error while parsing the XML file.");
            e.printStackTrace();
        }
    }
}
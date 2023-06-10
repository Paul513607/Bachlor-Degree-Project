package com.timetable.timetablebe.components;

import java.io.IOException;

public interface ApplicationStartupBase {
    void initializeDatabase(boolean useDefaultFile) throws IOException;
}

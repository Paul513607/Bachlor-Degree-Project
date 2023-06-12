package org.timetable.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class Timer {
    private static Timer instance;
    private long startTime;
    private long endTime;

    private Timer() {
    }

    public static Timer getInstance() {
        if (instance == null) {
            instance = new Timer();
        }
        return instance;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void end() {
        endTime = System.currentTimeMillis();
    }

    public long getDuration() {
        return endTime - startTime;
    }

    public long getDurationInSeconds() {
        return getDuration() / 1000;
    }
}

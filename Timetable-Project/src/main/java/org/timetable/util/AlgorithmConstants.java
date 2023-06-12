package org.timetable.util;

import java.time.LocalTime;

public final class AlgorithmConstants {
    public static int NUMBER_OF_DAYS = 5;
    public static LocalTime START_TIME = LocalTime.of(8, 0);
    public static LocalTime END_TIME = LocalTime.of(20, 0);
    public static int GENERAL_DURATION = 2;

    private AlgorithmConstants() {
    }
}

package org.timetable.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair<T, V> {
    private T first;
    private V second;

    public static <T, V> Pair<T, V> of(T first, V second) {
        return new Pair<>(first, second);
    }
}

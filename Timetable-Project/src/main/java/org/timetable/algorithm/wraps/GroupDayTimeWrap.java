package org.timetable.algorithm.wraps;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.pojo.Group;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDayTimeWrap {
    private List<Group> groupList;
    private int day;
    private int time;
}

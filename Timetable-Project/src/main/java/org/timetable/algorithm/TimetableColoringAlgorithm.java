package org.timetable.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.model.TimetableEdge;
import org.timetable.model.TimetableGraph;
import org.timetable.model.TimetableNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class TimetableColoringAlgorithm {
    protected TimetableGraph<TimetableNode, TimetableEdge> graph;
    protected Map<TimetableNode, ColorDayTimeWrap> nodeColorMap = new HashMap<>();

    public TimetableColoringAlgorithm(TimetableGraph<TimetableNode, TimetableEdge> graph) {
        this.graph = graph;
    }

    protected void sortTimetableNodesByEdgeCount() {
        graph.getNodes().sort((o1, o2) -> {
            int o1EdgeCount = 0;
            int o2EdgeCount = 0;
            for (TimetableEdge edge : graph.getEdges()) {
                if (edge.getNode1().equals(o1) || edge.getNode2().equals(o1)) {
                    o1EdgeCount++;
                }
                if (edge.getNode1().equals(o2) || edge.getNode2().equals(o2)) {
                    o2EdgeCount++;
                }
            }
            return o2EdgeCount - o1EdgeCount;
        });
    }

    public abstract void colorGraph();

    public void sortNodeColorMapByActorsAndDayTimeAndPrint() {
        List<Map.Entry<TimetableNode, ColorDayTimeWrap>> list = new ArrayList<>(nodeColorMap.entrySet());
        list.sort((o1, o2) -> {
            String o1FirstGroup = o1.getKey().getEvent().getGroupList().get(0).getAbbr();
            String o2FirstGroup = o2.getKey().getEvent().getGroupList().get(0).getAbbr();
            int compare = o1FirstGroup.compareTo(o2FirstGroup);
            if (compare != 0) {
                return compare;
            }
            int o1Day = o1.getValue().getDay();
            int o2Day = o2.getValue().getDay();
            compare = Integer.compare(o1Day, o2Day);
            if (compare != 0) {
                return compare;
            }
            int o1Time = o1.getValue().getTime();
            int o2Time = o2.getValue().getTime();
            return Integer.compare(o1Time, o2Time);
        });
        for (Map.Entry<TimetableNode, ColorDayTimeWrap> entry : list) {
            System.out.println(entry.getKey().getEvent() + "\n" + entry.getValue() + "\n");
        }
    }

    public void printNodeColorMap() {
        for (Map.Entry<TimetableNode, ColorDayTimeWrap> entry : nodeColorMap.entrySet()) {
            System.out.println(entry.getKey().getEvent() + "\n" + entry.getValue() + "\n");
        }
    }
}

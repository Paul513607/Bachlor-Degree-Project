package org.timetable.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.timetable.algorithm.wraps.ColorDayTimeWrap;
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
    public Map<TimetableNode, ColorDayTimeWrap> nodeColorMap;

    public TimetableColoringAlgorithm(TimetableGraph<TimetableNode, TimetableEdge> graph) {
        this.graph = graph;
        this.nodeColorMap = new HashMap<>();
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
}

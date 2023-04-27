package org.timetable.algorithm.wraps;

import java.util.Comparator;

public class NodeSaturationComparator implements Comparator<TimetableNodeSatur> {
    private static NodeSaturationComparator instance = null;

    private NodeSaturationComparator() {};

    public static NodeSaturationComparator getInstance() {
        if (instance == null) {
            instance = new NodeSaturationComparator();
        }
        return instance;
    }

    @Override
    public int compare(TimetableNodeSatur o1, TimetableNodeSatur o2) {
        int value = Integer.compare(o1.getSaturation(), o2.getSaturation());
        if (value != 0) {
            return value;
        }
        value = Integer.compare(o1.getDegree(), o2.getDegree());
        return value;
    }
}

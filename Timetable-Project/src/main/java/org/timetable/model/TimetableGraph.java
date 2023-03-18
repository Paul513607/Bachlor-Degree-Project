package org.timetable.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableGraph<V extends TimetableNode, E extends TimetableEdge> implements Graph<V, E> {
    public static final int NUMBER_OF_DAYS = 5;
    public static final LocalTime START_TIME = LocalTime.of(8, 0);
    public static final LocalTime END_TIME = LocalTime.of(20, 0);
    public static final int GENERAL_DURATION = 2;

    private List<TimetableNode> nodes = new ArrayList<>();
    private List<TimetableEdge> edges = new ArrayList<>();
    private List<TimetableColor> timetableColors = new ArrayList<>();

    @Override
    public Set<E> getAllEdges(V v, V v1) {
        return null;
    }

    @Override
    public E getEdge(V v, V v1) {
        return null;
    }

    @Override
    public Supplier<V> getVertexSupplier() {
        return null;
    }

    @Override
    public Supplier<E> getEdgeSupplier() {
        return null;
    }

    @Override
    public E addEdge(V v, V v1) {
        return null;
    }

    @Override
    public boolean addEdge(V v, V v1, E e) {
        return false;
    }

    @Override
    public V addVertex() {
        return null;
    }

    @Override
    public boolean addVertex(V v) {
        return false;
    }

    @Override
    public boolean containsEdge(V v, V v1) {
        return false;
    }

    @Override
    public boolean containsEdge(E e) {
        return false;
    }

    @Override
    public boolean containsVertex(V v) {
        return false;
    }

    @Override
    public Set<E> edgeSet() {
        return null;
    }

    @Override
    public int degreeOf(V v) {
        return 0;
    }

    @Override
    public Set<E> edgesOf(V v) {
        return null;
    }

    @Override
    public int inDegreeOf(V v) {
        return 0;
    }

    @Override
    public Set<E> incomingEdgesOf(V v) {
        return null;
    }

    @Override
    public int outDegreeOf(V v) {
        return 0;
    }

    @Override
    public Set<E> outgoingEdgesOf(V v) {
        return null;
    }

    @Override
    public boolean removeAllEdges(Collection<? extends E> collection) {
        return false;
    }

    @Override
    public Set<E> removeAllEdges(V v, V v1) {
        return null;
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> collection) {
        return false;
    }

    @Override
    public E removeEdge(V v, V v1) {
        return null;
    }

    @Override
    public boolean removeEdge(E e) {
        return false;
    }

    @Override
    public boolean removeVertex(V v) {
        return false;
    }

    @Override
    public Set<V> vertexSet() {
        return null;
    }

    @Override
    public V getEdgeSource(E e) {
        return null;
    }

    @Override
    public V getEdgeTarget(E e) {
        return null;
    }

    @Override
    public GraphType getType() {
        return null;
    }

    @Override
    public double getEdgeWeight(E e) {
        return 0;
    }

    @Override
    public void setEdgeWeight(E e, double v) {

    }
}

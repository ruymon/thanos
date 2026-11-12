package thanos.engine.graph;

import java.util.Arrays;

/**
 * A directed graph whose edges carry capacities.
 *
 * <p>Stored as a forward-star adjacency list on flat arrays (see ADR 0002). Every edge is added
 * together with its reverse, so the reverse of edge {@code e} is always {@code e ^ 1}. Solving a
 * maximum flow consumes the network: capacities become residual capacities.
 */
public final class FlowNetwork {

    static final int NO_EDGE = -1;

    private static final int INITIAL_EDGE_SLOTS = 16;
    private static final int GROWTH_FACTOR = 2;

    private final int vertexCount;
    private final int[] firstEdge;

    private int[] nextEdge;
    private int[] target;
    private int[] capacity;
    private int edgeSlots;

    public FlowNetwork(int vertexCount) {
        this(vertexCount, INITIAL_EDGE_SLOTS);
    }

    /** @param expectedEdges how many edges will be added, to allocate the arrays once */
    public FlowNetwork(int vertexCount, int expectedEdges) {
        if (vertexCount <= 0) {
            throw new IllegalArgumentException("A flow network needs at least one vertex");
        }

        final int slots = Math.max(INITIAL_EDGE_SLOTS, expectedEdges * 2);

        this.vertexCount = vertexCount;
        this.firstEdge = new int[vertexCount];
        this.nextEdge = new int[slots];
        this.target = new int[slots];
        this.capacity = new int[slots];

        Arrays.fill(firstEdge, NO_EDGE);
    }

    public void addEdge(int from, int to, int capacity) {
        addEdgePair(from, to, capacity, 0);
    }

    public void addUndirectedEdge(int a, int b, int capacity) {
        addEdgePair(a, b, capacity, capacity);
    }

    public int vertexCount() {
        return vertexCount;
    }

    /** Number of edges added, counting an undirected edge once. */
    public int edgeCount() {
        return edgeSlots / 2;
    }

    int firstEdgeOf(int vertex) {
        return firstEdge[vertex];
    }

    int nextEdgeAfter(int edge) {
        return nextEdge[edge];
    }

    int targetOf(int edge) {
        return target[edge];
    }

    int residualOf(int edge) {
        return capacity[edge];
    }

    void push(int edge, int amount) {
        capacity[edge] -= amount;
        capacity[edge ^ 1] += amount;
    }

    private void addEdgePair(int from, int to, int forwardCapacity, int backwardCapacity) {
        assertVertex(from);
        assertVertex(to);
        assertCapacity(forwardCapacity);
        assertCapacity(backwardCapacity);
        assertRoomForPair();

        addHalfEdge(from, to, forwardCapacity);
        addHalfEdge(to, from, backwardCapacity);
    }

    private void addHalfEdge(int from, int to, int halfCapacity) {
        target[edgeSlots] = to;
        capacity[edgeSlots] = halfCapacity;
        nextEdge[edgeSlots] = firstEdge[from];
        firstEdge[from] = edgeSlots;
        edgeSlots++;
    }

    private void assertRoomForPair() {
        if (edgeSlots + 2 <= target.length) {
            return;
        }

        final int grown = target.length * GROWTH_FACTOR;
        nextEdge = Arrays.copyOf(nextEdge, grown);
        target = Arrays.copyOf(target, grown);
        capacity = Arrays.copyOf(capacity, grown);
    }

    private void assertVertex(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException("Vertex " + vertex + " is outside 0.." + (vertexCount - 1));
        }
    }

    private static void assertCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must not be negative: " + capacity);
        }
    }
}

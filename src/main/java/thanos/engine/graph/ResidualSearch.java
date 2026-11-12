package thanos.engine.graph;

import java.util.Arrays;

/** Breadth-first search over the edges of a flow network that still have free capacity. */
final class ResidualSearch {

    static final int UNREACHED = -1;

    private final FlowNetwork network;
    private final int[] queue;

    ResidualSearch(FlowNetwork network) {
        this.network = network;
        this.queue = new int[network.vertexCount()];
    }

    int[] findLevels(int source) {
        final int[] level = new int[network.vertexCount()];
        fillLevels(source, level);

        return level;
    }

    void fillLevels(int source, int[] level) {
        Arrays.fill(level, UNREACHED);

        int head = 0;
        int tail = 0;
        level[source] = 0;
        queue[tail++] = source;

        while (head < tail) {
            final int vertex = queue[head++];

            for (int edge = network.firstEdgeOf(vertex); edge != FlowNetwork.NO_EDGE; edge = network.nextEdgeAfter(edge)) {
                final int neighbour = network.targetOf(edge);

                if (network.residualOf(edge) > 0 && level[neighbour] == UNREACHED) {
                    level[neighbour] = level[vertex] + 1;
                    queue[tail++] = neighbour;
                }
            }
        }
    }

    /** The minimum cut once no more flow fits: vertices still reachable from the source are its side. */
    MinCut findMinCut(int source, long flow) {
        final int[] level = findLevels(source);
        final boolean[] onSourceSide = new boolean[level.length];

        for (int vertex = 0; vertex < level.length; vertex++) {
            onSourceSide[vertex] = level[vertex] != UNREACHED;
        }

        return new MinCut(flow, onSourceSide);
    }
}

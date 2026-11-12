package thanos.graph;

import java.util.Arrays;

/**
 * Edmonds-Karp, {@code O(V·E²)}: the textbook baseline.
 *
 * <p>Finds one shortest augmenting path per breadth-first search and saturates it. Simple and
 * slow, kept to cross-check {@link DinicMaxFlow} and to time against it.
 */
public final class EdmondsKarpMaxFlow implements MaxFlowSolver {

    private static final int NO_PARENT = -1;

    @Override
    public MinCut solve(FlowNetwork network, int source, int sink) {
        final ResidualSearch search = new ResidualSearch(network);

        if (source == sink) {
            return search.findMinCut(source, 0);
        }

        final int[] parentEdge = new int[network.vertexCount()];
        final int[] queue = new int[network.vertexCount()];

        long flow = 0;

        while (findShortestPath(network, source, sink, parentEdge, queue)) {
            flow += augment(network, source, sink, parentEdge);
        }

        return search.findMinCut(source, flow);
    }

    private static boolean findShortestPath(
            FlowNetwork network, int source, int sink, int[] parentEdge, int[] queue) {
        Arrays.fill(parentEdge, NO_PARENT);

        int head = 0;
        int tail = 0;
        queue[tail++] = source;

        while (head < tail) {
            final int vertex = queue[head++];

            for (int edge = network.firstEdgeOf(vertex); edge != FlowNetwork.NO_EDGE; edge = network.nextEdgeAfter(edge)) {
                final int neighbour = network.targetOf(edge);
                final boolean isNew = neighbour != source && parentEdge[neighbour] == NO_PARENT;

                if (network.residualOf(edge) > 0 && isNew) {
                    parentEdge[neighbour] = edge;

                    if (neighbour == sink) {
                        return true;
                    }

                    queue[tail++] = neighbour;
                }
            }
        }

        return false;
    }

    private static int augment(FlowNetwork network, int source, int sink, int[] parentEdge) {
        int bottleneck = Integer.MAX_VALUE;

        for (int vertex = sink; vertex != source; vertex = network.targetOf(parentEdge[vertex] ^ 1)) {
            bottleneck = Math.min(bottleneck, network.residualOf(parentEdge[vertex]));
        }

        for (int vertex = sink; vertex != source; vertex = network.targetOf(parentEdge[vertex] ^ 1)) {
            network.push(parentEdge[vertex], bottleneck);
        }

        return bottleneck;
    }
}

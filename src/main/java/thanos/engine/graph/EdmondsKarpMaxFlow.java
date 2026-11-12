package thanos.engine.graph;

import java.util.Arrays;

/**
 * Edmonds-Karp, {@code O(V·E²)}: the textbook baseline.
 *
 * <p>Finds one shortest augmenting path per breadth-first search and saturates it. Simple and slow,
 * kept to cross-check {@link DinicMaxFlow} and to time against it.
 */
public final class EdmondsKarpMaxFlow implements MaxFlowSolver {

    private static final int NO_PARENT = -1;

    @Override
    public MinCut solve(FlowNetwork network, int source, int sink) {
        final long flow = source == sink ? 0 : new Run(network, source, sink).maxFlow();

        return new ResidualSearch(network).findMinCut(source, flow);
    }

    /** The working state of one solve, so the solver itself stays stateless. */
    private static final class Run {

        private final FlowNetwork network;
        private final int source;
        private final int sink;

        /** For each vertex, the edge the search arrived through. */
        private final int[] parentEdge;

        private final int[] queue;

        Run(FlowNetwork network, int source, int sink) {
            this.network = network;
            this.source = source;
            this.sink = sink;
            this.parentEdge = new int[network.vertexCount()];
            this.queue = new int[network.vertexCount()];
        }

        long maxFlow() {
            long flow = 0;

            while (findShortestPath()) {
                flow += augment();
            }

            return flow;
        }

        private boolean findShortestPath() {
            Arrays.fill(parentEdge, NO_PARENT);

            int head = 0;
            int tail = 0;
            queue[tail++] = source;

            while (head < tail) {
                final int vertex = queue[head++];
                int edge = network.firstEdgeOf(vertex);

                while (edge != FlowNetwork.NO_EDGE) {
                    final int neighbour = network.targetOf(edge);

                    if (network.residualOf(edge) > 0 && isUnvisited(neighbour)) {
                        parentEdge[neighbour] = edge;

                        if (neighbour == sink) {
                            return true;
                        }

                        queue[tail++] = neighbour;
                    }

                    edge = network.nextEdgeAfter(edge);
                }
            }

            return false;
        }

        private boolean isUnvisited(int vertex) {
            return vertex != source && parentEdge[vertex] == NO_PARENT;
        }

        /** Walks the path back from the sink twice: to find its narrowest edge, then to fill it. */
        private int augment() {
            int bottleneck = Integer.MAX_VALUE;

            for (int vertex = sink; vertex != source; vertex = previousOf(vertex)) {
                bottleneck = Math.min(bottleneck, network.residualOf(parentEdge[vertex]));
            }

            for (int vertex = sink; vertex != source; vertex = previousOf(vertex)) {
                network.push(parentEdge[vertex], bottleneck);
            }

            return bottleneck;
        }

        private int previousOf(int vertex) {
            return network.sourceOf(parentEdge[vertex]);
        }
    }
}

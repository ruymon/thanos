package thanos.engine.graph;

/**
 * Dinic's algorithm, {@code O(V²·E)}.
 *
 * <p>Repeats two phases until the sink is out of reach: a breadth-first search labels every vertex
 * with its distance from the source, then a depth-first search pushes flow only along edges that go
 * one level deeper, until no such path is left (a blocking flow).
 *
 * <p>The depth-first search is iterative: on a pixel grid a path can be thousands of edges long,
 * which would overflow the call stack.
 */
public final class DinicMaxFlow implements MaxFlowSolver {

    @Override
    public MinCut solve(FlowNetwork network, int source, int sink) {
        final ResidualSearch search = new ResidualSearch(network);
        final long flow = source == sink ? 0 : new Run(network, search, source, sink).maxFlow();

        return search.findMinCut(source, flow);
    }

    /** The working state of one solve, so the solver itself stays stateless. */
    private static final class Run {

        private final FlowNetwork network;
        private final ResidualSearch search;
        private final int source;
        private final int sink;

        private final int[] level;
        private final int[] currentEdge;
        private final int[] path;

        Run(FlowNetwork network, ResidualSearch search, int source, int sink) {
            this.network = network;
            this.search = search;
            this.source = source;
            this.sink = sink;
            this.level = new int[network.vertexCount()];
            this.currentEdge = new int[network.vertexCount()];
            this.path = new int[network.vertexCount()];
        }

        long maxFlow() {
            long flow = 0;

            while (hasPathToSink()) {
                resetCurrentEdges();
                flow += pushBlockingFlow();
            }

            return flow;
        }

        private boolean hasPathToSink() {
            search.fillLevels(source, level);

            return level[sink] != ResidualSearch.UNREACHED;
        }

        private void resetCurrentEdges() {
            for (int vertex = 0; vertex < currentEdge.length; vertex++) {
                currentEdge[vertex] = network.firstEdgeOf(vertex);
            }
        }

        private long pushBlockingFlow() {
            long pushed = 0;
            int depth = 0;
            int vertex = source;

            while (true) {
                if (vertex == sink) {
                    pushed += augment(depth);
                    depth = 0;
                    vertex = source;
                    continue;
                }

                final int edge = findAdmissibleEdge(vertex);

                if (edge != FlowNetwork.NO_EDGE) {
                    path[depth++] = edge;
                    vertex = network.targetOf(edge);
                    continue;
                }

                if (vertex == source) {
                    return pushed;
                }

                // Dead end: step back and never enter this vertex again in this phase.
                level[vertex] = ResidualSearch.UNREACHED;
                depth--;
                vertex = network.sourceOf(path[depth]);
            }
        }

        /**
         * Next edge out of {@code vertex} with free capacity that goes exactly one level deeper.
         */
        private int findAdmissibleEdge(int vertex) {
            int edge = currentEdge[vertex];

            while (edge != FlowNetwork.NO_EDGE) {
                final boolean hasCapacity = network.residualOf(edge) > 0;
                final boolean goesDeeper = level[network.targetOf(edge)] == level[vertex] + 1;

                if (hasCapacity && goesDeeper) {
                    break;
                }

                edge = network.nextEdgeAfter(edge);
            }

            currentEdge[vertex] = edge;

            return edge;
        }

        /** Saturates the narrowest edge of the first {@code length} edges of the path. */
        private int augment(int length) {
            int bottleneck = Integer.MAX_VALUE;

            for (int step = 0; step < length; step++) {
                bottleneck = Math.min(bottleneck, network.residualOf(path[step]));
            }

            for (int step = 0; step < length; step++) {
                network.push(path[step], bottleneck);
            }

            return bottleneck;
        }
    }
}

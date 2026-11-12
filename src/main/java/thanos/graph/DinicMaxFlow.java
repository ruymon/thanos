package thanos.graph;

/**
 * Dinic's algorithm, {@code O(V²·E)}.
 *
 * <p>Repeats two phases until the sink is out of reach: a breadth-first search labels every
 * vertex with its distance from the source, then a depth-first search pushes flow only along
 * edges that go one level deeper, until no such path is left (a blocking flow).
 *
 * <p>The depth-first search is iterative: on a pixel grid a path can be thousands of edges long,
 * which would overflow the call stack.
 */
public final class DinicMaxFlow implements MaxFlowSolver {

    @Override
    public MinCut solve(FlowNetwork network, int source, int sink) {
        final ResidualSearch search = new ResidualSearch(network);

        if (source == sink) {
            return search.findMinCut(source, 0);
        }

        final int[] level = new int[network.vertexCount()];
        final int[] currentEdge = new int[network.vertexCount()];
        final int[] path = new int[network.vertexCount()];

        long flow = 0;

        while (hasPathToSink(search, source, sink, level)) {
            resetCurrentEdges(network, currentEdge);
            flow += pushBlockingFlow(network, source, sink, level, currentEdge, path);
        }

        return search.findMinCut(source, flow);
    }

    private static boolean hasPathToSink(ResidualSearch search, int source, int sink, int[] level) {
        search.fillLevels(source, level);

        return level[sink] != ResidualSearch.UNREACHED;
    }

    private static void resetCurrentEdges(FlowNetwork network, int[] currentEdge) {
        for (int vertex = 0; vertex < currentEdge.length; vertex++) {
            currentEdge[vertex] = network.firstEdgeOf(vertex);
        }
    }

    private static long pushBlockingFlow(
            FlowNetwork network, int source, int sink, int[] level, int[] currentEdge, int[] path) {
        long pushed = 0;
        int depth = 0;
        int vertex = source;

        while (true) {
            if (vertex == sink) {
                pushed += augment(network, path, depth);
                depth = 0;
                vertex = source;
                continue;
            }

            final int edge = findAdmissibleEdge(network, vertex, level, currentEdge);

            if (edge != FlowNetwork.NO_EDGE) {
                path[depth++] = edge;
                vertex = network.targetOf(edge);
                continue;
            }

            if (vertex == source) {
                return pushed;
            }

            level[vertex] = ResidualSearch.UNREACHED;
            depth--;
            vertex = network.targetOf(path[depth] ^ 1);
        }
    }

    /** Next edge out of {@code vertex} with free capacity that goes exactly one level deeper. */
    private static int findAdmissibleEdge(FlowNetwork network, int vertex, int[] level, int[] currentEdge) {
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

    private static int augment(FlowNetwork network, int[] path, int length) {
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

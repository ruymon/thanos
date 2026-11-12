package thanos.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class MaxFlowSolverTest {

    static Stream<MaxFlowSolver> solvers() {
        return Stream.of(new DinicMaxFlow(), new EdmondsKarpMaxFlow());
    }

    @ParameterizedTest
    @MethodSource("solvers")
    void singleEdgeCarriesItsCapacity(MaxFlowSolver solver) {
        final FlowNetwork network = new FlowNetwork(2);
        network.addEdge(0, 1, 7);

        final MinCut cut = solver.solve(network, 0, 1);

        assertEquals(7, cut.value());
    }

    @ParameterizedTest
    @MethodSource("solvers")
    void pathIsLimitedByItsNarrowestEdge(MaxFlowSolver solver) {
        final FlowNetwork network = new FlowNetwork(4);
        network.addEdge(0, 1, 10);
        network.addEdge(1, 2, 3);
        network.addEdge(2, 3, 10);

        final MinCut cut = solver.solve(network, 0, 3);

        assertEquals(3, cut.value());
    }

    @ParameterizedTest
    @MethodSource("solvers")
    void disconnectedSinkGetsNoFlow(MaxFlowSolver solver) {
        final FlowNetwork network = new FlowNetwork(3);
        network.addEdge(0, 1, 5);

        final MinCut cut = solver.solve(network, 0, 2);

        assertEquals(0, cut.value());
        assertFalse(cut.isOnSourceSide(2));
    }

    /** The flow network of CLRS, Introduction to Algorithms, figure 26.1. Its maximum flow is 23. */
    @ParameterizedTest
    @MethodSource("solvers")
    void solvesTheTextbookNetwork(MaxFlowSolver solver) {
        final FlowNetwork network = textbookNetwork();

        final MinCut cut = solver.solve(network, 0, 5);

        assertEquals(23, cut.value());
    }

    /** In the same network the minimum cut is {s, v1, v2, v4} against {v3, t}. */
    @ParameterizedTest
    @MethodSource("solvers")
    void splitsTheTextbookNetworkAlongItsMinimumCut(MaxFlowSolver solver) {
        final FlowNetwork network = textbookNetwork();

        final MinCut cut = solver.solve(network, 0, 5);

        assertTrue(cut.isOnSourceSide(0));
        assertTrue(cut.isOnSourceSide(1));
        assertTrue(cut.isOnSourceSide(2));
        assertTrue(cut.isOnSourceSide(4));
        assertFalse(cut.isOnSourceSide(3));
        assertFalse(cut.isOnSourceSide(5));
    }

    @ParameterizedTest
    @MethodSource("solvers")
    void undirectedEdgeCarriesFlowEitherWay(MaxFlowSolver solver) {
        final FlowNetwork network = new FlowNetwork(3);
        network.addUndirectedEdge(1, 0, 4);
        network.addUndirectedEdge(2, 1, 9);

        final MinCut cut = solver.solve(network, 0, 2);

        assertEquals(4, cut.value());
    }

    @Test
    void bothSolversAgreeOnRandomNetworks() {
        final Random random = new Random(42);

        for (int round = 0; round < 200; round++) {
            final int vertexCount = 2 + random.nextInt(12);
            final int edgeCount = random.nextInt(40);
            final FlowNetwork forDinic = new FlowNetwork(vertexCount);
            final FlowNetwork forEdmondsKarp = new FlowNetwork(vertexCount);

            for (int edge = 0; edge < edgeCount; edge++) {
                final int from = random.nextInt(vertexCount);
                final int to = random.nextInt(vertexCount);
                final int capacity = random.nextInt(20);
                forDinic.addEdge(from, to, capacity);
                forEdmondsKarp.addEdge(from, to, capacity);
            }

            final MinCut dinic = new DinicMaxFlow().solve(forDinic, 0, vertexCount - 1);
            final MinCut edmondsKarp = new EdmondsKarpMaxFlow().solve(forEdmondsKarp, 0, vertexCount - 1);

            assertEquals(edmondsKarp.value(), dinic.value(), "round " + round);
        }
    }

    private static FlowNetwork textbookNetwork() {
        final FlowNetwork network = new FlowNetwork(6);
        network.addEdge(0, 1, 16);
        network.addEdge(0, 2, 13);
        network.addEdge(1, 3, 12);
        network.addEdge(2, 1, 4);
        network.addEdge(2, 4, 14);
        network.addEdge(3, 2, 9);
        network.addEdge(3, 5, 20);
        network.addEdge(4, 3, 7);
        network.addEdge(4, 5, 4);
        return network;
    }
}

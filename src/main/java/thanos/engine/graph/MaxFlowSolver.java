package thanos.engine.graph;

public interface MaxFlowSolver {

    /**
     * Pushes as much flow as possible from {@code source} to {@code sink}.
     *
     * <p>The network is consumed: after the call it holds residual capacities.
     */
    MinCut solve(FlowNetwork network, int source, int sink);
}

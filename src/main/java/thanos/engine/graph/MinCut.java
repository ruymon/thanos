package thanos.engine.graph;

/**
 * The cheapest set of edges whose removal disconnects the source from the sink.
 *
 * <p>By the max-flow min-cut theorem its value equals the maximum flow. The cut is described by
 * the side each vertex ends up on.
 */
public final class MinCut {

    private final long value;
    private final boolean[] onSourceSide;

    MinCut(long value, boolean[] onSourceSide) {
        this.value = value;
        this.onSourceSide = onSourceSide;
    }

    public long value() {
        return value;
    }

    public boolean isOnSourceSide(int vertex) {
        return onSourceSide[vertex];
    }
}

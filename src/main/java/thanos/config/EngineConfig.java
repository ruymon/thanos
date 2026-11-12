package thanos.config;

public final class EngineConfig {

    /** Longest side, in pixels, of the copy the cut runs on (ADR 0004). */
    public static final int MAX_WORKING_SIDE = 400;

    /** Colours are grouped into this many bins per channel to build a histogram (ADR 0005). */
    public static final int HISTOGRAM_BINS_PER_CHANNEL = 16;

    /** Default smoothness (λ): how much the cut resists separating neighbours. */
    public static final double DEFAULT_SMOOTHNESS = 200.0;

    /** Default edge sensitivity (σ): how small a colour change already counts as an edge. */
    public static final double DEFAULT_EDGE_SENSITIVITY = 40.0;

    /** Real-valued weights are multiplied by this before rounding to integer capacities. */
    public static final int CAPACITY_SCALE = 100;

    private EngineConfig() {}
}

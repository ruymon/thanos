package thanos.segmentation;

import thanos.config.EngineConfig;

/**
 * The two values the user tunes.
 *
 * @param smoothness how much the cut resists separating neighbouring pixels (λ)
 * @param edgeSensitivity how small a colour change already counts as an edge (σ)
 */
public record CutParameters(double smoothness, double edgeSensitivity) {

    public CutParameters {
        if (smoothness < 0) {
            throw new IllegalArgumentException("Smoothness must not be negative: " + smoothness);
        }

        if (edgeSensitivity <= 0) {
            throw new IllegalArgumentException("Edge sensitivity must be positive: " + edgeSensitivity);
        }
    }

    public static CutParameters defaults() {
        return new CutParameters(EngineConfig.DEFAULT_SMOOTHNESS, EngineConfig.DEFAULT_EDGE_SENSITIVITY);
    }
}

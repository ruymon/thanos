package thanos.engine.segmentation;

import static thanos.config.EngineConfig.DEFAULT_EDGE_SENSITIVITY;
import static thanos.config.EngineConfig.DEFAULT_SMOOTHNESS;

/**
 * The two values the user tunes.
 *
 * @param smoothness how much the cut resists separating neighbouring pixels (λ)
 * @param edgeSensitivity how small a colour change already counts as an edge (σ)
 */
public record CutParameters(double smoothness, double edgeSensitivity) {

    public CutParameters {
        if (smoothness < 0) {
            throw new IllegalArgumentException("Invalid smoothness: " + smoothness);
        }

        if (edgeSensitivity <= 0) {
            throw new IllegalArgumentException("Invalid edge sensitivity: " + edgeSensitivity);
        }
    }

    public static CutParameters defaults() {
        return new CutParameters(DEFAULT_SMOOTHNESS, DEFAULT_EDGE_SENSITIVITY);
    }
}

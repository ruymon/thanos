package thanos.engine.segmentation;

import thanos.config.EngineConfig;

/**
 * How often each colour appears under one kind of scribble.
 *
 * <p>Colours are grouped into bins. The histogram answers one question: how surprising would a
 * given colour be if the pixel belonged to this side?
 */
final class ColorHistogram {

    private static final int BINS = EngineConfig.HISTOGRAM_BINS_PER_CHANNEL;
    private static final int CHANNEL_LEVELS = 256;
    private static final int BIN_COUNT = BINS * BINS * BINS;

    /** Every bin starts with one imaginary sample, so an unseen colour is unlikely, never impossible. */
    private static final int PRIOR_SAMPLES_PER_BIN = 1;

    private final int[] samples = new int[BIN_COUNT];
    private int total;

    void add(int rgb) {
        samples[binOf(rgb)]++;
        total++;
    }

    /** Negative log-probability of {@code rgb}: 0 for a certain colour, larger the rarer it is. */
    double surpriseOf(int rgb) {
        final double seen = samples[binOf(rgb)] + PRIOR_SAMPLES_PER_BIN;
        final double all = total + (double) BIN_COUNT * PRIOR_SAMPLES_PER_BIN;

        return -Math.log(seen / all);
    }

    private static int binOf(int rgb) {
        final int red = ((rgb >> 16) & 0xFF) * BINS / CHANNEL_LEVELS;
        final int green = ((rgb >> 8) & 0xFF) * BINS / CHANNEL_LEVELS;
        final int blue = (rgb & 0xFF) * BINS / CHANNEL_LEVELS;

        return (red * BINS + green) * BINS + blue;
    }
}

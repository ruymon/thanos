package thanos.segmentation;

import java.awt.image.BufferedImage;
import thanos.config.EngineConfig;
import thanos.graph.MaxFlowSolver;
import thanos.graph.MinCut;
import thanos.image.ImageScaler;

/**
 * Separates the subject of a photo from its background.
 *
 * <p>The single entry point of the engine, shared by the command line and the window (ADR 0006).
 */
public final class Segmenter {

    private static final long NANOS_PER_MILLI = 1_000_000L;

    private final MaxFlowSolver solver;

    public Segmenter(MaxFlowSolver solver) {
        this.solver = solver;
    }

    /**
     * Runs one cut.
     *
     * @param scribbles at any size: they are rescaled to the photo
     * @throws MissingScribblesException unless there is a subject and a background scribble
     */
    public SnapResult snap(BufferedImage photo, Scribbles scribbles, CutParameters parameters) {
        final long startedAt = System.nanoTime();

        final BufferedImage working = ImageScaler.fitWithin(photo, EngineConfig.MAX_WORKING_SIDE);
        final int width = working.getWidth();
        final int height = working.getHeight();
        final int[] pixels = working.getRGB(0, 0, width, height, null, 0, width);
        final Scribbles workingScribbles = scribbles.scaledTo(width, height);

        assertHas(workingScribbles, Label.SUBJECT);
        assertHas(workingScribbles, Label.BACKGROUND);

        final PixelGraph graph = PixelGraph.of(pixels, width, height, workingScribbles, parameters);
        final int vertexCount = graph.network().vertexCount();
        final int edgeCount = graph.network().edgeCount();
        final MinCut cut = solver.solve(graph.network(), graph.source(), graph.sink());

        final Mask mask = maskOf(cut, width, height).scaledTo(photo.getWidth(), photo.getHeight());
        final long elapsedMillis = (System.nanoTime() - startedAt) / NANOS_PER_MILLI;

        return new SnapResult(mask, new SnapStats(width, height, vertexCount, edgeCount, cut.value(), elapsedMillis));
    }

    /** Pixels still connected to the source after the cut are the subject. */
    private static Mask maskOf(MinCut cut, int width, int height) {
        final boolean[] subject = new boolean[width * height];

        for (int pixel = 0; pixel < subject.length; pixel++) {
            subject[pixel] = cut.isOnSourceSide(pixel);
        }

        return new Mask(width, height, subject);
    }

    private static void assertHas(Scribbles scribbles, Label label) {
        if (!scribbles.has(label)) {
            throw new MissingScribblesException(label);
        }
    }
}

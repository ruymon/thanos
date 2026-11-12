package thanos.engine.segmentation;

import thanos.config.EngineConfig;
import thanos.engine.graph.FlowNetwork;

/**
 * A photo as a flow network: one vertex per pixel, plus a source for the subject and a sink for the
 * background. See {@code .docs/graph-model.md}.
 */
final class PixelGraph {

    /** Capacity of a link that must never be cut: a scribbled pixel stays on its side. */
    private static final int UNCUTTABLE = Integer.MAX_VALUE / 4;

    private static final int NEIGHBOUR_LINKS_PER_PIXEL = 2;
    private static final int TERMINAL_LINKS_PER_PIXEL = 2;
    private static final int LINKS_PER_PIXEL = NEIGHBOUR_LINKS_PER_PIXEL + TERMINAL_LINKS_PER_PIXEL;

    private final FlowNetwork network;
    private final int width;
    private final int source;
    private final int sink;

    private PixelGraph(FlowNetwork network, int width, int source, int sink) {
        this.network = network;
        this.width = width;
        this.source = source;
        this.sink = sink;
    }

    /**
     * @param pixels the photo, row by row, as RGB
     * @param scribbles at the photo's size, with at least one scribble of each kind
     */
    static PixelGraph of(int[] pixels, Scribbles scribbles, CutParameters parameters) {
        final int width = scribbles.width();
        final int height = scribbles.height();
        final int pixelCount = width * height;
        final int source = pixelCount;
        final int sink = pixelCount + 1;
        final FlowNetwork network = new FlowNetwork(pixelCount + 2, pixelCount * LINKS_PER_PIXEL);
        final PixelGraph graph = new PixelGraph(network, width, source, sink);

        graph.addNeighbourLinks(pixels, height, parameters);
        graph.addTerminalLinks(pixels, height, scribbles);

        return graph;
    }

    FlowNetwork network() {
        return network;
    }

    int source() {
        return source;
    }

    int sink() {
        return sink;
    }

    int vertexOf(int x, int y) {
        return y * width + x;
    }

    /**
     * Links every pixel to its right and lower neighbours: expensive to cut where colours agree.
     */
    private void addNeighbourLinks(int[] pixels, int height, CutParameters parameters) {
        final double sigma = parameters.edgeSensitivity();
        final double falloff = 2 * sigma * sigma;
        final double scale = parameters.smoothness() * EngineConfig.CAPACITY_SCALE;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int pixel = vertexOf(x, y);

                if (x + 1 < width) {
                    addNeighbourLink(pixels, pixel, vertexOf(x + 1, y), scale, falloff);
                }

                if (y + 1 < height) {
                    addNeighbourLink(pixels, pixel, vertexOf(x, y + 1), scale, falloff);
                }
            }
        }
    }

    private void addNeighbourLink(int[] pixels, int a, int b, double scale, double falloff) {
        final double similarity = Math.exp(-squaredDistance(pixels[a], pixels[b]) / falloff);
        final int capacity = (int) Math.round(scale * similarity);

        network.addUndirectedEdge(a, b, capacity);
    }

    /**
     * Links every pixel to both terminals: scribbled pixels are pinned, the rest follow their
     * colour.
     */
    private void addTerminalLinks(int[] pixels, int height, Scribbles scribbles) {
        final ColorHistogram subject = histogramOf(pixels, height, scribbles, Label.SUBJECT);
        final ColorHistogram background = histogramOf(pixels, height, scribbles, Label.BACKGROUND);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int pixel = vertexOf(x, y);

                switch (scribbles.labelAt(x, y)) {
                    case SUBJECT -> network.addEdge(source, pixel, UNCUTTABLE);
                    case BACKGROUND -> network.addEdge(pixel, sink, UNCUTTABLE);
                    case NONE -> {
                        final int color = pixels[pixel];
                        network.addEdge(source, pixel, capacityOf(background.surpriseOf(color)));
                        network.addEdge(pixel, sink, capacityOf(subject.surpriseOf(color)));
                    }
                }
            }
        }
    }

    private ColorHistogram histogramOf(int[] pixels, int height, Scribbles scribbles, Label label) {
        final ColorHistogram histogram = new ColorHistogram();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (scribbles.labelAt(x, y) == label) {
                    histogram.add(pixels[vertexOf(x, y)]);
                }
            }
        }

        return histogram;
    }

    private static int capacityOf(double surprise) {
        return (int) Math.round(surprise * EngineConfig.CAPACITY_SCALE);
    }

    private static double squaredDistance(int rgbA, int rgbB) {
        final int red = ((rgbA >> 16) & 0xFF) - ((rgbB >> 16) & 0xFF);
        final int green = ((rgbA >> 8) & 0xFF) - ((rgbB >> 8) & 0xFF);
        final int blue = (rgbA & 0xFF) - (rgbB & 0xFF);

        return red * red + green * green + blue * blue;
    }
}

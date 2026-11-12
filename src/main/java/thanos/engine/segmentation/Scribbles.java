package thanos.engine.segmentation;

import java.awt.image.BufferedImage;

/**
 * The rough strokes the user draws to say "this is subject" or "this is background".
 *
 * <p>A grid of labels with its own size, independent of the photo: it is rescaled to whatever
 * size the cut runs on. As an image, a scribble is any opaque pixel: more red than blue means
 * subject, more blue than red means background.
 */
public final class Scribbles {

    public static final int SUBJECT_COLOR = 0xFFE5482D;
    public static final int BACKGROUND_COLOR = 0xFF2D6CE5;

    private static final int OPAQUE_FROM_ALPHA = 128;
    private static final Label[] LABELS = Label.values();

    private final int width;
    private final int height;
    private final byte[] labels;

    public Scribbles(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Scribbles need a positive size: " + width + "x" + height);
        }

        this.width = width;
        this.height = height;
        this.labels = new byte[width * height];
    }

    public static Scribbles fromImage(BufferedImage image) {
        final Scribbles scribbles = new Scribbles(image.getWidth(), image.getHeight());

        for (int y = 0; y < scribbles.height; y++) {
            for (int x = 0; x < scribbles.width; x++) {
                scribbles.mark(x, y, labelOf(image.getRGB(x, y)));
            }
        }

        return scribbles;
    }

    public void mark(int x, int y, Label label) {
        labels[y * width + x] = (byte) label.ordinal();
    }

    public Label labelAt(int x, int y) {
        return LABELS[labels[y * width + x]];
    }

    public boolean has(Label label) {
        for (final byte stored : labels) {
            if (stored == label.ordinal()) {
                return true;
            }
        }

        return false;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    /** The same scribbles on a grid of another size. A labelled pixel always survives the resize. */
    public Scribbles scaledTo(int newWidth, int newHeight) {
        if (newWidth == width && newHeight == height) {
            return this;
        }

        final Scribbles scaled = new Scribbles(newWidth, newHeight);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final Label label = labelAt(x, y);

                if (label != Label.NONE) {
                    scaled.mark(x * newWidth / width, y * newHeight / height, label);
                }
            }
        }

        return scaled;
    }

    public BufferedImage toImage() {
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, colorOf(labelAt(x, y)));
            }
        }

        return image;
    }

    private static Label labelOf(int argb) {
        final int alpha = argb >>> 24;
        final int red = (argb >> 16) & 0xFF;
        final int blue = argb & 0xFF;

        if (alpha < OPAQUE_FROM_ALPHA || red == blue) {
            return Label.NONE;
        }

        return red > blue ? Label.SUBJECT : Label.BACKGROUND;
    }

    private static int colorOf(Label label) {
        return switch (label) {
            case SUBJECT -> SUBJECT_COLOR;
            case BACKGROUND -> BACKGROUND_COLOR;
            case NONE -> 0;
        };
    }
}

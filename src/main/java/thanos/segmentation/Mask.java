package thanos.segmentation;

import java.awt.image.BufferedImage;

/**
 * The answer of a snap: for every pixel, whether it belongs to the subject.
 *
 * <p>A mask that was enlarged remembers by how much, so the cutout can soften the staircase the
 * enlargement leaves on the edge.
 */
public final class Mask {

    private static final int OPAQUE = 255;

    private final int width;
    private final int height;
    private final boolean[] subject;
    private final int edgeSoftness;

    Mask(int width, int height, boolean[] subject) {
        this(width, height, subject, 0);
    }

    private Mask(int width, int height, boolean[] subject, int edgeSoftness) {
        this.width = width;
        this.height = height;
        this.subject = subject;
        this.edgeSoftness = edgeSoftness;
    }

    public boolean isSubject(int x, int y) {
        return subject[y * width + x];
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int subjectPixelCount() {
        int count = 0;

        for (final boolean isSubject : subject) {
            if (isSubject) {
                count++;
            }
        }

        return count;
    }

    /** The same mask on a grid of another size. */
    public Mask scaledTo(int newWidth, int newHeight) {
        if (newWidth == width && newHeight == height) {
            return this;
        }

        final boolean[] scaled = new boolean[newWidth * newHeight];

        for (int y = 0; y < newHeight; y++) {
            final int sourceRow = (y * height / newHeight) * width;

            for (int x = 0; x < newWidth; x++) {
                scaled[y * newWidth + x] = subject[sourceRow + x * width / newWidth];
            }
        }

        final int enlargement = Math.max(newWidth / width, newHeight / height);

        return new Mask(newWidth, newHeight, scaled, enlargement / 2);
    }

    /** The photo with every background pixel made transparent. */
    public BufferedImage cutOut(BufferedImage photo) {
        final Mask fitted = scaledTo(photo.getWidth(), photo.getHeight());
        final int[] alpha = fitted.alphaChannel();
        final int[] pixels = photo.getRGB(0, 0, fitted.width, fitted.height, null, 0, fitted.width);

        for (int index = 0; index < pixels.length; index++) {
            pixels[index] = (alpha[index] << 24) | (pixels[index] & 0x00FFFFFF);
        }

        final BufferedImage cutout = new BufferedImage(fitted.width, fitted.height, BufferedImage.TYPE_INT_ARGB);
        cutout.setRGB(0, 0, fitted.width, fitted.height, pixels, 0, fitted.width);

        return cutout;
    }

    private int[] alphaChannel() {
        final int[] alpha = new int[subject.length];

        for (int index = 0; index < alpha.length; index++) {
            alpha[index] = subject[index] ? OPAQUE : 0;
        }

        if (edgeSoftness == 0) {
            return alpha;
        }

        final int[] blurredAcross = new int[alpha.length];
        final int[] blurred = new int[alpha.length];
        averageAlongRows(alpha, blurredAcross);
        averageAlongColumns(blurredAcross, blurred);

        return blurred;
    }

    private void averageAlongRows(int[] from, int[] to) {
        for (int y = 0; y < height; y++) {
            averageLine(from, to, y * width, 1, width);
        }
    }

    private void averageAlongColumns(int[] from, int[] to) {
        for (int x = 0; x < width; x++) {
            averageLine(from, to, x, width, height);
        }
    }

    /** Replaces each value on a line with the mean of its neighbours within {@code edgeSoftness}. */
    private void averageLine(int[] from, int[] to, int start, int step, int length) {
        int sum = 0;
        int count = 0;

        for (int position = 0; position < Math.min(edgeSoftness, length); position++) {
            sum += from[start + position * step];
            count++;
        }

        for (int position = 0; position < length; position++) {
            final int entering = position + edgeSoftness;
            final int leaving = position - edgeSoftness - 1;

            if (entering < length) {
                sum += from[start + entering * step];
                count++;
            }

            if (leaving >= 0) {
                sum -= from[start + leaving * step];
                count--;
            }

            to[start + position * step] = sum / count;
        }
    }
}

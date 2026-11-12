package thanos.engine.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public final class ImageScaler {

    private ImageScaler() {}

    /** The image itself if its longest side is at most {@code maxSide}, a smaller copy otherwise. */
    public static BufferedImage fitWithin(BufferedImage image, int maxSide) {
        final int longestSide = Math.max(image.getWidth(), image.getHeight());

        if (longestSide <= maxSide) {
            return image;
        }

        final int width = Math.max(1, image.getWidth() * maxSide / longestSide);
        final int height = Math.max(1, image.getHeight() * maxSide / longestSide);

        return scaleTo(image, width, height);
    }

    private static BufferedImage scaleTo(BufferedImage image, int width, int height) {
        final BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = scaled.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.drawImage(image, 0, 0, width, height, null);
        graphics.dispose();

        return scaled;
    }
}

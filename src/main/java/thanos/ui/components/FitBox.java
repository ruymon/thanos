package thanos.ui.components;

import thanos.ui.Theme;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * Where an image lands when it is drawn as large as possible inside a panel, centred and without
 * distortion.
 *
 * @param scale panel pixels per image pixel
 */
record FitBox(int x, int y, int width, int height, double scale) {

    static FitBox of(JComponent panel, BufferedImage image) {
        final double availableWidth = Math.max(1, panel.getWidth() - 2 * Theme.SPACE_MD);
        final double availableHeight = Math.max(1, panel.getHeight() - 2 * Theme.SPACE_MD);
        final double widthScale = availableWidth / image.getWidth();
        final double heightScale = availableHeight / image.getHeight();
        final double scale = Math.min(widthScale, heightScale);

        final int width = (int) Math.round(image.getWidth() * scale);
        final int height = (int) Math.round(image.getHeight() * scale);
        final int x = (panel.getWidth() - width) / 2;
        final int y = (panel.getHeight() - height) / 2;

        return new FitBox(x, y, width, height, scale);
    }

    int toImageX(int panelX) {
        return (int) Math.floor((panelX - x) / scale);
    }

    int toImageY(int panelY) {
        return (int) Math.floor((panelY - y) / scale);
    }
}

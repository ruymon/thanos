package thanos.ui.components;

/**
 * Where an image lands when it is drawn as large as possible inside a panel, centred and without
 * distortion.
 *
 * @param scale panel pixels per image pixel
 */
record FitBox(int x, int y, int width, int height, double scale) {

    static FitBox of(int panelWidth, int panelHeight, int imageWidth, int imageHeight, int padding) {
        final double availableWidth = Math.max(1, panelWidth - 2 * padding);
        final double availableHeight = Math.max(1, panelHeight - 2 * padding);
        final double scale = Math.min(availableWidth / imageWidth, availableHeight / imageHeight);

        final int width = (int) Math.round(imageWidth * scale);
        final int height = (int) Math.round(imageHeight * scale);

        return new FitBox((panelWidth - width) / 2, (panelHeight - height) / 2, width, height, scale);
    }

    int toImageX(int panelX) {
        return (int) Math.floor((panelX - x) / scale);
    }

    int toImageY(int panelY) {
        return (int) Math.floor((panelY - y) / scale);
    }
}

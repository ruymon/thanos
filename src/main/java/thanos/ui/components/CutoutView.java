package thanos.ui.components;

import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;

import thanos.ui.Texts;
import thanos.ui.Theme;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public final class CutoutView extends JPanel {

    private static final long serialVersionUID = 1L;

    private transient BufferedImage cutout;

    public CutoutView() {
        setBackground(Theme.BG_CANVAS);
    }

    /**
     * @param newCutout the cutout to show, or {@code null} to show nothing
     */
    public void setCutout(BufferedImage newCutout) {
        cutout = newCutout;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        final Graphics2D canvas = (Graphics2D) graphics.create();

        if (cutout == null) {
            EmptyMessage.paint(canvas, this, Texts.CUTOUT_EMPTY);
        } else {
            paintCutout(canvas);
        }

        canvas.dispose();
    }

    private void paintCutout(Graphics2D canvas) {
        final FitBox box = FitBox.of(this, cutout);

        paintCheckerboard(canvas, box);
        canvas.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
        canvas.drawImage(cutout, box.x(), box.y(), box.width(), box.height(), null);
    }

    /** The checkerboard behind the cutout, so transparent pixels are visible. */
    private static void paintCheckerboard(Graphics2D canvas, FitBox box) {
        final int size = Theme.CHECKER_SIZE;
        final Shape previousClip = canvas.getClip();

        canvas.clipRect(box.x(), box.y(), box.width(), box.height());
        canvas.setColor(Theme.CHECKER_LIGHT);
        canvas.fillRect(box.x(), box.y(), box.width(), box.height());
        canvas.setColor(Theme.CHECKER_DARK);

        for (int row = 0; row * size < box.height(); row++) {
            for (int column = row % 2; column * size < box.width(); column += 2) {
                canvas.fillRect(box.x() + column * size, box.y() + row * size, size, size);
            }
        }

        canvas.setClip(previousClip);
    }
}

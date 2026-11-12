package thanos.ui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import thanos.ui.Texts;
import thanos.ui.Theme;

public final class CutoutView extends JPanel {

    private static final long serialVersionUID = 1L;

    private transient BufferedImage cutout;

    public CutoutView() {
        setBackground(Theme.BG_CANVAS);
    }

    /** @param newCutout the cutout to show, or {@code null} to show nothing */
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
            canvas.dispose();
            return;
        }

        final FitBox box = FitBox.of(getWidth(), getHeight(), cutout.getWidth(), cutout.getHeight(), Theme.SPACE_MD);

        paintCheckerboard(canvas, box);
        canvas.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        canvas.drawImage(cutout, box.x(), box.y(), box.width(), box.height(), null);
        canvas.dispose();
    }

    private static void paintCheckerboard(Graphics2D canvas, FitBox box) {
        final Shape previousClip = canvas.getClip();
        canvas.clipRect(box.x(), box.y(), box.width(), box.height());
        canvas.setColor(Theme.CHECKER_LIGHT);
        canvas.fillRect(box.x(), box.y(), box.width(), box.height());
        canvas.setColor(Theme.CHECKER_DARK);

        for (int row = 0; row * Theme.CHECKER_SIZE < box.height(); row++) {
            for (int column = row % 2; column * Theme.CHECKER_SIZE < box.width(); column += 2) {
                canvas.fillRect(
                        box.x() + column * Theme.CHECKER_SIZE,
                        box.y() + row * Theme.CHECKER_SIZE,
                        Theme.CHECKER_SIZE,
                        Theme.CHECKER_SIZE);
            }
        }

        canvas.setClip(previousClip);
    }
}

package thanos.ui.components;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import thanos.ui.Theme;

/** A muted line of text in the middle of a panel that has nothing to show yet. */
final class EmptyMessage {

    private EmptyMessage() {}

    static void paint(Graphics2D canvas, JComponent panel, String message) {
        canvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        canvas.setFont(Theme.FONT_BASE);
        canvas.setColor(Theme.TEXT_MUTED);

        final FontMetrics metrics = canvas.getFontMetrics();
        final int x = (panel.getWidth() - metrics.stringWidth(message)) / 2;
        final int y = (panel.getHeight() + metrics.getAscent()) / 2;

        canvas.drawString(message, x, y);
    }
}

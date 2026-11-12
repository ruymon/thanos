package thanos.ui.components;

import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;

import thanos.ui.Theme;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

final class EmptyMessage {

    private EmptyMessage() {}

    static void paint(Graphics2D canvas, JComponent panel, String message) {
        canvas.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        canvas.setFont(Theme.FONT_BASE);
        canvas.setColor(Theme.TEXT_MUTED);

        final FontMetrics metrics = canvas.getFontMetrics();
        final int x = (panel.getWidth() - metrics.stringWidth(message)) / 2;
        final int y = (panel.getHeight() + metrics.getAscent()) / 2;

        canvas.drawString(message, x, y);
    }
}

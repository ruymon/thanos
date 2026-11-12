package thanos.ui.components;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_OFF;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;

import thanos.engine.image.ImageScaler;
import thanos.engine.segmentation.Scribbles;
import thanos.ui.Texts;
import thanos.ui.Theme;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Optional;

import javax.swing.JPanel;

public final class ScribbleCanvas extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Scribbles are drawn on a copy of the photo no larger than this, whatever its size. */
    private static final int MAX_DRAWING_SIDE = 900;

    private static final float BRUSH_SCREEN_WIDTH = 9f;
    private static final float ERASER_SCREEN_WIDTH = 24f;
    private static final float SCRIBBLE_OPACITY = 0.85f;

    private transient BufferedImage photo;
    private transient BufferedImage scribbleLayer;
    private Brush brush = Brush.SUBJECT;
    private int lastX;
    private int lastY;

    public ScribbleCanvas() {
        final Drawing drawing = new Drawing();

        setBackground(Theme.BG_CANVAS);
        addMouseListener(drawing);
        addMouseMotionListener(drawing);
    }

    public void setPhoto(BufferedImage newPhoto) {
        photo = ImageScaler.fitWithin(newPhoto, MAX_DRAWING_SIDE);
        scribbleLayer = createEmptyLayer();
        repaint();
    }

    public void setBrush(Brush newBrush) {
        brush = newBrush;
    }

    public void clearScribbles() {
        if (photo == null) {
            return;
        }

        scribbleLayer = createEmptyLayer();
        repaint();
    }

    public Optional<Scribbles> findScribbles() {
        if (photo == null) {
            return Optional.empty();
        }

        return Optional.of(Scribbles.fromImage(scribbleLayer));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        final Graphics2D canvas = (Graphics2D) graphics.create();

        if (photo == null) {
            EmptyMessage.paint(canvas, this, Texts.CANVAS_EMPTY);
        } else {
            paintPhotoAndScribbles(canvas);
        }

        canvas.dispose();
    }

    private void paintPhotoAndScribbles(Graphics2D canvas) {
        final FitBox box = FitBox.of(this, photo);

        canvas.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
        canvas.drawImage(photo, box.x(), box.y(), box.width(), box.height(), null);
        canvas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, SCRIBBLE_OPACITY));
        canvas.drawImage(scribbleLayer, box.x(), box.y(), box.width(), box.height(), null);
    }

    private BufferedImage createEmptyLayer() {
        return new BufferedImage(photo.getWidth(), photo.getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    private void startStroke(MouseEvent event) {
        if (photo == null) {
            return;
        }

        final FitBox box = FitBox.of(this, photo);
        lastX = box.toImageX(event.getX());
        lastY = box.toImageY(event.getY());
        drawSegment(lastX, lastY, box);
    }

    private void continueStroke(MouseEvent event) {
        if (photo == null) {
            return;
        }

        final FitBox box = FitBox.of(this, photo);
        drawSegment(box.toImageX(event.getX()), box.toImageY(event.getY()), box);
    }

    /** Draws from the last point to this one. Not antialiased: a scribble is one pure colour. */
    private void drawSegment(int x, int y, FitBox box) {
        final Graphics2D layer = scribbleLayer.createGraphics();
        final float screenWidth = brush == Brush.ERASER ? ERASER_SCREEN_WIDTH : BRUSH_SCREEN_WIDTH;
        final float width = (float) Math.max(1.0, screenWidth / box.scale());

        layer.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
        layer.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (brush) {
            case SUBJECT -> layer.setColor(new Color(Scribbles.SUBJECT_COLOR, true));
            case BACKGROUND -> layer.setColor(new Color(Scribbles.BACKGROUND_COLOR, true));
            case ERASER -> layer.setComposite(AlphaComposite.Clear);
        }

        layer.drawLine(lastX, lastY, x, y);
        layer.dispose();

        lastX = x;
        lastY = y;
        repaint();
    }

    private final class Drawing extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent event) {
            startStroke(event);
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            continueStroke(event);
        }
    }
}

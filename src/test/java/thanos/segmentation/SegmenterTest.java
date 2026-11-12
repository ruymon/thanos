package thanos.segmentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import thanos.graph.DinicMaxFlow;

class SegmenterTest {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 40;
    private static final int ORANGE = 0xE08030;
    private static final int TEAL = 0x208090;

    private final Segmenter segmenter = new Segmenter(new DinicMaxFlow());

    @Test
    void splitsATwoColourPhotoAlongTheColourChange() {
        final BufferedImage photo = leftRightPhoto(ORANGE, TEAL);
        final Scribbles scribbles = new Scribbles(WIDTH, HEIGHT);
        scribbles.mark(5, 20, Label.SUBJECT);
        scribbles.mark(55, 20, Label.BACKGROUND);

        final Mask mask = segmenter.snap(photo, scribbles, CutParameters.defaults()).mask();

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                assertEquals(x < WIDTH / 2, mask.isSubject(x, y), "pixel " + x + "," + y);
            }
        }
    }

    @Test
    void keepsAScribbledPixelOnItsSideEvenAgainstItsColour() {
        final BufferedImage photo = leftRightPhoto(ORANGE, TEAL);
        final Scribbles scribbles = new Scribbles(WIDTH, HEIGHT);
        scribbles.mark(5, 20, Label.SUBJECT);
        scribbles.mark(55, 20, Label.BACKGROUND);
        scribbles.mark(50, 5, Label.SUBJECT);

        final Mask mask = segmenter.snap(photo, scribbles, CutParameters.defaults()).mask();

        assertTrue(mask.isSubject(50, 5));
        assertFalse(mask.isSubject(55, 20));
    }

    @Test
    void findsASubjectSurroundedByBackground() {
        final BufferedImage photo = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = photo.createGraphics();
        graphics.setColor(new Color(TEAL));
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        graphics.setColor(new Color(ORANGE));
        graphics.fillRect(20, 10, 20, 20);
        graphics.dispose();

        final Scribbles scribbles = new Scribbles(WIDTH, HEIGHT);
        scribbles.mark(30, 20, Label.SUBJECT);
        scribbles.mark(3, 3, Label.BACKGROUND);

        final Mask mask = segmenter.snap(photo, scribbles, CutParameters.defaults()).mask();

        assertEquals(20 * 20, mask.subjectPixelCount());
        assertTrue(mask.isSubject(20, 10));
        assertTrue(mask.isSubject(39, 29));
        assertFalse(mask.isSubject(19, 10));
        assertFalse(mask.isSubject(40, 29));
    }

    @Test
    void returnsAMaskAtThePhotoSizeWhenThePhotoIsLargerThanTheWorkingSize() {
        final int width = 1200;
        final int height = 600;
        final BufferedImage photo = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = photo.createGraphics();
        graphics.setColor(new Color(ORANGE));
        graphics.fillRect(0, 0, width / 2, height);
        graphics.setColor(new Color(TEAL));
        graphics.fillRect(width / 2, 0, width / 2, height);
        graphics.dispose();

        final Scribbles scribbles = new Scribbles(width, height);
        scribbles.mark(100, 300, Label.SUBJECT);
        scribbles.mark(1100, 300, Label.BACKGROUND);

        final Mask mask = segmenter.snap(photo, scribbles, CutParameters.defaults()).mask();

        assertEquals(width, mask.width());
        assertEquals(height, mask.height());
        assertTrue(mask.isSubject(100, 300));
        assertTrue(mask.isSubject(500, 50));
        assertFalse(mask.isSubject(700, 550));
    }

    @Test
    void refusesToSnapWithoutBothKindsOfScribble() {
        final BufferedImage photo = leftRightPhoto(ORANGE, TEAL);
        final Scribbles scribbles = new Scribbles(WIDTH, HEIGHT);
        scribbles.mark(5, 20, Label.SUBJECT);

        assertThrows(
                MissingScribblesException.class,
                () -> segmenter.snap(photo, scribbles, CutParameters.defaults()));
    }

    @Test
    void cutsOutTheSubjectAndMakesTheBackgroundTransparent() {
        final BufferedImage photo = leftRightPhoto(ORANGE, TEAL);
        final Scribbles scribbles = new Scribbles(WIDTH, HEIGHT);
        scribbles.mark(5, 20, Label.SUBJECT);
        scribbles.mark(55, 20, Label.BACKGROUND);

        final Mask mask = segmenter.snap(photo, scribbles, CutParameters.defaults()).mask();
        final BufferedImage cutout = mask.cutOut(photo);

        assertEquals(0xFF000000 | ORANGE, cutout.getRGB(5, 20));
        assertEquals(0, cutout.getRGB(55, 20) >>> 24);
    }

    private static BufferedImage leftRightPhoto(int leftRgb, int rightRgb) {
        final BufferedImage photo = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                photo.setRGB(x, y, x < WIDTH / 2 ? leftRgb : rightRgb);
            }
        }

        return photo;
    }
}

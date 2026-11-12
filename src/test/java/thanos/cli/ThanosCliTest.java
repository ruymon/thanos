package thanos.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thanos.engine.graph.DinicMaxFlow;
import thanos.engine.segmentation.Label;
import thanos.engine.segmentation.Scribbles;
import thanos.engine.segmentation.Segmenter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;

class ThanosCliTest {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 40;
    private static final int ORANGE = 0xE08030;
    private static final int TEAL = 0x208090;

    @TempDir Path folder;

    private final StringWriter output = new StringWriter();
    private final StringWriter errors = new StringWriter();
    private final AtomicBoolean windowOpened = new AtomicBoolean();

    private Path photo;
    private Path scribbles;
    private Path cutout;

    @BeforeEach
    void createPhotoAndScribbles() throws IOException {
        photo = folder.resolve("photo.png");
        scribbles = folder.resolve("scribbles.png");
        cutout = folder.resolve("cutout.png");

        final BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                image.setRGB(x, y, x < WIDTH / 2 ? ORANGE : TEAL);
            }
        }

        final Scribbles marks = new Scribbles(WIDTH, HEIGHT);
        marks.mark(5, 20, Label.SUBJECT);
        marks.mark(55, 20, Label.BACKGROUND);

        ImageIO.write(image, "png", photo.toFile());
        ImageIO.write(marks.toImage(), "png", scribbles.toFile());
    }

    @Test
    void printsTheHelpWhenCalledWithNothing() {
        final int exitCode = run();

        assertEquals(0, exitCode);
        assertTrue(output.toString().contains("Usage: thanos"));
        assertTrue(output.toString().contains("cut"));
        assertTrue(output.toString().contains("ui"));
    }

    @Test
    void cutsAPhotoIntoATransparentPng() throws IOException {
        final int exitCode =
                run(
                        "cut",
                        photo.toString(),
                        "--scribbles",
                        scribbles.toString(),
                        "--out",
                        cutout.toString());

        assertEquals(0, exitCode, errors.toString());

        final BufferedImage result = ImageIO.read(cutout.toFile());
        assertEquals(0xFF000000 | ORANGE, result.getRGB(5, 20));
        assertEquals(0, result.getRGB(55, 20) >>> 24);
    }

    @Test
    void acceptsTheMathsNamesOfBothParameters() {
        final int exitCode =
                run(
                        "cut",
                        photo.toString(),
                        "--scribbles",
                        scribbles.toString(),
                        "--out",
                        cutout.toString(),
                        "--lambda",
                        "30",
                        "--sigma",
                        "12");

        assertEquals(0, exitCode, errors.toString());
        assertTrue(output.toString().contains("30"));
        assertTrue(output.toString().contains("12"));
    }

    @Test
    void acceptsThePlainNamesOfBothParameters() {
        final int exitCode =
                run(
                        "cut",
                        photo.toString(),
                        "--scribbles",
                        scribbles.toString(),
                        "--out",
                        cutout.toString(),
                        "--smoothness",
                        "30",
                        "--edge-sensitivity",
                        "12");

        assertEquals(0, exitCode, errors.toString());
        assertTrue(output.toString().contains("30"));
        assertTrue(output.toString().contains("12"));
    }

    @Test
    void savesNextToThePhotoWhenNoOutputIsGiven() {
        final int exitCode = run("cut", photo.toString(), "--scribbles", scribbles.toString());

        assertEquals(0, exitCode, errors.toString());
        assertTrue(Files.exists(folder.resolve("photo-cutout.png")));
    }

    @Test
    void failsWithAMessageWhenThePhotoDoesNotExist() {
        final int exitCode =
                run(
                        "cut",
                        folder.resolve("missing.png").toString(),
                        "--scribbles",
                        scribbles.toString());

        assertNotEquals(0, exitCode);
        assertTrue(errors.toString().contains("missing.png"));
        assertFalse(errors.toString().contains("Exception"));
    }

    @Test
    void opensTheWindowOnUi() {
        final int exitCode = run("ui");

        assertEquals(0, exitCode);
        assertTrue(windowOpened.get());
    }

    private int run(String... arguments) {
        final Segmenter segmenter = new Segmenter(new DinicMaxFlow());

        final PrintWriter out = new PrintWriter(output, true);
        final PrintWriter err = new PrintWriter(errors, true);

        return ThanosCli.create(segmenter, () -> windowOpened.set(true), out, err)
                .execute(arguments);
    }
}

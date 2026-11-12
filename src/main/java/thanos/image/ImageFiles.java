package thanos.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/** Reads and writes image files. */
public final class ImageFiles {

    private static final String PNG = "png";

    private ImageFiles() {}

    /** @throws IOException if the file is missing or is not an image Java can decode */
    public static BufferedImage read(Path file) throws IOException {
        if (!Files.isRegularFile(file)) {
            throw new IOException("No such file: " + file);
        }

        final BufferedImage image = ImageIO.read(file.toFile());

        if (image == null) {
            throw new IOException("Not an image Java can read (use PNG or JPEG): " + file);
        }

        return image;
    }

    public static void writePng(BufferedImage image, Path file) throws IOException {
        final Path parent = file.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        ImageIO.write(image, PNG, file.toFile());
    }
}

package thanos.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import thanos.config.AppConfig;
import thanos.config.EngineConfig;
import thanos.engine.image.ImageFiles;
import thanos.engine.segmentation.CutParameters;
import thanos.engine.segmentation.Mask;
import thanos.engine.segmentation.Scribbles;
import thanos.engine.segmentation.Segmenter;
import thanos.engine.segmentation.SnapResult;
import thanos.engine.segmentation.SnapStats;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.Callable;

@Command(
        name = "cut",
        description = "Cut the subject out of a photo and save it as a transparent PNG.",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        optionListHeading = "%nOptions:%n",
        parameterListHeading = "%nArguments:%n")
final class CutCommand implements Callable<Integer> {

    private static final int EXIT_OK = 0;
    private static final int EXIT_FAILED = 1;
    private static final double MILLIS_PER_SECOND = 1000.0;
    private static final double PERCENT = 100.0;
    private static final String DEFAULT_CUTOUT = "<photo>" + AppConfig.CUTOUT_SUFFIX;

    private final Segmenter segmenter;

    @Spec private CommandSpec spec;

    @Parameters(paramLabel = "<photo>", description = "The photo to cut (PNG or JPEG).")
    private Path photoFile;

    @Option(
            names = {"-s", "--scribbles"},
            required = true,
            paramLabel = "<file>",
            description = {
                "Image with the scribbles.",
                "Red over the subject, blue over the background."
            })
    private Path scribblesFile;

    @Option(
            names = {"-o", "--out"},
            paramLabel = "<file>",
            description = {
                "Where to save the cutout.",
                "Default: " + DEFAULT_CUTOUT + ", next to the photo."
            })
    private Path cutoutFile;

    @Option(
            names = {"--smoothness", "--lambda"},
            paramLabel = "<number>",
            description = {
                "How much the cut resists separating neighbours.",
                "Higher is smoother. Default: ${DEFAULT-VALUE}."
            })
    private double smoothness = EngineConfig.DEFAULT_SMOOTHNESS;

    @Option(
            names = {"--edge-sensitivity", "--sigma"},
            paramLabel = "<number>",
            description = {
                "How small a colour change counts as an edge.",
                "Lower sees more edges. Default: ${DEFAULT-VALUE}."
            })
    private double edgeSensitivity = EngineConfig.DEFAULT_EDGE_SENSITIVITY;

    CutCommand(Segmenter segmenter) {
        this.segmenter = segmenter;
    }

    @Override
    public Integer call() {
        final PrintWriter output = spec.commandLine().getOut();
        final PrintWriter errors = spec.commandLine().getErr();

        try {
            final CutParameters parameters = new CutParameters(smoothness, edgeSensitivity);
            final BufferedImage photo = ImageFiles.read(photoFile);
            final Scribbles scribbles = Scribbles.fromImage(ImageFiles.read(scribblesFile));
            final Path destination = cutoutFile != null ? cutoutFile : defaultCutoutFile();

            final SnapResult result = segmenter.snap(photo, scribbles, parameters);
            ImageFiles.writePng(result.mask().cutOut(photo), destination);

            printReport(output, photo, result, destination);

            return EXIT_OK;
        } catch (IOException | IllegalArgumentException failure) {
            errors.println("thanos: " + failure.getMessage());

            return EXIT_FAILED;
        }
    }

    private Path defaultCutoutFile() {
        final String name = photoFile.getFileName().toString();
        final int extension = name.lastIndexOf('.');
        final String stem = extension > 0 ? name.substring(0, extension) : name;

        return photoFile.resolveSibling(stem + AppConfig.CUTOUT_SUFFIX);
    }

    private void printReport(PrintWriter out, BufferedImage photo, SnapResult result, Path file) {
        final SnapStats stats = result.stats();
        final Mask mask = result.mask();
        final double photoPixels = (double) mask.width() * mask.height();
        final double subjectShare = PERCENT * mask.subjectPixelCount() / photoPixels;
        final double seconds = stats.elapsedMillis() / MILLIS_PER_SECOND;

        printRow(out, "Photo", "%d x %d px", photo.getWidth(), photo.getHeight());
        printRow(out, "Cut on", "%d x %d px", stats.workingWidth(), stats.workingHeight());
        printRow(out, "Graph", "%,d vertices, %,d edges", stats.vertexCount(), stats.edgeCount());
        printRow(out, "Smoothness", "%s", smoothness);
        printRow(out, "Edge sensitivity", "%s", edgeSensitivity);
        printRow(out, "Minimum cut", "%,d", stats.cutValue());
        printRow(out, "Subject", "%.1f%% of the photo", subjectShare);
        printRow(out, "Time", "%.2f s", seconds);
        printRow(out, "Saved", "%s", file);
    }

    private static void printRow(PrintWriter out, String label, String format, Object... values) {
        out.printf("%-18s%s%n", label, String.format(Locale.ROOT, format, values));
    }
}

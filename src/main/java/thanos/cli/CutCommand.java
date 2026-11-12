package thanos.cli;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import thanos.config.AppConfig;
import thanos.config.EngineConfig;
import thanos.image.ImageFiles;
import thanos.segmentation.CutParameters;
import thanos.segmentation.Mask;
import thanos.segmentation.Scribbles;
import thanos.segmentation.SnapResult;
import thanos.segmentation.SnapStats;
import thanos.segmentation.Segmenter;

/** {@code thanos cut}: cuts a photo from the terminal. */
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

    private final Segmenter segmenter;

    @Spec
    private CommandSpec spec;

    @Parameters(paramLabel = "<photo>", description = "The photo to cut (PNG or JPEG).")
    private Path photoFile;

    @Option(
            names = {"-s", "--scribbles"},
            required = true,
            paramLabel = "<file>",
            description = "Image with the scribbles: red over the subject, blue over the background.")
    private Path scribblesFile;

    @Option(
            names = {"-o", "--out"},
            paramLabel = "<file>",
            description = "Where to save the cutout. Default: next to the photo, as <photo>" + AppConfig.CUTOUT_SUFFIX + ".")
    private Path cutoutFile;

    @Option(
            names = {"--smoothness", "--lambda"},
            paramLabel = "<number>",
            description = "How much the cut resists separating neighbours. Higher is smoother. Default: ${DEFAULT-VALUE}.")
    private double smoothness = EngineConfig.DEFAULT_SMOOTHNESS;

    @Option(
            names = {"--edge-sensitivity", "--sigma"},
            paramLabel = "<number>",
            description = "How small a colour change counts as an edge. Lower sees more edges. Default: ${DEFAULT-VALUE}.")
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

            printReport(output, photo, parameters, result, destination);

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

    private static void printReport(
            PrintWriter output, BufferedImage photo, CutParameters parameters, SnapResult result, Path destination) {
        final SnapStats stats = result.stats();
        final Mask mask = result.mask();
        final double subjectShare = PERCENT * mask.subjectPixelCount() / ((double) mask.width() * mask.height());

        output.printf(Locale.ROOT, "Photo             %d x %d px%n", photo.getWidth(), photo.getHeight());
        output.printf(Locale.ROOT, "Cut on            %d x %d px%n", stats.workingWidth(), stats.workingHeight());
        output.printf(Locale.ROOT, "Graph             %,d vertices, %,d edges%n", stats.vertexCount(), stats.edgeCount());
        output.printf(Locale.ROOT, "Smoothness        %s%n", parameters.smoothness());
        output.printf(Locale.ROOT, "Edge sensitivity  %s%n", parameters.edgeSensitivity());
        output.printf(Locale.ROOT, "Minimum cut       %,d%n", stats.cutValue());
        output.printf(Locale.ROOT, "Subject           %.1f%% of the photo%n", subjectShare);
        output.printf(Locale.ROOT, "Time              %.2f s%n", stats.elapsedMillis() / MILLIS_PER_SECOND);
        output.printf(Locale.ROOT, "Saved             %s%n", destination);
    }
}

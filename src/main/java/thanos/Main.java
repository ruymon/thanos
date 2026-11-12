package thanos;

import thanos.cli.ThanosCli;
import thanos.graph.DinicMaxFlow;
import thanos.segmentation.Segmenter;
import thanos.ui.AppWindow;

/** Wires the engine to its two front ends. */
public final class Main {

    private static final String WINDOW_COMMAND = "ui";
    private static final String HEADLESS_PROPERTY = "java.awt.headless";

    private Main() {}

    public static void main(String[] args) {
        if (!opensTheWindow(args)) {
            System.setProperty(HEADLESS_PROPERTY, Boolean.TRUE.toString());
        }

        final Segmenter segmenter = new Segmenter(new DinicMaxFlow());
        final int exitCode = ThanosCli.run(args, segmenter, () -> AppWindow.launch(segmenter));

        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    private static boolean opensTheWindow(String[] args) {
        return args.length > 0 && WINDOW_COMMAND.equals(args[0]);
    }
}

package thanos.cli;

import java.io.PrintWriter;
import picocli.CommandLine;
import thanos.engine.segmentation.Segmenter;

public final class ThanosCli {

    private ThanosCli() {}

    /**
     * @param windowLauncher opens the window, so this package never depends on it (ADR 0006)
     * @return the process exit code
     */
    public static int run(String[] arguments, Segmenter segmenter, Runnable windowLauncher) {
        return run(arguments, segmenter, windowLauncher, new PrintWriter(System.out, true), new PrintWriter(System.err, true));
    }

    static int run(
            String[] arguments, Segmenter segmenter, Runnable windowLauncher, PrintWriter output, PrintWriter errors) {
        final CommandLine commandLine = new CommandLine(new RootCommand())
                .addSubcommand(new CutCommand(segmenter))
                .addSubcommand(new UiCommand(windowLauncher))
                .setOut(output)
                .setErr(errors);

        return commandLine.execute(arguments);
    }
}

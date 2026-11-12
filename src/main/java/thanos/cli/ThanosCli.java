package thanos.cli;

import picocli.CommandLine;

import thanos.engine.segmentation.Segmenter;

import java.io.PrintWriter;

public final class ThanosCli {

    private ThanosCli() {}

    /**
     * @param windowLauncher opens the window, so this package never depends on it (ADR 0006)
     * @return the process exit code
     */
    public static int run(String[] arguments, Segmenter segmenter, Runnable windowLauncher) {
        final PrintWriter output = new PrintWriter(System.out, true);
        final PrintWriter errors = new PrintWriter(System.err, true);

        return create(segmenter, windowLauncher, output, errors).execute(arguments);
    }

    static CommandLine create(
            Segmenter segmenter, Runnable window, PrintWriter out, PrintWriter err) {
        final CommandLine commandLine = new CommandLine(new RootCommand());

        commandLine.addSubcommand(new CutCommand(segmenter));
        commandLine.addSubcommand(new UiCommand(window));
        commandLine.setOut(out);
        commandLine.setErr(err);

        return commandLine;
    }
}

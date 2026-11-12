package thanos.cli;

import picocli.CommandLine.Command;

@Command(name = "ui", description = "Open the window: scribble, snap, refine, export.")
final class UiCommand implements Runnable {

    private final Runnable windowLauncher;

    UiCommand(Runnable windowLauncher) {
        this.windowLauncher = windowLauncher;
    }

    @Override
    public void run() {
        windowLauncher.run();
    }
}

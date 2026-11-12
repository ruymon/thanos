package thanos.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;
import thanos.config.AppConfig;

/** {@code thanos} on its own: prints the help. */
@Command(
        name = AppConfig.COMMAND_NAME,
        version = AppConfig.APP_NAME + " " + AppConfig.VERSION,
        mixinStandardHelpOptions = true,
        description = {
            AppConfig.TAGLINE,
            "",
            "Removes the background of a photo by finding the minimum cut",
            "of a graph where every pixel is a vertex."
        },
        synopsisSubcommandLabel = "<command>",
        commandListHeading = "%nCommands:%n",
        optionListHeading = "%nOptions:%n",
        footer = {"", "Run 'thanos <command> --help' for the options of a command."})
final class RootCommand implements Runnable {

    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}

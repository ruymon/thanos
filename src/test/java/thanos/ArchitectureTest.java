package thanos;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Guards the dependency rules of ADR 0001, 0006 and 0007 by reading the imports of every source file. */
class ArchitectureTest {

    private static final Path SOURCES = Path.of("src/main/java/thanos");

    @Test
    void graphDependsOnNothingButTheJdk() throws IOException {
        assertImportsOnly("engine/graph", "java.util.", "thanos.engine.graph.");
    }

    @Test
    void segmentationDependsOnlyOnTheEngine() throws IOException {
        assertImportsOnly("engine/segmentation", "java.", "thanos.engine.", "thanos.config.");
    }

    @Test
    void imageDependsOnNothingButTheJdk() throws IOException {
        assertImportsOnly("engine/image", "java.", "javax.imageio.");
    }

    @Test
    void engineUsesNoLibraryAndNoFrontEnd() throws IOException {
        assertImportsOnly("engine", "java.", "javax.imageio.", "thanos.engine.", "thanos.config.");
    }

    @Test
    void commandLineDoesNotKnowTheWindow() throws IOException {
        assertNeverImports("cli", "thanos.ui.", "javax.swing.", "com.formdev.");
    }

    @Test
    void windowDoesNotKnowTheCommandLine() throws IOException {
        assertNeverImports("ui", "thanos.cli.", "picocli.", "thanos.engine.graph.");
    }

    private static void assertImportsOnly(String packageName, String... allowedPrefixes) throws IOException {
        for (final String imported : importsOf(packageName)) {
            final boolean allowed = Stream.of(allowedPrefixes).anyMatch(imported::startsWith);

            assertTrue(allowed, "Package " + packageName + " must not import " + imported);
        }
    }

    private static void assertNeverImports(String packageName, String... forbiddenPrefixes) throws IOException {
        for (final String imported : importsOf(packageName)) {
            final boolean forbidden = Stream.of(forbiddenPrefixes).anyMatch(imported::startsWith);

            assertTrue(!forbidden, "Package " + packageName + " must not import " + imported);
        }
    }

    private static List<String> importsOf(String packageName) throws IOException {
        try (Stream<Path> files = Files.walk(SOURCES.resolve(packageName))) {
            return files.filter(file -> file.toString().endsWith(".java"))
                    .flatMap(ArchitectureTest::linesOf)
                    .filter(line -> line.startsWith("import "))
                    .map(line -> line.replace("import static ", "").replace("import ", "").replace(";", "").trim())
                    .toList();
        }
    }

    private static Stream<String> linesOf(Path file) {
        try {
            return Files.readAllLines(file).stream();
        } catch (IOException failure) {
            throw new IllegalStateException(failure);
        }
    }
}

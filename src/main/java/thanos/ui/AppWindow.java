package thanos.ui;

import com.formdev.flatlaf.FlatLightLaf;

import thanos.config.AppConfig;
import thanos.config.EngineConfig;
import thanos.engine.image.ImageFiles;
import thanos.engine.segmentation.CutParameters;
import thanos.engine.segmentation.Label;
import thanos.engine.segmentation.MissingScribblesException;
import thanos.engine.segmentation.Scribbles;
import thanos.engine.segmentation.Segmenter;
import thanos.engine.segmentation.SnapResult;
import thanos.engine.segmentation.SnapStats;
import thanos.ui.components.Brush;
import thanos.ui.components.CutoutView;
import thanos.ui.components.ParameterSlider;
import thanos.ui.components.ScribbleCanvas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;

public final class AppWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String MAC_APP_NAME_PROPERTY = "apple.awt.application.name";
    private static final int SMOOTHNESS_MAX = 400;
    private static final int SMOOTHNESS_DEFAULT = (int) EngineConfig.DEFAULT_SMOOTHNESS;
    private static final int SENSITIVITY_MAX = 100;
    private static final int SENSITIVITY_DEFAULT = (int) EngineConfig.DEFAULT_EDGE_SENSITIVITY;
    private static final double MILLIS_PER_SECOND = 1000.0;
    private static final Dimension SNAP_BUTTON_SIZE = new Dimension(120, 56);
    private static final String PNG_EXTENSION = ".png";
    private static final Locale PORTUGUESE = Locale.forLanguageTag("pt-BR");

    private final transient Segmenter segmenter;

    private final ScribbleCanvas canvas = new ScribbleCanvas();
    private final CutoutView cutoutView = new CutoutView();
    private final JLabel status = new JLabel(" ");
    private final JButton snapButton = new JButton(Texts.SNAP);
    private final ParameterSlider smoothness;
    private final ParameterSlider sensitivity;

    private transient BufferedImage photo;
    private transient BufferedImage cutout;
    private String photoName = AppConfig.COMMAND_NAME;

    private AppWindow(Segmenter segmenter) {
        super(AppConfig.APP_NAME);
        this.segmenter = segmenter;
        this.smoothness = createSlider(Texts.SMOOTHNESS, 0, SMOOTHNESS_MAX, SMOOTHNESS_DEFAULT);
        this.sensitivity = createSlider(Texts.SENSITIVITY, 1, SENSITIVITY_MAX, SENSITIVITY_DEFAULT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Theme.WINDOW_WIDTH, Theme.WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        final JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Theme.BG_WINDOW);
        content.add(createToolbar(), BorderLayout.NORTH);
        content.add(createWorkspace(), BorderLayout.CENTER);
        content.add(createControls(), BorderLayout.SOUTH);
        setContentPane(content);
    }

    /** Opens the window. Returns at once: the window keeps the program alive. */
    public static void launch(Segmenter segmenter) {
        System.setProperty(MAC_APP_NAME_PROPERTY, AppConfig.APP_NAME);
        FlatLightLaf.setup();
        UIManager.put("defaultFont", Theme.FONT_BASE);

        SwingUtilities.invokeLater(() -> new AppWindow(segmenter).setVisible(true));
    }

    private JComponent createToolbar() {
        final JToggleButton subjectBrush = createBrushButton(Texts.SUBJECT, Brush.SUBJECT);
        final JToggleButton backgroundBrush = createBrushButton(Texts.BACKGROUND, Brush.BACKGROUND);
        final JToggleButton eraser = createBrushButton(Texts.ERASER, Brush.ERASER);

        final ButtonGroup brushes = new ButtonGroup();
        brushes.add(subjectBrush);
        brushes.add(backgroundBrush);
        brushes.add(eraser);
        subjectBrush.setSelected(true);

        final JPanel left = createRow(FlowLayout.LEFT);
        left.add(createButton(Texts.OPEN_IMAGE, this::openPhoto));
        left.add(Box.createHorizontalStrut(Theme.SPACE_MD));
        left.add(subjectBrush);
        left.add(backgroundBrush);
        left.add(eraser);
        left.add(createButton(Texts.CLEAR, this::clearScribbles));

        final JPanel right = createRow(FlowLayout.RIGHT);
        right.add(createButton(Texts.SAVE_SCRIBBLES, this::saveScribbles));
        right.add(createButton(Texts.EXPORT_PNG, this::exportCutout));

        final JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Theme.BG_PANEL);
        toolbar.setBorder(Theme.LINE_BELOW);
        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);

        return toolbar;
    }

    private JComponent createWorkspace() {
        final JPanel workspace = new JPanel(new GridLayout(1, 2, Theme.SPACE_MD, 0));
        workspace.setOpaque(false);
        workspace.setBorder(Theme.PADDING);
        workspace.add(createTitledPane(Texts.ORIGINAL_PANE, canvas));
        workspace.add(createTitledPane(Texts.CUTOUT_PANE, cutoutView));

        return workspace;
    }

    private JComponent createControls() {
        final JPanel sliders = new JPanel();
        sliders.setLayout(new BoxLayout(sliders, BoxLayout.Y_AXIS));
        sliders.setOpaque(false);
        sliders.add(smoothness);
        sliders.add(Box.createVerticalStrut(Theme.SPACE_SM));
        sliders.add(sensitivity);
        sliders.add(Box.createVerticalStrut(Theme.SPACE_SM));
        sliders.add(status);

        status.setForeground(Theme.TEXT_MUTED);
        status.setAlignmentX(LEFT_ALIGNMENT);
        smoothness.setAlignmentX(LEFT_ALIGNMENT);
        sensitivity.setAlignmentX(LEFT_ALIGNMENT);

        snapButton.setPreferredSize(SNAP_BUTTON_SIZE);
        snapButton.setBackground(Theme.ACCENT);
        snapButton.setForeground(Theme.ON_ACCENT);
        snapButton.setFont(Theme.FONT_BASE.deriveFont(Theme.SIZE_LARGE));
        snapButton.addActionListener(event -> snap());
        getRootPane().setDefaultButton(snapButton);

        final JPanel snapHolder = createRow(FlowLayout.RIGHT);
        snapHolder.add(snapButton);

        final JPanel controls = new JPanel(new BorderLayout(Theme.SPACE_LG, 0));
        controls.setBackground(Theme.BG_PANEL);
        controls.setBorder(new CompoundBorder(Theme.LINE_ABOVE, Theme.PADDING));
        controls.add(sliders, BorderLayout.CENTER);
        controls.add(snapHolder, BorderLayout.EAST);

        return controls;
    }

    private static JComponent createTitledPane(String title, JComponent body) {
        final JLabel heading = new JLabel(title);
        heading.setForeground(Theme.TEXT_MUTED);
        heading.setFont(Theme.FONT_BASE.deriveFont(Theme.SIZE_SMALL));
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, Theme.SPACE_SM, 0));

        body.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        final JPanel pane = new JPanel(new BorderLayout());
        pane.setOpaque(false);
        pane.add(heading, BorderLayout.NORTH);
        pane.add(body, BorderLayout.CENTER);

        return pane;
    }

    private static JPanel createRow(int alignment) {
        final JPanel row = new JPanel(new FlowLayout(alignment, Theme.SPACE_SM, Theme.SPACE_SM));
        row.setOpaque(false);

        return row;
    }

    private static JButton createButton(String text, Runnable action) {
        final JButton button = new JButton(text);
        button.addActionListener(event -> action.run());

        return button;
    }

    private ParameterSlider createSlider(String name, int minimum, int maximum, int initial) {
        return new ParameterSlider(name, minimum, maximum, initial, this::snapAgain);
    }

    private JToggleButton createBrushButton(String text, Brush brush) {
        final JToggleButton button = new JToggleButton(text);
        button.setForeground(colorOf(brush));
        button.addActionListener(event -> canvas.setBrush(brush));

        return button;
    }

    private void openPhoto() {
        final FileDialog dialog = new FileDialog(this, Texts.OPEN_IMAGE, FileDialog.LOAD);
        dialog.setVisible(true);

        if (dialog.getFile() == null) {
            return;
        }

        try {
            photo = ImageFiles.read(Path.of(dialog.getDirectory(), dialog.getFile()));
            photoName = stemOf(dialog.getFile());
            cutout = null;
            canvas.setPhoto(photo);
            cutoutView.setCutout(null);
            showStatus(Texts.HINT);
        } catch (IOException failure) {
            showError(Texts.CANNOT_OPEN);
        }
    }

    private void clearScribbles() {
        canvas.clearScribbles();
    }

    /** Runs the cut off the event thread, so the window stays responsive while it works. */
    private void snap() {
        final Optional<Scribbles> scribbles = canvas.findScribbles();

        if (photo == null || scribbles.isEmpty()) {
            showError(Texts.NO_PHOTO);
            return;
        }

        final BufferedImage snappedPhoto = photo;
        final int lambda = smoothness.value();
        final int sigma = sensitivity.value();
        final CutParameters parameters = new CutParameters(lambda, sigma);

        snapButton.setEnabled(false);
        showStatus(Texts.SNAPPING);

        new SwingWorker<Snapped, Void>() {
            @Override
            protected Snapped doInBackground() {
                final SnapResult result = segmenter.snap(snappedPhoto, scribbles.get(), parameters);

                return new Snapped(result.mask().cutOut(snappedPhoto), result.stats());
            }

            @Override
            protected void done() {
                snapButton.setEnabled(true);
                showSnapped(this);
            }
        }.execute();
    }

    private void showSnapped(SwingWorker<Snapped, Void> worker) {
        try {
            final Snapped snapped = worker.get();
            cutout = snapped.cutout();
            cutoutView.setCutout(cutout);
            showStatus(describe(snapped.stats()));
        } catch (ExecutionException failure) {
            showError(describe(failure.getCause()));
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    /** Moving a slider re-runs the cut, once there is a cutout to compare against. */
    private void snapAgain() {
        if (cutout != null && snapButton.isEnabled()) {
            snap();
        }
    }

    private void saveScribbles() {
        final Optional<Scribbles> scribbles = canvas.findScribbles();

        if (scribbles.isEmpty()) {
            showError(Texts.NO_PHOTO);
            return;
        }

        final BufferedImage image = scribbles.get().toImage();

        saveImage(image, photoName + Texts.SCRIBBLES_SUFFIX, Texts.SAVE_SCRIBBLES);
    }

    private void exportCutout() {
        if (cutout == null) {
            showError(Texts.NO_CUTOUT);
            return;
        }

        saveImage(cutout, photoName + AppConfig.CUTOUT_SUFFIX, Texts.EXPORT_PNG);
    }

    private void saveImage(BufferedImage image, String suggestedName, String title) {
        final FileDialog dialog = new FileDialog(this, title, FileDialog.SAVE);
        dialog.setFile(suggestedName);
        dialog.setVisible(true);

        if (dialog.getFile() == null) {
            return;
        }

        final Path file = Path.of(dialog.getDirectory(), withPngExtension(dialog.getFile()));

        try {
            ImageFiles.writePng(image, file);
            showStatus(String.format(Texts.SAVED, file));
        } catch (IOException failure) {
            showError(Texts.CANNOT_SAVE);
        }
    }

    private void showStatus(String message) {
        status.setForeground(Theme.TEXT_MUTED);
        status.setText(message);
    }

    private void showError(String message) {
        status.setForeground(Theme.TEXT_DANGER);
        status.setText(message);
    }

    private static String describe(SnapStats stats) {
        final double seconds = stats.elapsedMillis() / MILLIS_PER_SECOND;
        final String size = stats.workingWidth() + " × " + stats.workingHeight();

        final int vertices = stats.vertexCount();
        final int edges = stats.edgeCount();

        return String.format(PORTUGUESE, Texts.REPORT, seconds, vertices, edges, size);
    }

    private static String describe(Throwable failure) {
        if (!(failure instanceof MissingScribblesException missing)) {
            return Texts.SNAP_FAILED;
        }

        if (missing.missing() == Label.SUBJECT) {
            return Texts.MISSING_SUBJECT;
        }

        return Texts.MISSING_BACKGROUND;
    }

    private static Color colorOf(Brush brush) {
        return switch (brush) {
            case SUBJECT -> Theme.SUBJECT_BRUSH;
            case BACKGROUND -> Theme.BACKGROUND_BRUSH;
            case ERASER -> Theme.TEXT_PRIMARY;
        };
    }

    private static String withPngExtension(String fileName) {
        if (fileName.toLowerCase(Locale.ROOT).endsWith(PNG_EXTENSION)) {
            return fileName;
        }

        return fileName + PNG_EXTENSION;
    }

    private static String stemOf(String fileName) {
        final int extension = fileName.lastIndexOf('.');

        return extension > 0 ? fileName.substring(0, extension) : fileName;
    }

    private record Snapped(BufferedImage cutout, SnapStats stats) {}
}

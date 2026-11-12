package thanos.ui.components;

import thanos.ui.Theme;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public final class ParameterSlider extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int LABEL_WIDTH = 170;
    private static final int VALUE_WIDTH = 40;
    private static final int ROW_HEIGHT = 28;

    private final JSlider slider;
    private final JLabel value = new JLabel();

    /**
     * @param onRelease called once the user lets go of the slider at a new value
     */
    public ParameterSlider(String name, int minimum, int maximum, int initial, Runnable onRelease) {
        super(new BorderLayout(Theme.SPACE_MD, 0));
        setOpaque(false);

        final JLabel label = new JLabel(name);
        label.setPreferredSize(new Dimension(LABEL_WIDTH, ROW_HEIGHT));
        label.setForeground(Theme.TEXT_PRIMARY);

        value.setPreferredSize(new Dimension(VALUE_WIDTH, ROW_HEIGHT));
        value.setHorizontalAlignment(SwingConstants.RIGHT);
        value.setForeground(Theme.TEXT_MUTED);
        value.setText(String.valueOf(initial));

        slider = new JSlider(minimum, maximum, initial);
        slider.setOpaque(false);
        slider.addChangeListener(event -> {
            value.setText(String.valueOf(slider.getValue()));

            if (!slider.getValueIsAdjusting()) {
                onRelease.run();
            }
        });

        add(label, BorderLayout.WEST);
        add(slider, BorderLayout.CENTER);
        add(value, BorderLayout.EAST);
    }

    public int value() {
        return slider.getValue();
    }
}

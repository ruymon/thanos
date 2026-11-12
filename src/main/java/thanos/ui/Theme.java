package thanos.ui;

import java.awt.Color;
import java.awt.Font;

/** Colours, sizes and fonts of the window. */
public final class Theme {

    public static final Color BG_WINDOW = new Color(0xFAFAF7);
    public static final Color BG_PANEL = new Color(0xFFFFFF);
    public static final Color BG_CANVAS = new Color(0xF1F0EB);
    public static final Color BORDER = new Color(0xDEDCD3);
    public static final Color TEXT_PRIMARY = new Color(0x22252A);
    public static final Color TEXT_MUTED = new Color(0x7A7870);
    public static final Color TEXT_DANGER = new Color(0xB3261E);
    public static final Color ACCENT = new Color(0x22252A);
    public static final Color ON_ACCENT = new Color(0xFFFFFF);

    public static final Color SUBJECT = new Color(0xE5482D);
    public static final Color BACKGROUND = new Color(0x2D6CE5);

    public static final Color CHECKER_LIGHT = new Color(0xFFFFFF);
    public static final Color CHECKER_DARK = new Color(0xE4E2DA);
    public static final int CHECKER_SIZE = 12;

    public static final int SPACE_SM = 8;
    public static final int SPACE_MD = 12;
    public static final int SPACE_LG = 20;

    public static final int WINDOW_WIDTH = 1180;
    public static final int WINDOW_HEIGHT = 760;

    public static final float SIZE_SMALL = 12f;
    public static final float SIZE_BASE = 13f;
    public static final float SIZE_LARGE = 16f;

    public static final Font FONT_BASE = new Font(Font.SANS_SERIF, Font.PLAIN, (int) SIZE_BASE);

    private Theme() {}
}

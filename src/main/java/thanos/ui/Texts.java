package thanos.ui;

/** Every text the window shows, in Portuguese. The command line and the code stay in English. */
public final class Texts {

    public static final String OPEN_IMAGE = "Abrir imagem";
    public static final String SUBJECT = "Objeto";
    public static final String BACKGROUND = "Fundo";
    public static final String ERASER = "Borracha";
    public static final String CLEAR = "Limpar";
    public static final String SAVE_SCRIBBLES = "Salvar riscos";
    public static final String EXPORT_PNG = "Exportar PNG";
    public static final String SNAP = "Snap";

    public static final String ORIGINAL_PANE = "Original + riscos";
    public static final String CUTOUT_PANE = "Recorte";
    public static final String SMOOTHNESS = "Suavidade (λ)";
    public static final String SENSITIVITY = "Sensibilidade à borda (σ)";

    public static final String CANVAS_EMPTY = "Abra uma imagem para começar";
    public static final String CUTOUT_EMPTY = "Risque o objeto e o fundo, depois clique em Snap";

    public static final String HINT = "Risque o objeto e o fundo, depois clique em Snap.";
    public static final String SNAPPING = "Recortando…";
    public static final String REPORT = "Recorte em %.2f s · %,d vértices · %,d arestas · %s px";
    public static final String SAVED = "Salvo em %s";

    public static final String NO_PHOTO = "Abra uma imagem primeiro.";
    public static final String NO_CUTOUT = "Clique em Snap antes de exportar.";
    public static final String MISSING_SUBJECT = "Risque o objeto antes de clicar em Snap.";
    public static final String MISSING_BACKGROUND = "Risque o fundo antes de clicar em Snap.";
    public static final String CANNOT_OPEN = "Não foi possível abrir a imagem. Use PNG ou JPEG.";
    public static final String CANNOT_SAVE = "Não foi possível salvar o arquivo.";
    public static final String SNAP_FAILED = "O recorte falhou. Tente de novo.";

    public static final String SCRIBBLES_SUFFIX = "-riscos.png";

    private Texts() {}
}

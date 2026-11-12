package thanos.engine.segmentation;

public final class MissingScribblesException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    private final Label missing;

    public MissingScribblesException(Label missing) {
        super("Scribble over the " + missing.name().toLowerCase() + " before snapping");
        this.missing = missing;
    }

    public Label missing() {
        return missing;
    }
}

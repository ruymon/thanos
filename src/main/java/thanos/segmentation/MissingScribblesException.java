package thanos.segmentation;

/** Thrown when a snap is asked for without at least one subject and one background scribble. */
public final class MissingScribblesException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public MissingScribblesException(Label missing) {
        super("Scribble over the " + missing.name().toLowerCase() + " before snapping");
    }
}

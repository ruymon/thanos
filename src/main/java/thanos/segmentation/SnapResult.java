package thanos.segmentation;

/**
 * @param mask which pixels of the photo are subject, at the photo's size
 * @param stats what the snap cost
 */
public record SnapResult(Mask mask, SnapStats stats) {}

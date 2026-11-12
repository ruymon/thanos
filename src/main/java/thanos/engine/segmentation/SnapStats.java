package thanos.engine.segmentation;

/**
 * What one snap cost.
 *
 * @param workingWidth width of the copy the cut ran on
 * @param workingHeight height of the copy the cut ran on
 * @param vertexCount vertices of the flow network: one per pixel, plus source and sink
 * @param edgeCount edges of the flow network
 * @param cutValue total capacity of the minimum cut, equal to the maximum flow
 * @param elapsedMillis wall-clock time of the whole snap
 */
public record SnapStats(
        int workingWidth, int workingHeight, int vertexCount, int edgeCount, long cutValue, long elapsedMillis) {}

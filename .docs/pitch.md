# Pitch

**Background removal by minimum cut on graphs**

## Problem

Automatically separate a person or object from the background of a photo, as a phone's "remove background" does. The user lightly scribbles over the subject and the background, and the system produces the cutout.

## Graph model

- Each pixel is a vertex, connected to its 4 neighbours.
- Those edges weigh more between similar colours and less where there is contrast (the edges of the picture).
- Two special vertices, **S (subject)** and **T (background)**, connect to every pixel with a weight proportional to how much the pixel's colour resembles each scribble.
- The ideal cutout is the **minimum s-t cut** of this graph: it separates subject from background by cutting the cheapest edges, which are the real edges of the image.

## Algorithms and structures

- **Maximum flow** with **Dinic's algorithm**, using the **max-flow = min-cut** theorem
- **BFS on the residual graph** to extract the cut
- A graph of about 100k vertices and 500k edges, implemented **with no external libraries**, on an array-based adjacency list for performance

## Deliverables

- Java (Swing) application: open an image, mark subject and background, cut, refine, export a PNG with a transparent background
- Checks of the max-flow on small graphs that can be verified by hand
- Written survey on applications of graphs

## Why this is a graph problem, not image processing

The image only supplies the weights. The whole problem is solved as network flow.

## Reference

Boykov, Y. & Jolly, M.-P. (2001). *Interactive Graph Cuts for Optimal Boundary & Region Segmentation of Objects in N-D Images*. ICCV.

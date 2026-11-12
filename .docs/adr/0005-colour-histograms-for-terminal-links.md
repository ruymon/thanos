---
status: accepted
---

# Terminal links come from colour histograms of the scribbles

To decide how strongly a pixel belongs to the subject or to the background, we build one colour histogram from each set of scribbles and use the negative log of the pixel's probability under each as the terminal link capacity. This is the model of the original paper (Boykov & Jolly, 2001). We chose it over the Gaussian mixture models of GrabCut because a histogram is a counting array: no iteration, no numerical fitting, and it can be explained in one sentence.

## Considered options

- **Gaussian mixture models (GrabCut)**: better on photos with many colours, and they allow a rectangle instead of scribbles. They need an iterative fit (k-means plus re-estimation), which is a second algorithm to write, tune and explain, and it is not a graph algorithm.
- **Hard constraints only** (no colour model, scribbled pixels pinned and nothing else): the simplest. The cut then leans entirely on neighbour links and needs many more scribbles.
- **Histograms (chosen)**.

## Consequences

- A subject and a background with similar colours need more scribbles.
- Colours are quantised into bins. The number of bins is a configuration constant.

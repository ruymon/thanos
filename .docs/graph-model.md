# Graph model

How a photo becomes a flow network, and why its minimum cut is the cutout.

## Vertices

- One vertex per pixel of the downscaled image (`width × height`)
- Two terminals: **S** (source, subject) and **T** (sink, background)

A 400 × 300 image gives 120,002 vertices.

## Edges

### Neighbour links (pixel ↔ pixel)

Each pixel is connected to its 4 neighbours (up, down, left, right). The capacity says how expensive it is to separate the two pixels:

```
capacity(p, q) = λ · exp( −‖colour(p) − colour(q)‖² / (2σ²) )
```

- Similar colours → high capacity → expensive to cut
- Different colours → low capacity → cheap to cut

So cheap edges sit on the real edges of the picture.

### Terminal links (S → pixel, pixel → T)

Each pixel is connected to both terminals. The capacities come from the user's scribbles:

1. Build one colour histogram from subject scribbles and one from background scribbles.
2. For each pixel, estimate how likely its colour is under each histogram.

```
capacity(S, p) = −log P(colour(p) | background)
capacity(p, T) = −log P(colour(p) | subject)
```

A pixel that looks like the subject is strongly tied to S and weakly tied to T.

Scribbled pixels are hard constraints:

| Pixel | S → p | p → T |
|---|---|---|
| Scribbled as subject | ∞ | 0 |
| Scribbled as background | 0 | ∞ |

## The cut

An **s-t cut** removes edges until S can no longer reach T. Its cost is the sum of the removed capacities.

The **minimum** cut pays as little as possible, so it:

- avoids cutting between similar neighbours (stays out of flat regions),
- cuts where colours change (follows the edge),
- keeps each pixel on the side its colour looks like.

## Solving it

**Max-flow min-cut theorem:** the maximum flow from S to T equals the capacity of the minimum cut.

1. Run **Dinic's algorithm** (BFS level graph + blocking flow by DFS) to find the maximum flow.
2. Run a BFS from S on the **residual graph**.
3. Pixels reached are subject. The rest are background.

## Size

For a `w × h` image:

- Vertices: `w·h + 2`
- Neighbour links: about `2·w·h` (stored in both directions)
- Terminal links: `2·w·h`

At 400 × 300 that is roughly 120k vertices and 480k edges. The graph is stored in flat arrays (forward-star adjacency list), with each edge's reverse at index `e ^ 1`, so the residual graph costs no extra objects.

## Reference

Boykov, Y. & Jolly, M.-P. (2001). *Interactive Graph Cuts for Optimal Boundary & Region Segmentation of Objects in N-D Images*. ICCV.

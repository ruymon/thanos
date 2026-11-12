---
status: accepted
---

# Dinic's algorithm, behind a `MaxFlowSolver` interface

Maximum flow is computed with Dinic's algorithm (BFS level graph, then blocking flow by DFS). `segmentation` depends on the `MaxFlowSolver` interface, not on Dinic. We chose Dinic because it is fast enough on a pixel grid, fits in about 150 lines, and can be explained on one slide. The interface exists so a second algorithm can be added and timed against it without touching the caller.

## Considered options

- **Edmonds-Karp** (`O(V·E²)`): the simplest, and the one taught first. Too slow at 120,000 vertices. Kept as the planned baseline for a timing comparison.
- **Boykov-Kolmogorov**: designed for exactly this kind of graph and usually the fastest in practice. Much harder to implement and to explain, with a worse theoretical bound.
- **Push-relabel**: fast, but its behaviour is hard to show step by step in a presentation.
- **Dinic (chosen)** (`O(V²·E)`, far better in practice on grids).

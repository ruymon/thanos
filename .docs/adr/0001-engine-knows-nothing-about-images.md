---
status: accepted
---

# The graph engine knows nothing about images

The `graph` package solves maximum flow on a flow network and nothing else: no pixels, no colours, no Swing. Dependencies run one way only, `cli` / `ui` → `segmentation` → `graph`, and `image` is a leaf used by the front ends and by `segmentation`. We chose this because the assignment is graded on the graph, so the graph must be readable and testable on its own, with small hand-checked networks, before any image exists.

## Consequences

- The three packages live together under `engine/`, apart from the two front ends.
- `graph` can be reused for any other flow problem (matching, scheduling) without touching it.
- Anything that needs to know "this vertex is the pixel at (x, y)" lives in `segmentation`, never in `graph`.
- A class in `graph` that imports `java.awt` or `thanos.engine.segmentation` is a bug.

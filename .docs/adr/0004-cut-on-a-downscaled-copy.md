---
status: accepted
---

# The cut runs on a downscaled copy of the photo

A phone photo has around 12 million pixels, which would be a graph of 12 million vertices. Before building the graph we scale the photo so its longest side is at most 400 px, run the cut there, and scale the mask back up to the original resolution. We chose this because a snap must finish in about two seconds to work as a live demo, and the edge of a subject is still well defined at 400 px.

## Consequences

- The edge of the cutout is only as precise as the downscaled image: fine detail (hair, fur) is lost.
- The limit is one configuration constant, so it can be raised on a faster machine.
- The exported cutout keeps the original resolution. Only the mask is computed small.

---
status: accepted
---

# The flow network is stored in flat arrays, not objects

A 400 px image is about 120,000 vertices and 480,000 directed edges. We store the network as a forward-star adjacency list on primitive arrays (`head`, `next`, `to`, `capacity`) and add every edge together with its reverse, so the reverse of edge `e` is always `e ^ 1`. The textbook `List<List<Edge>>` was rejected: half a million `Edge` objects cost memory, garbage collection and pointer chasing, and the cut would no longer feel live in a demo.

## Considered options

- **`List<List<Edge>>` with an `Edge` class**: the most readable, and what the course material shows. Too slow and heavy at this size.
- **Adjacency matrix**: `120,000²` cells. Not possible.
- **Flat arrays (chosen)**: one `int` index per edge, residual capacity updated in place, reverse edge found with one XOR.

## Consequences

- The arrays are private. Callers see `addEdge`, vertex and edge counts, and an iteration API, never the indices' layout.
- Edges must always be added in pairs. `addEdge` is the only way to add one.

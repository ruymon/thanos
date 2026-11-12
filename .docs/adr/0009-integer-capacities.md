---
status: accepted
---

# Capacities are integers

The weights of the model are real numbers (an exponential and a logarithm), but the flow network stores `int` capacities: each weight is multiplied by a fixed scale (100) and rounded. We chose this because integer arithmetic is exact, so "is there capacity left on this edge?" is a plain `> 0` with no tolerance to tune, and the algorithm is guaranteed to terminate. The total flow is accumulated in a `long`.

## Consequences

- Two weights closer than 0.01 become the same capacity. That is far below what changes a cut.
- A scribbled pixel is pinned with a very large finite capacity, not infinity. It is sized so that adding flow to it can never overflow an `int`.

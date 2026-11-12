# Thanos

One snap and the background is gone.

Desktop app that removes the background of a photo. You scribble over the subject and over the background, and Thanos cuts the image along the subject's edge.

Under the hood the image is a graph: every pixel is a vertex, and the cutout is the **minimum s-t cut** of that graph, computed with **maximum flow (Dinic's algorithm)**.

Built with plain Java + Swing as a university project (ECM306, Advanced Topics in Data Structures, Instituto Mauá de Tecnologia).

---

## How it works

1. The image is scaled down and every pixel becomes a vertex connected to its 4 neighbours.
2. Edges between similar colours are expensive to cut. Edges across a colour change are cheap.
3. Two extra vertices are added: **S** (subject) and **T** (background). Every pixel is connected to both, weighted by how much its colour looks like each scribble.
4. Maximum flow from S to T finds the cheapest set of edges that separates them. That set runs along the subject's edge.
5. Pixels still reachable from S are the subject. Everything else becomes transparent.

Full model in [.docs/graph-model.md](.docs/graph-model.md).

---

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| Engine (graph, segmentation, image) | JDK only, no libraries |
| Command line | picocli |
| Window | Swing (hand-coded) + FlatLaf |
| Tests | JUnit 5 |
| Build | `javac`, no build tool |

Libraries are allowed only at the edges. See [ADR 0007](.docs/adr/0007-libraries-only-at-the-edges.md).

---

## Usage

```
thanos                 Print the help
thanos ui              Open the window
thanos cut <photo>     Cut from the terminal
```

Both tuning parameters have two names:

| What it does | Option | Maths name |
|---|---|---|
| How much the cut resists separating neighbours | `--smoothness` | `--lambda` |
| How small a colour change counts as an edge | `--edge-sensitivity` | `--sigma` |

---

## Getting started

_Coming with the first build._

---

## Docs

| File | What it covers |
|---|---|
| [.docs/scope.md](.docs/scope.md) | Problem, scope, non-goals, constraints |
| [.docs/graph-model.md](.docs/graph-model.md) | How an image becomes a flow network |
| [.docs/roadmap.md](.docs/roadmap.md) | Ordered build plan |
| [.docs/pitch.md](.docs/pitch.md) | One-page technical pitch |
| [.docs/adr/](.docs/adr/) | Architecture decisions and why each was taken |
| [GLOSSARY.md](GLOSSARY.md) | The words used in the code, the CLI, the UI and the docs |

---

## Reference

Boykov, Y. & Jolly, M.-P. (2001). *Interactive Graph Cuts for Optimal Boundary & Region Segmentation of Objects in N-D Images*. ICCV.

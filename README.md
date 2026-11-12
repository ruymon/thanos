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
| Engine (`engine/`: graph, segmentation, image) | JDK only, no libraries |
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

Requires Java 17 or newer. Nothing else to install: the three jars are in `lib/`.

```bash
./build
```

```bash
./thanos
```

Try it on the sample photo, from the terminal:

```bash
./thanos cut samples/astronaut/photo.png --scribbles samples/astronaut/scribbles.png --out astronaut.png
```

Or in the window:

```bash
./thanos ui
```

1. **Open image**.
2. Drag over the subject with the **Subject** brush and over the background with the **Background** brush. A few rough strokes are enough.
3. **Snap**.
4. Missing a piece? Scribble over it and snap again. Moving a slider re-runs the cut.
5. **Export PNG**.

Run the tests:

```bash
./test
```

To call `thanos` from any folder, link the launcher into a folder on your `PATH`:

```bash
ln -s "$(pwd)/thanos" /usr/local/bin/thanos
```

---

## Project structure

```
thanos/
├── src/main/java/thanos/
│   ├── Main.java              # Wires the engine to the two front ends
│   ├── config/                # Constants: working size, defaults, names
│   ├── engine/
│   │   ├── graph/             # Flow network, Dinic, Edmonds-Karp, minimum cut
│   │   ├── segmentation/      # Photo + scribbles + parameters → mask
│   │   └── image/             # Read, write and resize images
│   ├── cli/                   # `thanos` and `thanos cut` (picocli), in English
│   └── ui/                    # `thanos ui` (Swing + FlatLaf), in Portuguese
├── src/test/java/thanos/      # JUnit tests, including the dependency rules
├── samples/                   # Demo photos, each with its scribbles, cutout and command
├── lib/                       # picocli, FlatLaf, JUnit
└── .docs/                     # Scope, graph model, decisions, report, slides
```

Dependencies point one way: `cli` / `ui` → `segmentation` → `graph`. The `graph` package knows nothing about images, and a test fails the build if that ever changes.

---

## Docs

| File | What it covers |
|---|---|
| [.docs/scope.md](.docs/scope.md) | Problem, scope, non-goals, constraints |
| [.docs/graph-model.md](.docs/graph-model.md) | How an image becomes a flow network |
| [.docs/roadmap.md](.docs/roadmap.md) | Ordered build plan |
| [.docs/pitch.md](.docs/pitch.md) | One-page technical pitch |
| [.docs/adr/](.docs/adr/) | Architecture decisions and why each was taken |
| [.docs/report/relatorio.pdf](.docs/report/relatorio.pdf) | Written report, in Portuguese: graphs and their applications, the model, the implementation |
| [.docs/slides/](.docs/slides/) | Class presentation as PowerPoint, PDF and a self-contained web page |
| [GLOSSARY.md](GLOSSARY.md) | The words used in the code, the CLI, the UI and the docs |

---

## Reference

Boykov, Y. & Jolly, M.-P. (2001). *Interactive Graph Cuts for Optimal Boundary & Region Segmentation of Objects in N-D Images*. ICCV.

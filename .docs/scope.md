# Scope

## Context

- **Course:** ECM306, Advanced Topics in Data Structures (Prof. Calvetti), Instituto Mauá de Tecnologia
- **Assignment:** "Graphs and their applications": a written survey plus a Java application that solves a problem with a graph
- **Deadline and presentation:** 12 Nov 2026, 07:40, delivered on Canvas (each group member submits a copy)

## Problem

Separate a person or object from the background of a photo, the way a phone's "remove background" does. The user gives a rough hint (a few scribbles) and the app finds the exact edge.

## Why this project

The idea was picked against four criteria:

1. **The graph is the engine.** Without the graph there is no project. The image only supplies the edge weights.
2. **It looks like a real product**, not a classroom demo.
3. **Anyone understands the problem** in five seconds, and the result is visible.
4. **The code has technical depth**: a flow network with ~100k vertices, a residual graph, and a classic theorem (max-flow = min-cut).

Ideas considered and dropped: election analysis (statistics, not graphs), Huffman compression (a tree topic), Shazam-style fingerprinting (hash tables and signal processing), word ladder (BFS only), automatic DJ set ordering (travelling salesman, kept as plan B).

## In scope

- Open an image from disk
- Scribble subject (one colour) and background (another colour)
- Cut: build the graph, run max-flow, extract the min-cut
- Show the result over a transparency checkerboard
- Refine: add scribbles and cut again
- Export a PNG with a transparent background

## Out of scope

- Neural networks or any learned model
- Libraries inside the engine (graph, segmentation, image)
- Full-resolution cuts (the cut runs on a downscaled copy, at most 400 px on the longest side)
- Video, batch processing, undo history
- Soft edges (hair, fur): the cut is binary

## Constraints

- The engine is plain Java (JDK only). Libraries are allowed only at the edges: picocli for the command line, FlatLaf for the window, JUnit for tests
- Two front ends over one engine: a command line and a window
- Both tuning parameters (smoothness, edge sensitivity) are adjustable from both front ends
- No build tool: compiles with `javac`, runs through a `thanos` launcher script
- A cut must finish in about 2 seconds on a 400 px image, so the demo feels live

## Principle

The basics, done well. Clean architecture, small classes with one job each, and a graph engine that knows nothing about images.

## Demo

Take a photo in class, scribble over it, cut it live, export the PNG.

# Roadmap

Ordered build plan. Each step is usable on its own before the next starts.

## 1. Setup
- [x] Repository, README, docs
- [ ] Package structure and compile/run commands
- [ ] Sample images with good contrast in `samples/`

## 2. Graph
- [ ] Flow network on flat arrays (forward-star adjacency, reverse edge at `e ^ 1`)
- [ ] Add edges with capacity in one or both directions

## 3. Maximum flow
- [ ] Dinic: BFS level graph + DFS blocking flow
- [ ] Minimum cut: BFS on the residual graph from S
- [ ] Checks on small graphs with known answers

## 4. Image → graph
- [ ] Load image and downscale to at most 400 px
- [ ] Neighbour links from colour difference
- [ ] Colour histograms from the scribbles
- [ ] Terminal links from the histograms, hard constraints on scribbled pixels
- [ ] Mask from the cut, scaled back to the original resolution

## 5. Interface
- [ ] Window: open image
- [ ] Brushes: subject, background, eraser
- [ ] Cut button, result over a checkerboard
- [ ] Refine: more scribbles, cut again
- [ ] Export PNG with transparency

## 6. Tuning
- [ ] Calibrate `λ` and `σ` on the sample images
- [ ] Cut time under 2 s at 400 px

## 7. Delivery
- [ ] README: how to run, screenshots
- [ ] Written survey: "Graphs and their applications"
- [ ] Slides: image → pixels → graph → cut
- [ ] Live demo rehearsal

## Stretch (only if everything above is done)
- [ ] Edmonds-Karp as a baseline, timed against Dinic
- [ ] Replace the background with another image

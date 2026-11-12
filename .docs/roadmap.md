# Roadmap

Ordered build plan. Each step is usable on its own before the next starts.

## 1. Setup
- [x] Repository, README, docs
- [x] Package structure and compile/run commands
- [x] Sample images with good contrast in `samples/`

## 2. Graph
- [x] Flow network on flat arrays (forward-star adjacency, reverse edge at `e ^ 1`)
- [x] Add edges with capacity in one or both directions

## 3. Maximum flow
- [x] Dinic: BFS level graph + DFS blocking flow
- [x] Minimum cut: BFS on the residual graph from S
- [x] Checks on small graphs with known answers

## 4. Image → graph
- [x] Load image and downscale to at most 400 px
- [x] Neighbour links from colour difference
- [x] Colour histograms from the scribbles
- [x] Terminal links from the histograms, hard constraints on scribbled pixels
- [x] Mask from the cut, scaled back to the original resolution

## 5. Command line
- [x] `thanos` launcher and `build` script
- [x] `thanos` alone prints the help
- [x] `thanos cut <photo> --scribbles <file> --out <file>`
- [x] `--smoothness` / `--lambda` and `--edge-sensitivity` / `--sigma`

## 6. Window
- [x] `thanos ui`: open image
- [x] Brushes: subject, background, eraser
- [x] Sliders: smoothness and edge sensitivity
- [x] Snap button, cutout over a checkerboard, cut time
- [x] Refine: more scribbles, snap again
- [x] Export PNG with transparency, save scribbles

## 7. Tuning
- [x] Calibrate `λ` and `σ` on the sample images
- [x] Cut time under 2 s at 400 px

## 8. Delivery
- [x] README: how to run
- [x] Written report with the survey "Graphs and their applications": `.docs/report/`
- [x] Slides: image → pixels → graph → cut: `.docs/slides/`
- [ ] Live demo rehearsal

## Stretch (only if everything above is done)
- [x] Edmonds-Karp as a second solver, cross-checked against Dinic
- [ ] Time Edmonds-Karp against Dinic on growing images
- [ ] Replace the background with another image

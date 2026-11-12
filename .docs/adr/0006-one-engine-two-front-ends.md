---
status: accepted
---

# One engine, two front ends

Thanos has a command-line front end and a window front end, and both call the same entry point in `segmentation` with the same inputs: the photo, the scribbles, and a parameters value (smoothness and edge sensitivity). Neither front end knows the other exists. We chose this because the two serve different moments of the presentation: the terminal proves the engine stands on its own, and the window is the live demo. If the window fails, the terminal still runs.

A single launcher, `thanos`, dispatches to them as subcommands: `thanos` alone prints the help, `thanos cut` runs in the terminal, `thanos ui` opens the window.

## Consequences

- Both parameters have two names everywhere they are exposed. The user-facing name says what it does (`--smoothness`, `--edge-sensitivity`, and the slider labels). The maths name matches the formula on the slides (`--lambda`, `--sigma`). They are aliases of the same option.
- In the terminal there is no way to draw, so scribbles arrive as an image file. The window can save its scribbles in that format.
- Default parameter values live in one place and are shared by both front ends.

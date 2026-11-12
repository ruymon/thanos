# Thanos

Background removal for photos, solved as a minimum cut on a graph of pixels. This glossary fixes the words used in the code, the CLI, the UI and the docs.

## Language

### What the user sees

**Subject**:
The person or object the user wants to keep.
_Avoid_: Foreground, object, target

**Background**:
Everything in the photo that is not the subject.
_Avoid_: Backdrop, rest

**Scribble**:
A rough stroke the user draws to say "this is subject" or "this is background".
_Avoid_: Seed, stroke, mark, annotation

**Snap**:
One run of the cut: scribbles and parameters in, mask out.
_Avoid_: Run, segment, process

**Mask**:
The answer of a snap: for every pixel, whether it belongs to the subject.
_Avoid_: Selection, alpha, matte

**Cutout**:
The original photo with every background pixel made transparent.
_Avoid_: Result, output image

### What the user tunes

**Smoothness**:
How much the cut resists separating neighbouring pixels. Written λ (lambda) in the maths.
_Avoid_: Weight, strength, regularisation

**Edge sensitivity**:
How small a colour change already counts as an edge. Written σ (sigma) in the maths.
_Avoid_: Threshold, contrast, tolerance

### The graph

**Flow network**:
A directed graph whose edges carry capacities, with one source and one sink.
_Avoid_: Graph (when capacities matter), net

**Source**:
The terminal vertex that stands for the subject. Written S.
_Avoid_: Start, origin

**Sink**:
The terminal vertex that stands for the background. Written T.
_Avoid_: Target, end, drain

**Neighbour link**:
An edge between two adjacent pixels. Its capacity is the cost of putting them on different sides.
_Avoid_: n-link, boundary edge, smoothness edge

**Terminal link**:
An edge between a pixel and the source or the sink. Its capacity is the cost of denying the pixel that side.
_Avoid_: t-link, region edge, data edge

**Residual graph**:
The flow network after flow has been pushed: what capacity is still free on every edge.
_Avoid_: Leftover graph

**Minimum cut**:
The cheapest set of edges whose removal disconnects the source from the sink.
_Avoid_: Min-cut (in prose), best cut, segmentation

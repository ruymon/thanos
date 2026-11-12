---
status: accepted
---

# Libraries only at the edges

The engine (`graph`, `segmentation`, `image`) uses the JDK and nothing else. Libraries are allowed only in the outer packages, where they replace boilerplate that has nothing to do with graphs. We chose this because the engine is what the course grades, so every line of it must be ours, while hand-writing an argument parser or a look-and-feel would add code without adding anything to learn.

| Library | Version | Used only in | Why |
|---|---|---|---|
| picocli | 4.7.7 | `cli` | Subcommands, option aliases, validation and `--help` from annotations |
| FlatLaf | 3.6.1 | `ui` | A modern look for Swing with one line of setup |
| JUnit Platform Console Standalone | 5.x | `src/test` | Standard test runner as a single jar. Not shipped with the app |

## Considered options

- **No libraries at all**: the original plan. The parser and the test runner would be hand-written, and the window would have the default Swing look.
- **Libraries anywhere**: a graph or image library would solve the assignment for us.
- **Libraries at the edges only (chosen)**.

## Consequences

- An import of `picocli` outside `cli`, or of `com.formdev` outside `ui`, is a bug.
- The engine and its tests compile and run without picocli or FlatLaf on the classpath.

---
status: accepted
---

# No build tool: `javac`, a `lib/` folder and a launcher script

The project compiles with `javac` into `out/`, keeps its few jars in `lib/`, and runs through a small `thanos` launcher script. We chose this over Maven or Gradle because there are three jars and no release process, and anyone grading the project can read one compile command and understand the whole build. It is the same setup as our previous course project.

## Consequences

- Jars are committed to the repository, so a fresh clone builds offline.
- The launcher is a shell script: it runs on macOS and Linux. Windows needs a `.bat` twin if someone presents from one.
- If the project ever grows a second module or a real dependency tree, this decision should be revisited.

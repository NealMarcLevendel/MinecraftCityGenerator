# CLAUDE.md — io.levendel.Visualizer

Guidance for AI assistants working in this package.

## Scope rule

- **Only edit files inside `src/main/java/io/levendel/Visualizer/`.** Anything in this package may be changed freely.
- **Do not edit anything outside it** (`io.levendel.Core`, `io.levendel.Minecraft`, `build.gradle`, `gradle.properties`, …) unless the user explicitly asks. When something outside is broken, explain the fix and let the user make it.
- When you add, rewrite or substantially change a file here, update the AI-usage table in `README.md`.

## What this package is

A standalone Swing tool (no Minecraft needed) for watching the street-growing simulation step by step on sample terrain. It shows the terrain, street nodes, their connections, and the agents.

| File | Role |
|---|---|
| `CityVisualizer.java` | Entry point (`main`). Window, toolbar (simulation dropdown, Step, Step ×10, Play/Pause, Reset, Nodes/Contours toggles), status bar. Also `--snapshot` headless mode. |
| `CityPanel.java` | Rendering: cached terrain image (colour by height + hillshade + contours), edges, nodes (junctions = degree ≥ 3 in red), agents (blue), last step's edges (orange), mouse-hover readout. |
| `StreetSimulation.java` | Interface the panel draws from: `step()`, `getElevation()`, `getConnections()`, `getAgentHeads()`, `getLastStepEdges()`, `getStepCount()`. |
| `StreetGrowerSimulation.java` | Adapter around the real `io.levendel.Core.StreetGrower` + `StreetMap`. Attractors come from a `WeightedStreetAttractorProvider` and cullers from a `StreetCullerProvider`; the default constructor uses Core's `HardcodedWeightedStreetAttractorProvider` and `HardcodedStreetCullerProvider`, and other providers can be passed in. Snapshots the map each step to find new edges; reads `grower.agents` (public). Catches exceptions from Core and exposes them via `getLastError()` so the window shows them instead of crashing. |
| `ConstantStreetAttractor.java` | Placeholder `StreetAttractor` (elevation-aware signature) that returns a constant (1.0 by default). Not used any more: the visualizer now takes attractors from `HardcodedWeightedStreetAttractorProvider`. |
| `SampleStreetSimulation.java` | Self-contained reference simulation following the same design as `StreetGrower` (relative-angle proposals, slope/straightness/spacing/bounds attractors, snapping to junctions, branching). Useful for comparison. |
| `SampleElevationProvider.java` | Perlin-noise terrain for testing. |
| `ElevationPanel.java` | Older terrain-only panel. Not used by `CityVisualizer` any more. |

## Conventions

- Street coordinates are **array-relative** (0..width-1, 0..height-1), matching `Elevation.get(x, z)`. Use `elevation.offsetX/offsetZ` only for world coordinates.
- The panel must only depend on `StreetSimulation`. New simulations go behind that interface and get an entry in `CityVisualizer.Mode`.
- Keep drawing code from mutating simulation state. `StreetGrowerSimulation` hands the panel a copy of the map for that reason.
- The project targets Java 17 (`--release 17`). Records, switch expressions and `instanceof` patterns are fine.

## Running

From the project root:

```bash
./gradlew runVis
```

Without Gradle (only needs Core + this package):

```bash
javac --release 17 -d build/vis $(find src/main/java/io/levendel/Core src/main/java/io/levendel/Visualizer -name '*.java')
java -cp build/vis io.levendel.Visualizer.CityVisualizer
```

Headless check, useful for verifying changes without a display:

```bash
java -Djava.awt.headless=true -cp build/vis io.levendel.Visualizer.CityVisualizer --snapshot out.png 80 grower   # or: sample
```

It prints a status line (nodes / edges / junctions, or the Core error) and writes the PNG. Look at the image after changes to rendering or simulation code.

## Known state of Core (as of 2026-11-14)

- `StreetGrower`'s constructor takes `(Elevation, WeightedStreetAttractorProvider, StreetCullerProvider, List<Proposal>, int numAgents, StreetMap, int seed)`.
- `StreetAttractor.evaluate` and `StreetCuller.evaluate` both take `(StreetNode current, StreetNode proposed, StreetMap streetMap, Elevation elevation)`. Attractors return a score; cullers return a boolean. The cullers' names (`OutOfBoundsStreetCuller`, `SameNodeStreetCuller`) mean "true = remove this proposal".
- `HardcodedStreetCullerProvider` returns `SameNodeStreetCuller` and `OutOfBoundsStreetCuller`.
- **Bug (left for the author):** `StreetGrower.cull` calls `evaluate(n, head, ...)` (arguments swapped) and removes proposals where the culler returns *false* (inverted). Every proposal gets culled, `decide` gets an empty map, and `rng.nextInt(0)` throws "bound must be positive" on the first step. The fix is `proposedNodes.removeIf(n -> streetCuller.evaluate(head, n, streetMap, elevation));`. With that change the grower runs normally.
- `HardcodedWeightedStreetAttractorProvider` returns a constant 1.0 attractor plus `AngleStreetAttractor` (prefers ~22.5° turns). Neither uses elevation yet.
- Once culling works, out-of-bounds proposals are removed before attractors run, so attractors can call `Elevation.get` on rounded proposal coordinates. Proposals are doubles: round and clamp (see `SampleStreetSimulation.heightAt(Elevation, StreetNode)`).
- If every proposal is culled, `StreetGrower.decide` will still throw. Core may want to handle an empty result (e.g. leave the agent where it is).
- `StreetGrower.evaluate()` divides by the number of attractors, not the sum of weights.
- `Proposal.angle` is absolute, not relative to the agent's heading. `SampleStreetSimulation` uses relative angles.

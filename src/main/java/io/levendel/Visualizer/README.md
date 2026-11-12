# io.levendel.Visualizer

A standalone desktop tool for watching the city generator's street-growing simulation, one step at a time, on sample terrain. It is a development aid and is not part of the Minecraft mod at runtime.

## Running

```bash
./gradlew runVis
```

Use the **Simulation** dropdown to switch between:
- **StreetGrower (Core):** the real `io.levendel.Core.StreetGrower`.
- **Sample simulation:** a self-contained reference implementation.

**Controls:**
- **Step** (or Enter) advances one step; **Step ×10** advances ten.
- **Play/Pause** steps continuously.
- **Reset** restarts on the same terrain with a new seed.
- **Nodes** and **Contours** toggle those layers.

**What's on screen:**
- **Blue dots:** agents.
- **Red dots:** junctions (nodes with three or more connections).
- **Orange:** edges added in the most recent step.
- **Hovering** shows the coordinates and height under the mouse.

To render an image without opening a window:

```bash
java -Djava.awt.headless=true -cp <classes> io.levendel.Visualizer.CityVisualizer --snapshot out.png 80 grower
```

## AI usage disclosure

Most of the code in this package was written with AI assistance. The rest of the project (`io.levendel.Core`, `io.levendel.Minecraft`, and the build files) is not covered by this disclosure.

| File | Authorship |
|---|---|
| `CityVisualizer.java` | Rewritten by Claude (Anthropic, Claude Opus 5.5), Nov 2026. It replaced a smaller human-written version that opened a window showing only the terrain. |
| `CityPanel.java` | Written by Claude (Anthropic, Claude Opus 5.5), Nov 2026. |
| `StreetSimulation.java` | Written by Claude (Anthropic, Claude Opus 5.5), Nov 2026. |
| `StreetGrowerSimulation.java` | Written by Claude (Anthropic, Claude Opus 5.5), Nov 2026. |
| `ConstantStreetAttractor.java` | Written by Claude (Anthropic, Claude Opus 5.5), Nov 2026, at the author's request. |
| `SampleStreetSimulation.java` | Written by Claude (Anthropic, Claude Opus 5.5), Nov 2026. It follows the design of the author's `StreetGrower`. |
| `SampleElevationProvider.java` | Generated with ChatGPT (OpenAI), as noted in the file. |
| `ElevationPanel.java` | Predates the Claude work. Its authorship is not recorded here. |
| `CLAUDE.md`, `README.md` | Written by Claude (Anthropic, Claude Opus 5.5), Nov 2026. |

The human author directed the work: what to build, the limits on what could be changed, and the simulation design these files follow. They reviewed the AI-written code before committing it. Claude worked only inside this package. It explained fixes needed in `io.levendel.Core` but left those changes to the author.

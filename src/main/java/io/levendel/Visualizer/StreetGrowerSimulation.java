package io.levendel.Visualizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.levendel.Core.Elevation;
import io.levendel.Core.Proposal;
import io.levendel.Core.StreetGrower;
import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;
import io.levendel.Core.StreetAttractors.WeightedStreetAttractor;

/**
 * Adapter that runs the real io.levendel.Core.StreetGrower and exposes its
 * StreetMap to the visualizer.
 *
 * Any exception thrown by StreetGrower is caught and kept in getLastError(),
 * so the window shows what went wrong instead of crashing.
 */
public class StreetGrowerSimulation implements StreetSimulation {

    private final Elevation elevation;
    private final StreetMap streetMap = new StreetMap();
    private StreetGrower grower;
    private Throwable lastError;
    private int stepCount = 0;

    private Map<StreetNode, Set<StreetNode>> previousSnapshot = new LinkedHashMap<>();
    private final List<StreetNode[]> lastStepEdges = new ArrayList<>();

    public StreetGrowerSimulation(Elevation elevation, int numAgents, int seed) {
        this.elevation = elevation;

        List<WeightedStreetAttractor> attractors = List.of(
                new WeightedStreetAttractor(new ConstantStreetAttractor(), 1.0));

        List<Proposal> proposals = new ArrayList<>();
        for (int deg = 0; deg < 360; deg += 30) {
            proposals.add(new Proposal(6.0, Math.toRadians(deg)));
        }

        try {
            grower = new StreetGrower(elevation, attractors, proposals, numAgents, streetMap, seed);
        } catch (Throwable t) {
            lastError = t;
        }
        previousSnapshot = snapshot();
    }

    @Override
    public void step() {
        lastStepEdges.clear();
        if (grower == null)
            return;
        try {
            grower.step();
            lastError = null;
        } catch (Throwable t) {
            lastError = t;
            t.printStackTrace();
        }
        stepCount++;

        // Work out which edges are new since the last step, for highlighting.
        Map<StreetNode, Set<StreetNode>> now = snapshot();
        for (Map.Entry<StreetNode, Set<StreetNode>> e : now.entrySet()) {
            Set<StreetNode> before = previousSnapshot.getOrDefault(e.getKey(), Set.of());
            for (StreetNode n : e.getValue()) {
                if (!before.contains(n))
                    lastStepEdges.add(new StreetNode[] { e.getKey(), n });
            }
        }
        previousSnapshot = now;
    }

    /** A copy of StreetMap's connections, so drawing never touches the live sets. */
    private Map<StreetNode, Set<StreetNode>> snapshot() {
        Map<StreetNode, Set<StreetNode>> copy = new LinkedHashMap<>();
        for (StreetNode n : streetMap.getNodes()) {
            Set<StreetNode> c = streetMap.getConnections(n);
            copy.put(n, c == null ? new LinkedHashSet<>() : new LinkedHashSet<>(c));
        }
        return copy;
    }

    public Throwable getLastError() {
        return lastError;
    }

    @Override
    public Elevation getElevation() {
        return elevation;
    }

    @Override
    public Map<StreetNode, Set<StreetNode>> getConnections() {
        return Collections.unmodifiableMap(previousSnapshot);
    }

    @Override
    public List<StreetNode> getAgentHeads() {
        List<StreetNode> heads = new ArrayList<>();
        if (grower == null || grower.agents == null)
            return heads;
        for (StreetGrower.Agent agent : grower.agents) {
            if (agent != null && agent.head != null)
                heads.add(agent.head);
        }
        return heads;
    }

    @Override
    public List<StreetNode[]> getLastStepEdges() {
        return Collections.unmodifiableList(lastStepEdges);
    }

    @Override
    public int getStepCount() {
        return stepCount;
    }
}

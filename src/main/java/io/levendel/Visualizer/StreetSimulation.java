package io.levendel.Visualizer;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.levendel.Core.Elevation;
import io.levendel.Core.StreetNode;

/**
 * Everything the visualizer needs to draw a street-growing simulation.
 *
 * CityPanel only talks to this interface, so once io.levendel.Core.StreetGrower
 * compiles, a small adapter around StreetGrower + StreetMap can replace
 * SampleStreetSimulation without touching any drawing code.
 */
public interface StreetSimulation {

    /** Advance the simulation by one tick (every agent moves once). */
    void step();

    /** Terrain the streets are grown on. Node coordinates are array-relative (0..width, 0..height). */
    Elevation getElevation();

    /** Every node and the nodes it connects to (undirected). */
    Map<StreetNode, Set<StreetNode>> getConnections();

    /** Current head (position) of each agent, in agent order. */
    List<StreetNode> getAgentHeads();

    /** Edges added during the most recent step, highlighted by the panel. */
    List<StreetNode[]> getLastStepEdges();

    int getStepCount();
}

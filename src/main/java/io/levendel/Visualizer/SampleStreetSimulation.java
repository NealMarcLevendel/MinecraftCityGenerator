package io.levendel.Visualizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import io.levendel.Core.Elevation;
import io.levendel.Core.StreetNode;

/**
 * A self-contained stand-in for io.levendel.Core.StreetGrower, following the
 * same design so the visualizer has something live to show:
 *
 * - N agents start at the centre of the terrain, each with a head node.
 * - Each step, every agent proposes candidate nodes from a list of
 *   (distance, angle) proposals. Here the angle is relative to the agent's
 *   current heading.
 * - Each candidate is scored by weighted attractors (slope, straightness,
 *   spacing, bounds); scores are averaged like StreetGrower.evaluate().
 * - The best candidate wins, ties broken by a seeded Random. The agent
 *   connects its head to it and moves there.
 *
 * Two small additions make a network rather than N separate lines:
 * - If the chosen candidate lands near an existing node, the agent snaps to it
 *   (forming a junction) and then branches from a random existing node.
 * - If an agent has no acceptable candidate (all culled), it also branches.
 */
public class SampleStreetSimulation implements StreetSimulation {

    /** Mirrors io.levendel.Core.Proposal, but the angle is relative to the agent's heading. */
    public record SimProposal(double distance, double angleOffset) {
    }

    /** Mirrors io.levendel.Core.StreetAttractor; returns a score in [0, 1], or a negative value to cull. */
    @FunctionalInterface
    public interface SimAttractor {
        double evaluate(Agent agent, StreetNode proposed, SampleStreetSimulation sim);
    }

    /** Mirrors io.levendel.Core.StreetAttractors.WeightedStreetAttractor. */
    public record WeightedSimAttractor(String name, SimAttractor attractor, double weight) {
    }

    public static class Agent {
        public StreetNode head;
        public double heading; // radians
        public int branches;

        Agent(StreetNode head, double heading) {
            this.head = head;
            this.heading = heading;
        }
    }

    // ----- tunables -----
    public double maxGrade = 0.6; // blocks of rise per block of run at which the slope score hits 0
    public double minSpacing = 6.0; // nodes closer than this to a foreign node score badly
    public double snapRadius = 4.0; // a chosen node this close to an existing node becomes a junction
    public double maxTurn = Math.toRadians(80); // sharper turns are culled

    private final Elevation elevation;
    private final List<SimProposal> proposals;
    private final List<WeightedSimAttractor> attractors;
    private final Random random;

    private final Map<StreetNode, Set<StreetNode>> connections = new LinkedHashMap<>();
    private final List<Agent> agents = new ArrayList<>();
    private final List<StreetNode[]> lastStepEdges = new ArrayList<>();
    private int stepCount = 0;

    public SampleStreetSimulation(Elevation elevation, int numAgents, long seed) {
        this.elevation = elevation;
        this.random = new Random(seed);
        this.proposals = defaultProposals();
        this.attractors = defaultAttractors();

        StreetNode start = new StreetNode(elevation.getWidth() / 2.0, elevation.getHeight() / 2.0);
        addNode(start);
        double offset = random.nextDouble() * Math.PI * 2;
        for (int i = 0; i < numAgents; i++) {
            agents.add(new Agent(start, offset + i * Math.PI * 2 / numAgents));
        }
    }

    private static List<SimProposal> defaultProposals() {
        List<SimProposal> list = new ArrayList<>();
        for (double distance : new double[] { 5, 8 }) {
            for (int deg = -60; deg <= 60; deg += 15) {
                list.add(new SimProposal(distance, Math.toRadians(deg)));
            }
        }
        return list;
    }

    private List<WeightedSimAttractor> defaultAttractors() {
        List<WeightedSimAttractor> list = new ArrayList<>();
        list.add(new WeightedSimAttractor("bounds", SampleStreetSimulation::boundsCuller, 1.0));
        list.add(new WeightedSimAttractor("slope", SampleStreetSimulation::slopeAttractor, 3.0));
        list.add(new WeightedSimAttractor("straightness", SampleStreetSimulation::straightnessAttractor, 1.5));
        list.add(new WeightedSimAttractor("spacing", SampleStreetSimulation::spacingAttractor, 2.0));
        return list;
    }

    // ----- attractors -----

    private static double boundsCuller(Agent agent, StreetNode p, SampleStreetSimulation sim) {
        boolean inside = p.x() >= 0 && p.z() >= 0
                && p.x() <= sim.elevation.getWidth() - 1 && p.z() <= sim.elevation.getHeight() - 1;
        return inside ? 1.0 : -1.0;
    }

    /** Prefers gentle grades (cf. SlopeStreetAttractor). */
    private static double slopeAttractor(Agent agent, StreetNode p, SampleStreetSimulation sim) {
        double run = distance(agent.head, p);
        if (run == 0)
            return 0;
        double rise = Math.abs(sim.heightAt(p) - sim.heightAt(agent.head));
        return Math.max(0, 1 - (rise / run) / sim.maxGrade);
    }

    /** Prefers continuing straight; culls sharp turns (cf. SharpAngleCuller). */
    private static double straightnessAttractor(Agent agent, StreetNode p, SampleStreetSimulation sim) {
        double angle = Math.atan2(p.z() - agent.head.z(), p.x() - agent.head.x());
        double turn = Math.abs(normalize(angle - agent.heading));
        if (turn > sim.maxTurn)
            return -1.0;
        return (Math.cos(turn) + 1) / 2;
    }

    /** Keeps streets apart, except where a node is close enough to snap to. */
    private static double spacingAttractor(Agent agent, StreetNode p, SampleStreetSimulation sim) {
        double nearest = Double.POSITIVE_INFINITY;
        for (StreetNode n : sim.connections.keySet()) {
            if (n.equals(agent.head))
                continue;
            nearest = Math.min(nearest, distance(n, p));
        }
        if (nearest <= sim.snapRadius)
            return 0.6; // joining an existing street is acceptable
        return Math.min(1.0, nearest / (sim.minSpacing * 2));
    }

    // ----- simulation -----

    @Override
    public void step() {
        lastStepEdges.clear();
        for (Agent agent : agents) {
            Map<StreetNode, Double> results = evaluate(propose(agent), agent);
            StreetNode decision = decide(results);
            if (decision == null) {
                branch(agent);
                continue;
            }
            StreetNode snapped = nearestWithin(decision, snapRadius, agent.head);
            StreetNode target = snapped != null ? snapped : decision;

            addNode(target);
            connect(agent.head, target);
            lastStepEdges.add(new StreetNode[] { agent.head, target });

            agent.heading = Math.atan2(target.z() - agent.head.z(), target.x() - agent.head.x());
            agent.head = target;
            if (snapped != null)
                branch(agent);
        }
        stepCount++;
    }

    private Set<StreetNode> propose(Agent agent) {
        Set<StreetNode> proposed = new LinkedHashSet<>();
        for (SimProposal proposal : proposals) {
            double a = agent.heading + proposal.angleOffset();
            proposed.add(new StreetNode(
                    agent.head.x() + proposal.distance() * Math.cos(a),
                    agent.head.z() + proposal.distance() * Math.sin(a)));
        }
        return proposed;
    }

    private Map<StreetNode, Double> evaluate(Set<StreetNode> proposed, Agent agent) {
        Map<StreetNode, Double> results = new LinkedHashMap<>();
        double totalWeight = attractors.stream().mapToDouble(WeightedSimAttractor::weight).sum();
        candidates: for (StreetNode p : proposed) {
            double value = 0;
            for (WeightedSimAttractor wsa : attractors) {
                double score = wsa.attractor().evaluate(agent, p, this);
                if (score < 0)
                    continue candidates; // culled
                value += score * wsa.weight();
            }
            results.put(p, value / totalWeight);
        }
        return results;
    }

    private StreetNode decide(Map<StreetNode, Double> results) {
        double max = Double.NEGATIVE_INFINITY;
        List<StreetNode> best = new ArrayList<>();
        for (Map.Entry<StreetNode, Double> e : results.entrySet()) {
            if (e.getValue() > max + 1e-9) {
                max = e.getValue();
                best.clear();
                best.add(e.getKey());
            } else if (Math.abs(e.getValue() - max) <= 1e-9) {
                best.add(e.getKey());
            }
        }
        return best.isEmpty() ? null : best.get(random.nextInt(best.size()));
    }

    /** Restart the agent from a random existing node, heading perpendicular-ish to a street there. */
    private void branch(Agent agent) {
        List<StreetNode> nodes = new ArrayList<>(connections.keySet());
        StreetNode from = nodes.get(random.nextInt(nodes.size()));
        double base = random.nextDouble() * Math.PI * 2;
        Set<StreetNode> neighbours = connections.get(from);
        if (!neighbours.isEmpty()) {
            StreetNode n = neighbours.iterator().next();
            base = Math.atan2(n.z() - from.z(), n.x() - from.x()) + (random.nextBoolean() ? 1 : -1) * Math.PI / 2;
        }
        agent.head = from;
        agent.heading = base;
        agent.branches++;
    }

    // ----- graph helpers (StreetMap stores immutable Set.of(), so this keeps its own) -----

    private void addNode(StreetNode n) {
        connections.putIfAbsent(n, new LinkedHashSet<>());
    }

    private void connect(StreetNode a, StreetNode b) {
        if (a.equals(b))
            return;
        connections.get(a).add(b);
        connections.get(b).add(a);
    }

    private StreetNode nearestWithin(StreetNode p, double radius, StreetNode exclude) {
        StreetNode best = null;
        double bestD = radius;
        for (StreetNode n : connections.keySet()) {
            if (n.equals(exclude) || connections.get(exclude).contains(n))
                continue;
            double d = distance(n, p);
            if (d <= bestD) {
                bestD = d;
                best = n;
            }
        }
        return best;
    }

    public double heightAt(StreetNode n) {
        int x = (int) Math.max(0, Math.min(elevation.getWidth() - 1, Math.round(n.x())));
        int z = (int) Math.max(0, Math.min(elevation.getHeight() - 1, Math.round(n.z())));
        return elevation.get(x, z);
    }

    private static double distance(StreetNode a, StreetNode b) {
        return Math.hypot(a.x() - b.x(), a.z() - b.z());
    }

    private static double normalize(double a) {
        while (a > Math.PI)
            a -= Math.PI * 2;
        while (a < -Math.PI)
            a += Math.PI * 2;
        return a;
    }

    // ----- StreetSimulation -----

    @Override
    public Elevation getElevation() {
        return elevation;
    }

    @Override
    public Map<StreetNode, Set<StreetNode>> getConnections() {
        return Collections.unmodifiableMap(connections);
    }

    @Override
    public List<StreetNode> getAgentHeads() {
        List<StreetNode> heads = new ArrayList<>();
        for (Agent a : agents)
            heads.add(a.head);
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

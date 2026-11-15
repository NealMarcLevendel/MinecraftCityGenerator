package io.levendel.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import io.levendel.Core.StreetAttractors.WeightedStreetAttractor;
import io.levendel.Core.StreetAttractors.WeightedStreetAttractorProvider;
import io.levendel.Core.StreetCullers.StreetCuller;
import io.levendel.Core.StreetCullers.StreetCullerProvider;
import io.levendel.Core.StreetNode;

import java.util.List;
import java.util.Random;

public class StreetGrower {
    public Elevation elevation;
    public List<WeightedStreetAttractor> weightedStreetAttractors;
    public List<StreetCuller> streetCullers;
    public List<Proposal> proposals;
    public Agent[] agents;
    public StreetMap streetMap;
    public Random rng;

    public StreetGrower(Elevation elevation, WeightedStreetAttractorProvider weightedStreetAttractorProvider,
            StreetCullerProvider streetCullerProvider,
            List<Proposal> proposals,
            int numAgents, StreetMap streetMap, int seed) {
        this.elevation = elevation;

        this.weightedStreetAttractors = weightedStreetAttractorProvider.provide();
        this.streetCullers = streetCullerProvider.provide();

        this.proposals = proposals;
        this.agents = new Agent[numAgents];
        this.streetMap = streetMap;
        this.rng = new java.util.Random(seed);

        for (int i = 0; i < numAgents; i++) {
            StreetNode start = new StreetNode(this.elevation.getWidth() / 2.0, this.elevation.getHeight() / 2.0);
            streetMap.addNode(start);

            this.agents[i] = new Agent(start, this.streetMap);
        }
    }

    public void step() {
        for (Agent agent : agents) {
            Set<StreetNode> proposedNodes = agent.propose(proposals);

            cull(agent.head, proposedNodes);

            Map<StreetNode, Double> results = evaluate(agent.head, proposedNodes);
            agent.decide(decide(results));
        }
    }

    private Map<StreetNode, Double> evaluate(StreetNode head, Set<StreetNode> proposedNodes) {
        Map<StreetNode, Double> results = new HashMap<>();

        for (StreetNode proposedNode : proposedNodes) {
            double value = 0.0;
            for (WeightedStreetAttractor wsa : weightedStreetAttractors) {
                value += wsa.streetAttractor().evaluate(head, proposedNode, streetMap, elevation) * wsa.weight();
            }
            value /= weightedStreetAttractors.size();
            results.put(proposedNode, value);
        }
        return results;
    }

    private StreetNode decide(Map<StreetNode, Double> results) {
        double max = Double.NEGATIVE_INFINITY;
        List<StreetNode> maxNodes = new ArrayList<>();

        for (Map.Entry<StreetNode, Double> entry : results.entrySet()) {
            double value = entry.getValue();

            if (value > max) {
                max = value;
                maxNodes.clear();
                maxNodes.add(entry.getKey());
            } else if (value == max) {
                maxNodes.add(entry.getKey());
            }
        }

        return maxNodes.get(rng.nextInt(maxNodes.size()));
    }

    public static class Agent {
        public StreetNode head;
        public StreetMap streetMap;

        public Agent(StreetNode head, StreetMap streetMap) {

            this.head = head;
            this.streetMap = streetMap;
            streetMap.addNode(head);
        }

        public Set<StreetNode> propose(List<Proposal> proposals) {
            Set<StreetNode> proposedNodes = new HashSet<>();

            for (Proposal proposal : proposals) {
                double newX = head.x() + proposal.distance() * Math.cos(proposal.angle());
                double newZ = head.z() + proposal.distance() * Math.sin(proposal.angle());
                proposedNodes.add(new StreetNode(newX, newZ));
            }
            return proposedNodes;
        }

        public void decide(StreetNode decision) {
            streetMap.addNode(decision);
            streetMap.connectNodes(head, decision);
            this.head = decision;
        }
    }

    public void cull(StreetNode head, Set<StreetNode> proposedNodes) {
        for (StreetCuller streetCuller : streetCullers) {
            proposedNodes.removeIf(n -> streetCuller.evaluate(head, n, streetMap, elevation));
        }

    }

    public static double random(double min, double max) {
        return min + Math.random() * (max - min);
    }
}

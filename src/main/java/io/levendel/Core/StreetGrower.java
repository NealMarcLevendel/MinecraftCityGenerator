package io.levendel.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import main.java.io.levendel.Core.StreetAttractors.WeightedStreetAttractor;

import java.util.List;

public class StreetGrower {
    private Elevation elevation;
    private List<WeightedStreetAttractor> weightedStreetAttractors;
    private List<Proposal> proposals;
    private Agent[] agents;
    private StreetMap streetMap;
    private int seed;

    public StreetGrower(Elevation elevation, List<WeightedStreetAttractor> weightedStreetAttractors, List<Proposal> proposals, 
        int numAgents, StreepMap streetMap, int seed) {
        this.elevation = elevation;
        this.weightedStreetAttractors = weightedStreetAttractors;
        this.proposals = proposals;
        this.agents = new Agent[numAgents];
        for (Agent agent : this.agents) {
            StreetNode start = new StreetNode(this.elevation.getWidth(), this.elevation.getHeight());
            streetMap.addNode(start);

            agent = new Agent(start, this.streetMap);
        }
        this.streetMap = streetMap;
        this.seed = seed;
    }

    public void step() {
        for (Agent agent : agents) {
            Set<StreetNode> proposedNodes = agent.propose(proposals);
            Map<StreetNode, Double> results = evaluate(proposedNodes, agent);
            agent.decide(decide(results));
        }
    }
    private Map<StreetNode, Double> evaluate(Set<StreetNode> proposedNodes, Agent agent) {
        Map<StreetNode, Double> results = new HashMap<>();

        for (StreetNode proposedNode : proposedNodes) {
            double value = 0.0;
            for (WeightedStreetAttractor wsa : weightedStreetAttractors) {
                value += wsa.streetAttractor().evaluate(agent.head, proposedNode, streetMap) * wsa.weight();
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
            } 
            else if (value == max) {
                maxNodes.add(entry.getKey());
            }
        }

        return maxNodes.get(this.seed % maxNodes.size());
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
                double newX = head.x() * proposal.distance() * Math.cos(proposal.angle());
                double newZ = head.x() * proposal.distance() * Math.sin(proposal.angle());
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
    public static double random(double min, double max) {
        return min + Math.random() * (max - min);
    }
}

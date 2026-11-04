package io.levendel.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.util.List;

public class StreetGrower {
    private Elevation ev;
    private List<StreetAttractor> streetAttractors;
    private Agent[] agents;
    private Map<StreetNode, Set<StreetNode>> streetNodeConnections = new HashMap<>();

    public StreetGrower(Elevation ev, List<StreetAttractor> streetAttractors, int numAgents) {
        this.ev = ev;
        this.streetAttractors = streetAttractors;
        this.agents = new Agent[numAgents];
    }

    public StreetMap grow() {
        StreetMap sm = new StreetMap();
        // logic

        //
        return sm;
    }

    public void connectNodes(StreetNode node1, StreetNode node2) {
        if (streetNodeConnections.containsKey(node1))
            streetNodeConnections.get(node1).add(node2);
        if (streetNodeConnections.containsKey(node2))
            streetNodeConnections.get(node2).add(node1);
    }

    public Set<StreetNode> getConnections(StreetNode node) {
        if (!streetNodeConnections.containsKey(node))
            return null;
        return streetNodeConnections.get(node);
    }

    public static class Agent {
        public StreetNode head;
        public StreetNode prev;

        public Agent(StreetNode head) {

            this.head = head;
            this.head = prev;
        }

        public Set<StreetNode> propose() {
            /*
            0, 22.5, 45, 67.5, 90, 112.5, 135, ...
            */
            Set<StreetNode> proposedNodes = new HashSet<>();
            double dist = 1.0;
            for (double t = 0; t <= (Math.PI*2); t += (Math.PI*2)/16) {
                proposedNodes.add(new StreetNode(dist*Math.cos(t)+this.head.x(), dist*Math.sin(t)+this.head.z()));
            }
            return proposedNodes;
        }
        public void decide(StreetNode decision) {

        }
    }
}

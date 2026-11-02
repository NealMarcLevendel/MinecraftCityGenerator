package io.levendel.Core;

import java.util.ArrayList;
import java.util.HashMap;
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

        public Agent(StreetNode head, StreetAttractor af) {

            this.head = head;
        }

        public StreetNode propose() {

            return null;
        }
    }
}

package io.levendel.Core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StreetMap {
    private Map<StreetNode, Set<StreetNode>> connections = new HashMap<>();

    public StreetMap() {

    }

    public void addNode(StreetNode curr) {
        if (getConnections(curr) == null)
            connections.put(curr, new HashSet<>());
    }

     public void connectNodes(StreetNode node1, StreetNode node2) {
        if (connections.containsKey(node1))
            connections.get(node1).add(node2);
        if (connections.containsKey(node2))
            connections.get(node2).add(node1);
    }

    public Set<StreetNode> getNodes() {
        return connections.keySet();
    }

    public Set<StreetNode> getConnections(StreetNode curr) {
        if (!connections.containsKey(curr))
            return null;
        else
            return connections.get(curr);
    }
}

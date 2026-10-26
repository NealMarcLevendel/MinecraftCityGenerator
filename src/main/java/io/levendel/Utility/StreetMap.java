package io.levendel.Utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StreetMap {
    private Map<StreetNode, Set<StreetNode>> connections = new HashMap<>();

    public StreetMap() {

    }

    public void addNode(StreetNode curr) {
        if (getConnections(curr) == null)
            connections.put(curr, Set.of());
    }

    public void addNode(StreetNode curr, StreetNode next) {
        if (getConnections(curr) == null)
            connections.put(curr, Set.of(next));
        else
            connections.get(curr).add(next);

        if (getConnections(next) == null)
            connections.put(next, Set.of(curr));
        else
            connections.get(next).add(curr);
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

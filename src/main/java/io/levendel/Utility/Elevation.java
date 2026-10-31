package io.levendel.Utility;

import java.util.HashMap;
import java.util.Map;

public class Elevation {
    private Map<ElevationPoint, Integer> ElevationMap = new HashMap<>();
    public ElevationPoint bound1;
    public ElevationPoint bound2;
 
    public Elevation (ElevationPoint bound1, ElevationPoint bound2) {
        int minX = Math.min(bound1.x(), bound2.x());
        int minZ = Math.min(bound1.z(), bound2.z());
        int maxX = Math.max(bound1.x(), bound2.x());
        int maxZ = Math.max(bound1.z(), bound2.z());

        this.bound1 = new ElevationPoint(minX, minZ);
        this.bound2 = new ElevationPoint(maxX, maxZ);
    }

    public void set(ElevationPoint ep, int value) {
        if (ep.x() > bound2.x() || ep.z() > bound2.z() || ep.x() < bound1.x() || ep.z() < bound1.z()) return; 
        ElevationMap.put(ep, value);
    }

    public Integer get(ElevationPoint ep) {
        if (!ElevationMap.containsKey(ep))
            return null;
        else
            return ElevationMap.get(ep);
    }
}

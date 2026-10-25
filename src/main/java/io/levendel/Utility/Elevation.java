package io.levendel.Utility;

import java.util.HashMap;
import java.util.Map;

public class Elevation {
    private Map<ElevationPoint, Integer> ElevationMap = new HashMap<>();

    public void set(ElevationPoint ep, int value) {
        ElevationMap.put(ep, value);
    }

    public Integer get(ElevationPoint ep) {
        if (!ElevationMap.containsKey(ep))
            return null;
        else
            return ElevationMap.get(ep);
    }
}

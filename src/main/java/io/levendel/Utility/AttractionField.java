package io.levendel.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttractionField {
    public Elevation elevation;
    public int tileRadius;
    public int max;
    public int min;
    private Map<ElevationPoint, Double> af = new HashMap<>();

    public AttractionField(Elevation elevation, int tileRadius, int verticalBound1, int verticalBound2) {
        this.tileRadius = tileRadius;
        this.elevation = elevation;
        this.max = Math.max(verticalBound1, verticalBound2);
        this.min = Math.min(verticalBound1, verticalBound2);

        for (int x = elevation.bound1.x(); x <= elevation.bound2.x(); x++) {
            for (int z = elevation.bound1.z(); z <= elevation.bound2.z(); z++) {
                List<Integer> tile = new ArrayList<>();
                af.put(new ElevationPoint(x, z), getHeightDifferenceMagnitude(tile, x, z));
            }
        }
    }

    private double getHeightDifferenceMagnitude(List<Integer> tile, int x, int z) {
        for (int xr = x - tileRadius; xr <= x + tileRadius; xr++) {
            for (int zr = z - tileRadius; zr <= z + tileRadius; zr++) {
                if (xr < elevation.bound1.x() || xr > elevation.bound2.x())
                    continue;
                if (zr < elevation.bound1.z() || zr > elevation.bound2.z())
                    continue;

                Integer height = elevation.get(new ElevationPoint(xr, zr));
                if (height != null)
                    tile.add(height);
            }
        }

        int top = Integer.MIN_VALUE;
        int bottom = Integer.MAX_VALUE;
        for (Integer i : tile) {
            top = Math.max(top, i);
            bottom = Math.min(bottom, i);
        }
        int value = top - bottom;

        if (this.max == this.min)
            return 0.0;
        return (double) (value - this.min) / (this.max - this.min);
    }

    public Double get(ElevationPoint ep) {
        if (!af.containsKey(ep))
            return null;
        return af.get(ep);
    }
}
package main.java.io.levendel.Core.StreetAttractors;

import io.levendel.Core.Elevation;
import io.levendel.Core.StreetAttractor;
import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;

public class SlopeStreetAttractor implements StreetAttractor {
    public StreetNode curr;
    public StreetNode proposed;
    public StreepMap sm;

    public SlopeStreetAttractor (StreetNode curr, StreetNode proposed, StreetMap sm) {
        this.curr = curr;
        this.proposed = proposed;
        this.sm = sm;
    }
    public double evaluate () {
        return 0.0;
    }
}

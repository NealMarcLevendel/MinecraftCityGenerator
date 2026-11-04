package main.java.io.levendel.Core.StreetAttractors;

import io.levendel.Core.StreetAttractor;
import io.levendel.Core.StreetNode;

public class SlopeStreetAttractor implements StreetAttractor {
    public Elevation ev;
    public int step;
    public SlopeStreetAttractor (Elevation ev, int step) {
        this.ev = ev;
        this.step = step;
    }
    public Double (StreetNode current, StreetNode proposed) {
        return 0.0;
    }
}

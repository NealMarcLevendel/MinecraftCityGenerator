package main.java.io.levendel.Core.StreetAttractors;

import io.levendel.Core.StreetAttractor;
import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;

public class SharpAngleCuller implements StreetAttractor {
    public StreetNode curr;
    public StreetNode proposed;
    public SteepMap sm;
    public SharpAngleCuller (StreetNode curr, StreetNode proposed, StreetMap sm) {
        this.curr = curr;
        this.proposed = proposed;
        this.sm = sm;
    }
}

package io.levendel.Visualizer;

import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;
import io.levendel.Core.StreetAttractors.StreetAttractor;

/**
 * Placeholder attractor that scores every proposal the same (1.0 by default),
 * so StreetGrower can be run and visualized before real attractors exist.
 */
public class ConstantStreetAttractor implements StreetAttractor {

    private final double value;

    public ConstantStreetAttractor() {
        this(1.0);
    }

    public ConstantStreetAttractor(double value) {
        this.value = value;
    }

    @Override
    public double evaluate(StreetNode current, StreetNode proposed, StreetMap streetMap) {
        return value;
    }
}

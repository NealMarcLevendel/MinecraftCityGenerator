package io.levendel.Core.StreetAttractors;

import io.levendel.Core.Elevation;
import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;

public interface StreetAttractor {

    double evaluate(StreetNode current, StreetNode proposed, StreetMap streetMap, Elevation elevation);

}
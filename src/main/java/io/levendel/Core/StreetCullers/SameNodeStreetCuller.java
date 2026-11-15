package io.levendel.Core.StreetCullers;

import io.levendel.Core.Elevation;
import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;

public class SameNodeStreetCuller implements StreetCuller {

    @Override
    public boolean evaluate(StreetNode current, StreetNode proposed, StreetMap streetMap, Elevation elevation) {
        return proposed.x() == current.x() && proposed.z() == current.z();
    }

}

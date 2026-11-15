package io.levendel.Core.StreetCullers;

import io.levendel.Core.Elevation;
import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;

public class OutOfBoundsStreetCuller implements StreetCuller {

    @Override
    public boolean evaluate(StreetNode current, StreetNode proposed, StreetMap streetMap, Elevation elevation) {
        return proposed.x() < 0 || proposed.x() > elevation.getWidth() - 1 ||
                proposed.z() < 0 || proposed.z() > elevation.getHeight() - 1;
    }

}

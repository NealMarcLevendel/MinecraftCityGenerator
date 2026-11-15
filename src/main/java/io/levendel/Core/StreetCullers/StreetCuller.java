package io.levendel.Core.StreetCullers;

import io.levendel.Core.Elevation;
import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;

public interface StreetCuller {

    boolean evaluate(StreetNode current, StreetNode proposed, StreetMap streetMap, Elevation elevation);

}
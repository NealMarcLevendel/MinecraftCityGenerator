package io.levendel.Core.StreetCullers;

import io.levendel.Core.Elevation;
import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;

public class OverlapStreetCuller implements StreetCuller {

    @Override
    public boolean evaluate(StreetNode current, StreetNode proposed, StreetMap streetMap, Elevation elevation) {

        for (StreetNode streetNode : streetMap.getNodes()) {
            for (StreetNode connection : streetMap.getConnections(streetNode)) {
                if (streetNode.equals(current) || connection.equals(current))
                    continue; // streets leaving our own node always touch it
                if (lineOverlaps(current, proposed, connection, streetNode))
                    return true;
            }
        }
        return false;
    }

    public boolean lineOverlaps(StreetNode a, StreetNode b, StreetNode c, StreetNode d) {

        double dx = b.x() - a.x(); // x2 - x1
        double dz = b.z() - a.z(); // y2 - y1

        double ex = d.x() - c.x(); // x4 - x3
        double ez = d.z() - c.z(); // y4 - y3

        double determinant = dx * ez - dz * ex;

        if (determinant == 0)
            return false;

        double t = ((c.x() - a.x()) * ez - (c.z() - a.z()) * ex) / determinant;
        double u = ((c.x() - a.x()) * dz - (c.z() - a.z()) * dx) / determinant;

        return (t >= 0 && t <= 1) && (u >= 0 && u <= 1);
    }

}

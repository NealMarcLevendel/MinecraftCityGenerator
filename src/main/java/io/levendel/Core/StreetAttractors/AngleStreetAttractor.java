package io.levendel.Core.StreetAttractors;

import java.util.Set;

import io.levendel.Core.Elevation;
import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;

class AngleStreetAttractor implements StreetAttractor {

    @Override
    public double evaluate(StreetNode current, StreetNode proposed, StreetMap streetMap, Elevation elevation) {
        Set<StreetNode> connections = streetMap.getConnections(current);
        if (connections.size() != 1)
            return 1.0;

        double theta = angle((StreetNode) connections.toArray()[0], current, proposed);

        return turnScore(theta);
    }

    public double angle(StreetNode a, StreetNode b, StreetNode c) {
        double bax = a.x() - b.x();
        double bay = a.z() - b.z();

        double bcx = c.x() - b.x();
        double bcy = c.z() - b.z();

        double dot = bax * bcx + bay * bcy;

        double magBA = Math.sqrt(bax * bax + bay * bay);
        double magBC = Math.sqrt(bcx * bcx + bcy * bcy);

        if (magBA == 0 || magBC == 0)
            return 0;

        double cos = dot / (magBA * magBC);
        cos = Math.max(-1.0, Math.min(1.0, cos));

        return 180.0 - Math.toDegrees(Math.acos(cos));
    }

    public static double turnScore(double theta) {
        double preferredAngle = 22.5;
        double sigma = 8.0;

        double difference = theta - preferredAngle;

        return Math.exp(
                -(difference * difference) / (2 * sigma * sigma));
    }
}
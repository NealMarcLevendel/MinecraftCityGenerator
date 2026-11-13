package io.levendel.Core.StreetAttractors;

import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;
import net.minecraft.world.level.levelgen.DensityFunctions.Spline.Point;

class AngleStreetAttractor implements StreetAttractor {

    @Override
    public double evaluate(StreetNode current, StreetNode proposed, StreetMap streetMap) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
    }
    public double angle(StreetNode a, StreetNode b, StreetNode c) {
    double bax = a.x() - b.x();
    double bay = a.z() - b.z();

    double bcx = c.x() - b.x();
    double bcy = c.z() - b.z();

    double dot = bax * bcx + bay * bcy;

    double magBA = Math.sqrt(bax * bax + bay * bay);
    double magBC = Math.sqrt(bcx * bcx + bcy * bcy);

    return Math.acos(dot / (magBA * magBC));
}

}
package io.levendel.Core.StreetCullers;

import java.util.List;

public class HardcodedStreetCullerProvider implements StreetCullerProvider {

    @Override
    public List<StreetCuller> provide() {
        return List.of(
                new SameNodeStreetCuller(),
                new OutOfBoundsStreetCuller(),
                new OverlapStreetCuller());
    }

}

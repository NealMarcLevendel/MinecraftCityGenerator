package io.levendel.Core.StreetAttractors;

import java.util.List;

import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;

public class HardcodedWeightedStreetAttractorProvider implements WeightedStreetAttractorProvider {

    @Override
    public List<WeightedStreetAttractor> provide() {
        return List.of(
            new WeightedStreetAttractor(new StreetAttractor() {
                @Override
                public double evaluate(StreetNode current, StreetNode proposed, StreetMap streetMap) {
                    return 1.0;
                }
            }, 1.0),
            new WeightedStreetAttractor(new AngleStreetAttractor(), 1.0)
        );
    }
    
}

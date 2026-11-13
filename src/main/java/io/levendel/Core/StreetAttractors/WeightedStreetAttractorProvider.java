package io.levendel.Core.StreetAttractors;

import java.util.List;

import io.levendel.Core.StreetMap;
import io.levendel.Core.StreetNode;

public interface WeightedStreetAttractorProvider {

    List<WeightedStreetAttractor> provide();
    
} 
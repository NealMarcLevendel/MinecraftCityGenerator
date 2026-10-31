package io.levendel.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AttractionField {
    private Map<Tile, Set<Tile>> connections = new HashMap<>();
    
    public Elevation ev;
    public List<Tile> tiles = new ArrayList<>();
    public AttractionField (Elevation ev, int tileSize) {
        this.ev = ev;
    }
}

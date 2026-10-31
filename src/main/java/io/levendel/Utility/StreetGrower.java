package io.levendel.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class StreetGrower {
    private Elevation ev;
    private AttractionField af;
    private Agent [] agents;
    

    public StreetGrower(Elevation ev, AttractionField af, int numAgents) {
        this.ev = ev;
        this.af = af;
        this.agents = new Agent[numAgents];
    }

    public StreetMap grow() {
        StreetMap sm = new StreetMap();
        //logic
        
        //
        return sm;
    }

    public static class Agent {
        public StreetNode head;

        public Agent(StreetNode head, AttractionField af) {
            this.head = head;
        }
        public StreetNode decide () {
            

            return this.head;
        }
    }
}

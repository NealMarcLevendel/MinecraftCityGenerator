package io.levendel.Utility;

public class StreetGrower {
    private Elevation ev;

    public StreetGrower(Elevation ev, int numAgents) {
        this.ev = ev;
    }

    public StreetMap grow() {
        StreetMap sm = new StreetMap();
        return sm;
    }

    public static class Agent {
        public StreetNode head;

        public Agent(StreetNode head) {
            this.head = head;
        }
    }
}

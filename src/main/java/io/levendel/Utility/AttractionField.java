package io.levendel.Utility;

public class AttractionField {
    public Elevation ev;

    public int tileRadius;
    public int blurRadius;
    public double sigma;

    private double[][] elevation;
    private double[][] smoothed;
    private double[][] slope;
    private double[][] accessibility;

    public AttractionField(
            Elevation ev,
            int tileRadius,
            int blurRadius,
            double sigma) {
        this.ev = ev;
        this.tileRadius = tileRadius;
        this.blurRadius = blurRadius;
        this.sigma = sigma;

        this.elevation = ev.toArray();
    }
}
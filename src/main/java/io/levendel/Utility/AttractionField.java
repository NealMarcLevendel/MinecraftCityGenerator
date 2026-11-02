package io.levendel.Utility;

public class AttractionField {

    private final double[][] data;

    private final int offsetX;
    private final int offsetZ;

    private final int tileRadius;
    private final int max;
    private final int min;

    public AttractionField(
            Elevation elevation,
            int tileRadius,
            int verticalBound1,
            int verticalBound2) {

        if (tileRadius < 0) {
            throw new IllegalArgumentException("Tile radius cannot be negative.");
        }

        this.tileRadius = tileRadius;
        this.max = Math.max(verticalBound1, verticalBound2);
        this.min = Math.min(verticalBound1, verticalBound2);

        this.offsetX = elevation.offsetX;
        this.offsetZ = elevation.offsetZ;

        data = new double[elevation.getHeight()][elevation.getWidth()];

        for (int z = 0; z < data.length; z++) {
            for (int x = 0; x < data[z].length; x++) {
                data[z][x] = getHeightDifferenceMagnitude(elevation, x, z);
            }
        }
    }

    private double getHeightDifferenceMagnitude(
            Elevation elevation, int x, int z) {

        int top = Integer.MIN_VALUE;
        int bottom = Integer.MAX_VALUE;

        for (int xr = x - tileRadius; xr <= x + tileRadius; xr++) {
            for (int zr = z - tileRadius; zr <= z + tileRadius; zr++) {

                if (xr < 0 || xr >= elevation.getWidth())
                    continue;

                if (zr < 0 || zr >= elevation.getHeight())
                    continue;

                int height = elevation.get(xr, zr);

                top = Math.max(top, height);
                bottom = Math.min(bottom, height);
            }
        }

        int value = top - bottom;

        if (max == min)
            return 0.0;

        return Math.max(0.0, Math.min(1.0,
                (double) (value - min) / (max - min)));
    }

    // Array-relative coordinates

    public double get(int x, int z) {
        checkBounds(x, z);
        return data[z][x];
    }

    public void set(int x, int z, double value) {
        checkBounds(x, z);
        data[z][x] = value;
    }

    // World coordinates

    public double getWithOffset(int x, int z) {
        return get(x - offsetX, z - offsetZ);
    }

    public void setWithOffset(int x, int z, double value) {
        set(x - offsetX, z - offsetZ, value);
    }

    public int getWidth() {
        return data[0].length;
    }

    public int getHeight() {
        return data.length;
    }

    private void checkBounds(int x, int z) {
        if (x < 0 || x >= getWidth() || z < 0 || z >= getHeight()) {
            throw new IndexOutOfBoundsException(
                    "Coordinates out of bounds: (" + x + ", " + z + ")");
        }
    }
}
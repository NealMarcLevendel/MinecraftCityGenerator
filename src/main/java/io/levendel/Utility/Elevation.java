package io.levendel.Utility;

public class Elevation {

    private final int[][] data;

    public final int offsetX;
    public final int offsetZ;

    public Elevation(int minX, int minZ, int maxX, int maxZ) {

        if (maxX < minX || maxZ < minZ) {
            throw new IllegalArgumentException(
                    "Maximum bounds must be greater than or equal to minimum bounds.");
        }

        this.offsetX = minX;
        this.offsetZ = minZ;

        int width = Math.addExact(Math.subtractExact(maxX, minX), 1);
        int height = Math.addExact(Math.subtractExact(maxZ, minZ), 1);

        data = new int[height][width];
    }

    // Array-relative coordinates
    public void set(int x, int z, int elevation) {
        checkBounds(x, z);
        data[z][x] = elevation;
    }

    public int get(int x, int z) {
        checkBounds(x, z);
        return data[z][x];
    }

    // World coordinates
    public void setWithOffset(int x, int z, int elevation) {
        checkWorldBounds(x, z);
        data[z - offsetZ][x - offsetX] = elevation;
    }

    public int getWithOffset(int x, int z) {
        checkWorldBounds(x, z);
        return data[z - offsetZ][x - offsetX];
    }

    public int getWidth() {
        return data[0].length;
    }

    public int getHeight() {
        return data.length;
    }

    public int[][] getData() {
        return data;
    }

    private void checkBounds(int x, int z) {
        if (z < 0 || z >= data.length ||
                x < 0 || x >= data[0].length) {

            throw new IndexOutOfBoundsException(
                    "Array coordinates out of bounds: (" + x + ", " + z + ")");
        }
    }

    private void checkWorldBounds(int x, int z) {
        int relativeX = x - offsetX;
        int relativeZ = z - offsetZ;

        checkBounds(relativeX, relativeZ);
    }
}
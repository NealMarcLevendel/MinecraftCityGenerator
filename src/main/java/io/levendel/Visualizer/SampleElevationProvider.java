
package io.levendel.Visualizer;

import io.levendel.Core.Elevation;
import io.levendel.Core.ElevationProvider;

import java.util.Random;

// Implemented by GPT
public class SampleElevationProvider implements ElevationProvider {

    private final int minX, minZ, width, height;
    private final int baseElevation;
    private final double amplitude;
    private final double scale;
    private final long seed;

    public SampleElevationProvider(
            int minX,
            int minZ,
            int width,
            int height,
            int baseElevation,
            double amplitude,
            double scale,
            long seed) {
        this.minX = minX;
        this.minZ = minZ;
        this.width = width;
        this.height = height;
        this.baseElevation = baseElevation;
        this.amplitude = amplitude;
        this.scale = scale;
        this.seed = seed;
    }

    @Override
    public Elevation provide() {
        Elevation elevation = new Elevation(
                minX,
                minZ,
                minX + width - 1,
                minZ + height - 1);

        int[] permutation = createPermutation(seed);

        for (int x = minX; x < minX + width; x++) {
            for (int z = minZ; z < minZ + height; z++) {

                double noise = 0;
                double frequency = 1.0;
                double octaveAmplitude = 1.0;
                double maxAmplitude = 0;

                // Combine several layers of noise
                for (int octave = 0; octave < 4; octave++) {
                    noise += perlin(
                            x / scale * frequency,
                            z / scale * frequency,
                            permutation) * octaveAmplitude;

                    maxAmplitude += octaveAmplitude;

                    frequency *= 2.0;
                    octaveAmplitude *= 0.5;
                }

                noise /= maxAmplitude;

                int value = baseElevation
                        + (int) Math.round(noise * amplitude);

                elevation.setWithOffset(x, z, value);
            }
        }

        return elevation;
    }

    private static double perlin(double x, double y, int[] p) {
        int xi = (int) Math.floor(x) & 255;
        int yi = (int) Math.floor(y) & 255;

        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        double u = fade(xf);
        double v = fade(yf);

        int aa = p[p[xi] + yi];
        int ab = p[p[xi] + yi + 1];
        int ba = p[p[xi + 1] + yi];
        int bb = p[p[xi + 1] + yi + 1];

        double x1 = lerp(
                grad(aa, xf, yf),
                grad(ba, xf - 1, yf),
                u);

        double x2 = lerp(
                grad(ab, xf, yf - 1),
                grad(bb, xf - 1, yf - 1),
                u);

        return lerp(x1, x2, v);
    }

    private static double grad(int hash, double x, double y) {
        return switch (hash & 7) {
            case 0 -> x + y;
            case 1 -> -x + y;
            case 2 -> x - y;
            case 3 -> -x - y;
            case 4 -> x;
            case 5 -> -x;
            case 6 -> y;
            default -> -y;
        };
    }

    private static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private static int[] createPermutation(long seed) {
        int[] p = new int[512];
        int[] values = new int[256];

        for (int i = 0; i < 256; i++) {
            values[i] = i;
        }

        Random random = new Random(seed);

        for (int i = 255; i > 0; i--) {
            int j = random.nextInt(i + 1);

            int temp = values[i];
            values[i] = values[j];
            values[j] = temp;
        }

        for (int i = 0; i < 512; i++) {
            p[i] = values[i & 255];
        }

        return p;
    }
}

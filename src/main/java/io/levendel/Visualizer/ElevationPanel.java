package io.levendel.Visualizer;

import javax.swing.*;
import java.awt.*;

import io.levendel.Core.Elevation;

public class ElevationPanel extends JPanel {

    private final Elevation elevation;

    public ElevationPanel(Elevation elevation) {
        this.elevation = elevation;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = elevation.getWidth();
        int height = elevation.getHeight();

        // Find minimum and maximum elevation
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                int value = elevation.get(x, z);
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
        }

        // Draw each elevation cell
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {

                int value = elevation.get(x, z);

                int brightness = max == min
                        ? 128
                        : (int) ((value - min) * 255.0 / (max - min));

                g.setColor(new Color(brightness, brightness, brightness));

                int screenX = x * getWidth() / width;
                int screenY = z * getHeight() / height;

                int cellWidth = (x + 1) * getWidth() / width - screenX;
                int cellHeight = (z + 1) * getHeight() / height - screenY;

                g.fillRect(screenX, screenY, cellWidth, cellHeight);
            }
        }
    }
}
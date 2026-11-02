package io.levendel.Visualizer;

import javax.swing.*;

import io.levendel.Core.Elevation;
import io.levendel.Core.ElevationProvider;

public class CityVisualizer {

    public static void main(String[] args) {

        ElevationProvider provider = new SampleElevationProvider(
                0, 0,
                256, 256,
                70, 30,
                80.0,
                12345L);

        Elevation elevation = provider.provide();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("City Generator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 800);

            frame.add(new ElevationPanel(elevation));

            frame.setVisible(true);
        });
    }
}
package io.levendel.Visualizer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import io.levendel.Core.Elevation;
import io.levendel.Core.ElevationProvider;

/**
 * Live visualizer: terrain, street nodes and their connections, with a Step button.
 *
 * The "Simulation" dropdown switches between:
 *   - StreetGrower: the real io.levendel.Core.StreetGrower, using ConstantStreetAttractor
 *   - Sample:       SampleStreetSimulation, a self-contained reference implementation
 *
 * Headless:
 *   CityVisualizer --snapshot out.png 60 [grower|sample]
 */
public class CityVisualizer {

    private static final int TERRAIN_SEED = 12345;
    private static final int AGENTS = 4;

    enum Mode {
        GROWER("StreetGrower (Core)"), SAMPLE("Sample simulation");

        final String label;

        Mode(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private Mode mode = Mode.GROWER;
    private int simSeed = 1;
    private Elevation elevation;
    private StreetSimulation simulation;
    private CityPanel panel;
    private JLabel status;
    private Timer timer;
    private JButton playButton;

    public static void main(String[] args) throws IOException {
        if (args.length >= 2 && args[0].equals("--snapshot")) {
            int steps = args.length >= 3 ? Integer.parseInt(args[2]) : 60;
            Mode mode = args.length >= 4 && args[3].equalsIgnoreCase("sample") ? Mode.SAMPLE : Mode.GROWER;
            StreetSimulation sim = createSimulation(mode, createElevation(), 1);
            for (int i = 0; i < steps; i++)
                sim.step();
            ImageIO.write(new CityPanel(sim).snapshot(900, 900), "png", new File(args[1]));
            System.out.println("Wrote " + args[1] + " (" + mode + ") after " + steps + " steps: "
                    + statusText(sim));
            return;
        }
        SwingUtilities.invokeLater(() -> new CityVisualizer().show());
    }

    static Elevation createElevation() {
        ElevationProvider provider = new SampleElevationProvider(
                0, 0,
                256, 256,
                70, 30,
                80.0,
                TERRAIN_SEED);
        return provider.provide();
    }

    static StreetSimulation createSimulation(Mode mode, Elevation elevation, int seed) {
        return switch (mode) {
            case GROWER -> new StreetGrowerSimulation(elevation, AGENTS, seed);
            case SAMPLE -> new SampleStreetSimulation(elevation, AGENTS, seed);
        };
    }

    private void show() {
        elevation = createElevation();
        simulation = createSimulation(mode, elevation, simSeed);
        panel = new CityPanel(simulation);

        JComboBox<Mode> modeBox = new JComboBox<>(Mode.values());
        modeBox.setSelectedItem(mode);
        modeBox.addActionListener(e -> {
            mode = (Mode) modeBox.getSelectedItem();
            restart();
        });

        JButton step = new JButton("Step");
        step.addActionListener(e -> step(1));

        JButton step10 = new JButton("Step ×10");
        step10.addActionListener(e -> step(10));

        playButton = new JButton("Play");
        timer = new Timer(80, e -> step(1));
        playButton.addActionListener(e -> togglePlay());

        JButton reset = new JButton("Reset");
        reset.setToolTipText("Restart on the same terrain with a new seed");
        reset.addActionListener(e -> {
            simSeed++;
            restart();
        });

        JCheckBox nodes = new JCheckBox("Nodes", true);
        nodes.addActionListener(e -> panel.setShowNodes(nodes.isSelected()));
        JCheckBox contours = new JCheckBox("Contours", true);
        contours.addActionListener(e -> panel.setShowContours(contours.isSelected()));

        status = new JLabel();
        status.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        toolbar.add(new JLabel("Simulation:"));
        toolbar.add(modeBox);
        toolbar.add(step);
        toolbar.add(step10);
        toolbar.add(playButton);
        toolbar.add(reset);
        toolbar.add(nodes);
        toolbar.add(contours);

        JFrame frame = new JFrame("City Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(toolbar, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.add(status, BorderLayout.SOUTH);
        frame.setSize(1000, 940);
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setDefaultButton(step); // Enter = step

        updateStatus();
        frame.setVisible(true);
    }

    private void restart() {
        if (timer.isRunning())
            togglePlay();
        simulation = createSimulation(mode, elevation, simSeed);
        panel.setSimulation(simulation);
        updateStatus();
    }

    private void step(int n) {
        for (int i = 0; i < n; i++)
            simulation.step();
        if (simulation instanceof StreetGrowerSimulation g && g.getLastError() != null && timer.isRunning())
            togglePlay(); // stop autoplay on errors
        updateStatus();
        panel.repaint();
    }

    private void togglePlay() {
        if (timer.isRunning()) {
            timer.stop();
            playButton.setText("Play");
        } else {
            timer.start();
            playButton.setText("Pause");
        }
    }

    private void updateStatus() {
        String text = statusText(simulation);
        boolean error = text.startsWith("Error");
        status.setForeground(error ? new Color(200, 30, 30) : UIManager.getColor("Label.foreground"));
        status.setText(text);
    }

    static String statusText(StreetSimulation sim) {
        if (sim instanceof StreetGrowerSimulation g && g.getLastError() != null) {
            Throwable t = g.getLastError();
            StackTraceElement where = t.getStackTrace().length > 0 ? t.getStackTrace()[0] : null;
            return "Error in StreetGrower: " + t + (where != null ? "  at " + where : "");
        }
        int nodes = sim.getConnections().size();
        int edges = sim.getConnections().values().stream().mapToInt(s -> s.size()).sum() / 2;
        long junctions = sim.getConnections().values().stream().filter(s -> s.size() >= 3).count();
        return String.format("Step %d   ·   %d nodes   ·   %d edges   ·   %d junctions   ·   %d agents",
                sim.getStepCount(), nodes, edges, junctions, sim.getAgentHeads().size());
    }
}

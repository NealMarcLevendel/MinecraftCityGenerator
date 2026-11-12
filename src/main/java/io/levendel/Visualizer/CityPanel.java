package io.levendel.Visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.levendel.Core.Elevation;
import io.levendel.Core.StreetNode;

/**
 * Draws the terrain (shaded relief + contours), the street graph, and the agents.
 * The terrain image is rendered once and cached; streets are redrawn every repaint.
 */
public class CityPanel extends JPanel {

    private static final Color EDGE = new Color(250, 246, 235);
    private static final Color EDGE_CASING = new Color(40, 34, 28, 200);
    private static final Color NEW_EDGE = new Color(255, 120, 60);
    private static final Color NODE = new Color(40, 34, 28);
    private static final Color JUNCTION = new Color(200, 40, 60);
    private static final Color AGENT = new Color(30, 120, 255);

    private StreetSimulation simulation;
    private BufferedImage terrain;
    private boolean showNodes = true;
    private boolean showContours = true;
    private Point hover;

    public CityPanel(StreetSimulation simulation) {
        setBackground(new Color(24, 24, 28));
        setPreferredSize(new Dimension(800, 800));
        setSimulation(simulation);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hover = e.getPoint();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = null;
                repaint();
            }
        };
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
    }

    public void setSimulation(StreetSimulation simulation) {
        this.simulation = simulation;
        this.terrain = renderTerrain(simulation.getElevation(), showContours);
        repaint();
    }

    public void setShowNodes(boolean show) {
        showNodes = show;
        repaint();
    }

    public void setShowContours(boolean show) {
        showContours = show;
        terrain = renderTerrain(simulation.getElevation(), showContours);
        repaint();
    }

    // ---- layout: fit the terrain into the panel, keeping its aspect ratio ----

    private double scale() {
        Elevation e = simulation.getElevation();
        return Math.min((double) getWidth() / e.getWidth(), (double) getHeight() / e.getHeight());
    }

    private double offsetX() {
        return (getWidth() - simulation.getElevation().getWidth() * scale()) / 2;
    }

    private double offsetY() {
        return (getHeight() - simulation.getElevation().getHeight() * scale()) / 2;
    }

    private double sx(double x) {
        return offsetX() + (x + 0.5) * scale();
    }

    private double sy(double z) {
        return offsetY() + (z + 0.5) * scale();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        Elevation e = simulation.getElevation();
        double s = scale();
        g.drawImage(terrain, (int) offsetX(), (int) offsetY(),
                (int) Math.round(e.getWidth() * s), (int) Math.round(e.getHeight() * s), null);

        Map<StreetNode, Set<StreetNode>> connections = simulation.getConnections();
        float streetWidth = (float) Math.max(1.5, s * 1.2);

        // Edges: dark casing, then light fill, so streets read on any terrain colour.
        drawEdges(g, connections, new BasicStroke(streetWidth + 2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), EDGE_CASING);
        drawEdges(g, connections, new BasicStroke(streetWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), EDGE);

        g.setColor(NEW_EDGE);
        g.setStroke(new BasicStroke(streetWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (StreetNode[] edge : simulation.getLastStepEdges()) {
            g.draw(new Line2D.Double(sx(edge[0].x()), sy(edge[0].z()), sx(edge[1].x()), sy(edge[1].z())));
        }

        if (showNodes) {
            double r = Math.max(1.5, s * 0.9);
            for (Map.Entry<StreetNode, Set<StreetNode>> entry : connections.entrySet()) {
                StreetNode n = entry.getKey();
                boolean junction = entry.getValue().size() >= 3;
                double rr = junction ? r * 1.6 : r;
                g.setColor(junction ? JUNCTION : NODE);
                g.fill(new Ellipse2D.Double(sx(n.x()) - rr, sy(n.z()) - rr, rr * 2, rr * 2));
            }
        }

        double ar = Math.max(4, s * 2.2);
        g.setStroke(new BasicStroke(2f));
        for (StreetNode head : simulation.getAgentHeads()) {
            Ellipse2D dot = new Ellipse2D.Double(sx(head.x()) - ar, sy(head.z()) - ar, ar * 2, ar * 2);
            g.setColor(AGENT);
            g.fill(dot);
            g.setColor(Color.WHITE);
            g.draw(dot);
        }

        drawHover(g, e);
        g.dispose();
    }

    private void drawEdges(Graphics2D g, Map<StreetNode, Set<StreetNode>> connections, Stroke stroke, Color color) {
        g.setStroke(stroke);
        g.setColor(color);
        for (Map.Entry<StreetNode, Set<StreetNode>> entry : connections.entrySet()) {
            StreetNode a = entry.getKey();
            for (StreetNode b : entry.getValue()) {
                // each undirected edge once
                if (a.x() < b.x() || (a.x() == b.x() && a.z() < b.z())) {
                    g.draw(new Line2D.Double(sx(a.x()), sy(a.z()), sx(b.x()), sy(b.z())));
                }
            }
        }
    }

    private void drawHover(Graphics2D g, Elevation e) {
        if (hover == null)
            return;
        int x = (int) Math.floor((hover.x - offsetX()) / scale());
        int z = (int) Math.floor((hover.y - offsetY()) / scale());
        if (x < 0 || z < 0 || x >= e.getWidth() || z >= e.getHeight())
            return;
        String text = String.format("x %d  z %d  (world %d, %d)  y %d",
                x, z, x + e.offsetX, z + e.offsetZ, e.get(x, z));
        g.setFont(getFont().deriveFont(12f));
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(text) + 12;
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRoundRect(8, getHeight() - 30, w, 22, 8, 8);
        g.setColor(Color.WHITE);
        g.drawString(text, 14, getHeight() - 14);
    }

    // ---- terrain rendering ----

    /** Hypsometric tint + hillshade + contour lines, one pixel per elevation cell. */
    public static BufferedImage renderTerrain(Elevation e, boolean contours) {
        int w = e.getWidth(), h = e.getHeight();
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int z = 0; z < h; z++)
            for (int x = 0; x < w; x++) {
                min = Math.min(min, e.get(x, z));
                max = Math.max(max, e.get(x, z));
            }
        double range = Math.max(1, max - min);

        // light from the north-west
        double lx = -1, lz = -1, ly = 1.5;
        double ll = Math.sqrt(lx * lx + lz * lz + ly * ly);
        lx /= ll; lz /= ll; ly /= ll;

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int z = 0; z < h; z++) {
            for (int x = 0; x < w; x++) {
                int v = e.get(x, z);
                double t = (v - min) / range;
                Color base = ramp(t);

                double dx = (e.get(Math.min(w - 1, x + 1), z) - e.get(Math.max(0, x - 1), z)) / 2.0;
                double dz = (e.get(x, Math.min(h - 1, z + 1)) - e.get(x, Math.max(0, z - 1))) / 2.0;
                double nx = -dx, nz = -dz, ny = 1;
                double nl = Math.sqrt(nx * nx + nz * nz + ny * ny);
                double shade = (nx * lx + nz * lz + ny * ly) / nl; // 0..1
                double k = 0.55 + 0.6 * shade;

                int r = clamp((int) (base.getRed() * k));
                int g = clamp((int) (base.getGreen() * k));
                int b = clamp((int) (base.getBlue() * k));

                if (contours && x + 1 < w && z + 1 < h) {
                    int interval = 4;
                    int c = Math.floorDiv(v, interval);
                    if (c != Math.floorDiv(e.get(x + 1, z), interval) || c != Math.floorDiv(e.get(x, z + 1), interval)) {
                        r = (int) (r * 0.78);
                        g = (int) (g * 0.78);
                        b = (int) (b * 0.78);
                    }
                }
                img.setRGB(x, z, (r << 16) | (g << 8) | b);
            }
        }
        return img;
    }

    private static final double[] STOPS = { 0.0, 0.25, 0.5, 0.75, 1.0 };
    private static final Color[] COLORS = {
            new Color(86, 130, 96), new Color(132, 166, 104), new Color(196, 190, 128),
            new Color(170, 134, 96), new Color(236, 232, 226) };

    private static Color ramp(double t) {
        for (int i = 0; i < STOPS.length - 1; i++) {
            if (t <= STOPS[i + 1]) {
                double f = (t - STOPS[i]) / (STOPS[i + 1] - STOPS[i]);
                Color a = COLORS[i], b = COLORS[i + 1];
                return new Color(
                        (int) (a.getRed() + f * (b.getRed() - a.getRed())),
                        (int) (a.getGreen() + f * (b.getGreen() - a.getGreen())),
                        (int) (a.getBlue() + f * (b.getBlue() - a.getBlue())));
            }
        }
        return COLORS[COLORS.length - 1];
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    /** Renders the current state off-screen (used for --snapshot). */
    public BufferedImage snapshot(int width, int height) {
        setSize(width, height);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        paint(g);
        g.dispose();
        return img;
    }
}

package com.jscratch.ui;

import com.jscratch.Project.MapMakerData;
import com.jscratch.Project.LayerData;
import com.jscratch.WorkspaceManager;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapMakerPanel extends JPanel {
    private MapMakerData mapData;
    private int selectedTile = 1;
    private int currentLayerIndex = 1; // Default to "Main"
    private int hoveredX = -1, hoveredY = -1;

    private JPanel gridCanvas;
    private JPanel sidebar;
    private Map<Integer, Image> tileImages = new HashMap<>();

    public MapMakerPanel(MapMakerData data) {
        this.mapData = data;
        setLayout(new BorderLayout());

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));

        loadTileImages();
        setupSidebar();

        gridCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
            }
        };
        gridCanvas.setBackground(new Color(40, 44, 52));
        updateGridCanvasSize();

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { paintTile(e); }
            @Override public void mouseDragged(MouseEvent e) { paintTile(e); }
            @Override public void mouseMoved(MouseEvent e) {
                int ds = mapData.tileSize * mapData.displayScale;
                hoveredX = e.getX() / ds;
                hoveredY = e.getY() / ds;
                gridCanvas.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                hoveredX = -1; hoveredY = -1;
                gridCanvas.repaint();
            }
        };
        gridCanvas.addMouseListener(ma);
        gridCanvas.addMouseMotionListener(ma);

        add(sidebar, BorderLayout.WEST);
        add(new JScrollPane(gridCanvas), BorderLayout.CENTER);
    }

    private void updateGridCanvasSize() {
        int ds = mapData.tileSize * mapData.displayScale;
        gridCanvas.setPreferredSize(new Dimension(mapData.width * ds, mapData.height * ds));
        gridCanvas.revalidate();
    }

    private void setupSidebar() {
        sidebar.removeAll();
        sidebar.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.gridx = 0;
        int gy = 0;

        gbc.gridy = gy++;
        sidebar.add(new JLabel("MAP SETTINGS"), gbc);

        // Tile Size
        gbc.gridy = gy++;
        JPanel tsPanel = new JPanel(new BorderLayout());
        tsPanel.add(new JLabel("Tile Size: "), BorderLayout.WEST);
        JSpinner tsSpinner = new JSpinner(new SpinnerNumberModel(mapData.tileSize, 1, 128, 1));
        tsSpinner.addChangeListener(e -> {
            mapData.tileSize = (int) tsSpinner.getValue();
            updateGridCanvasSize();
            repaint();
        });
        tsPanel.add(tsSpinner, BorderLayout.CENTER);
        sidebar.add(tsPanel, gbc);

        // Grid Width
        gbc.gridy = gy++;
        JPanel wPanel = new JPanel(new BorderLayout());
        wPanel.add(new JLabel("Width: "), BorderLayout.WEST);
        JSpinner wSpinner = new JSpinner(new SpinnerNumberModel(mapData.width, 1, 500, 1));
        wSpinner.addChangeListener(e -> {
            int newW = (int) wSpinner.getValue();
            resizeGrid(newW, mapData.height);
            updateGridCanvasSize();
            repaint();
        });
        wPanel.add(wSpinner, BorderLayout.CENTER);
        sidebar.add(wPanel, gbc);

        // Grid Height
        gbc.gridy = gy++;
        JPanel hPanel = new JPanel(new BorderLayout());
        hPanel.add(new JLabel("Height: "), BorderLayout.WEST);
        JSpinner hSpinner = new JSpinner(new SpinnerNumberModel(mapData.height, 1, 500, 1));
        hSpinner.addChangeListener(e -> {
            int newH = (int) hSpinner.getValue();
            resizeGrid(mapData.width, newH);
            updateGridCanvasSize();
            repaint();
        });
        hPanel.add(hSpinner, BorderLayout.CENTER);
        sidebar.add(hPanel, gbc);

        // Display Scale
        gbc.gridy = gy++;
        JPanel sPanel = new JPanel(new BorderLayout());
        sPanel.add(new JLabel("View Scale: "), BorderLayout.WEST);
        JSpinner sSpinner = new JSpinner(new SpinnerNumberModel(mapData.displayScale, 1, 20, 1));
        sSpinner.addChangeListener(e -> {
            mapData.displayScale = (int) sSpinner.getValue();
            updateGridCanvasSize();
            repaint();
        });
        sPanel.add(sSpinner, BorderLayout.CENTER);
        sidebar.add(sPanel, gbc);

        gbc.gridy = gy++;
        sidebar.add(new JSeparator(), gbc);

        gbc.gridy = gy++;
        sidebar.add(new JLabel("LAYERS"), gbc);
        ButtonGroup bg = new ButtonGroup();
        for (int i = 0; i < mapData.layers.size(); i++) {
            final int idx = i;
            JRadioButton rb = new JRadioButton(mapData.layers.get(i).name, i == currentLayerIndex);
            rb.addActionListener(e -> currentLayerIndex = idx);
            bg.add(rb);
            gbc.gridy = gy++;
            sidebar.add(rb, gbc);
        }

        gbc.gridy = gy++;
        sidebar.add(new JSeparator(), gbc);
        gbc.gridy = gy++;
        sidebar.add(new JLabel("TILES"), gbc);

        JPanel palette = new JPanel(new GridLayout(0, 2, 5, 5));
        for (int i = 1; i <= 20; i++) {
            if (!mapData.tileAssets.containsKey(i) && i > 1 && !mapData.tileAssets.containsKey(i-1)) break;
            final int tileId = i;
            JButton b = new JButton() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Image img = tileImages.get(tileId);
                    if (img != null) g.drawImage(img, 5, 5, getWidth()-10, getHeight()-10, null);
                    else {
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(5, 5, getWidth()-10, getHeight()-10);
                        g.setColor(Color.BLACK);
                        g.drawString(""+tileId, 10, 20);
                    }
                    if (selectedTile == tileId) {
                        g.setColor(Color.YELLOW);
                        g.drawRect(2, 2, getWidth()-4, getHeight()-4);
                    }
                }
            };
            b.setPreferredSize(new Dimension(40, 40));
            b.addActionListener(e -> {
                selectedTile = tileId;
                if (!mapData.tileAssets.containsKey(tileId)) configureTile(tileId);
                repaint();
            });
            palette.add(b);
        }
        gbc.gridy = gy++;
        sidebar.add(palette, gbc);

        gbc.gridy = gy++;
        JButton configBtn = new JButton("Config Selected Tile");
        configBtn.addActionListener(e -> configureTile(selectedTile));
        sidebar.add(configBtn, gbc);

        gbc.gridy = gy++;
        JButton exportBtn = new JButton("Export PNG");
        exportBtn.addActionListener(e -> exportToPNG());
        sidebar.add(exportBtn, gbc);

        // Filler to push everything up
        gbc.gridy = gy++;
        gbc.weighty = 1.0;
        sidebar.add(Box.createGlue(), gbc);
        
        sidebar.revalidate();
    }

    private void resizeGrid(int newW, int newH) {
        if (newW == mapData.width && newH == mapData.height) return;
        for (LayerData layer : mapData.layers) {
            int[][] newTiles = new int[newW][newH];
            for (int x = 0; x < Math.min(newW, mapData.width); x++) {
                for (int y = 0; y < Math.min(newH, mapData.height); y++) {
                    newTiles[x][y] = layer.tiles[x][y];
                }
            }
            layer.tiles = newTiles;
        }
        mapData.width = newW;
        mapData.height = newH;
    }

    private void paintTile(MouseEvent e) {
        int ds = mapData.tileSize * mapData.displayScale;
        int tx = e.getX() / ds;
        int ty = e.getY() / ds;
        if (tx >= 0 && tx < mapData.width && ty >= 0 && ty < mapData.height) {
            LayerData layer = mapData.layers.get(currentLayerIndex);
            layer.tiles[tx][ty] = SwingUtilities.isLeftMouseButton(e) ? selectedTile : 0;
            gridCanvas.repaint();
        }
    }

    private void drawGrid(Graphics g, boolean showGridLines) {
        if (mapData == null) return;
        int ds = mapData.tileSize * (showGridLines ? mapData.displayScale : 1);

        for (int i = 0; i < mapData.layers.size(); i++) {
            LayerData layer = mapData.layers.get(i);
            float alpha = (i == currentLayerIndex || !showGridLines) ? 1.0f : 0.4f;
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            
            for (int x = 0; x < mapData.width; x++) {
                for (int y = 0; y < mapData.height; y++) {
                    int tileId = layer.tiles[x][y];
                    if (tileId > 0) {
                        Image img = tileImages.get(tileId);
                        if (img != null) {
                            g2d.drawImage(img, x * ds, y * ds, ds, ds, null);
                        } else {
                            g2d.setColor(getFallbackColor(tileId));
                            g2d.fillRect(x * ds, y * ds, ds, ds);
                        }
                    }
                }
            }
            g2d.dispose();
        }

        if (showGridLines) {
            g.setColor(new Color(60, 60, 60));
            for (int x = 0; x <= mapData.width; x++) g.drawLine(x * ds, 0, x * ds, mapData.height * ds);
            for (int y = 0; y <= mapData.height; y++) g.drawLine(0, y * ds, mapData.width * ds, y * ds);

            if (hoveredX >= 0 && hoveredX < mapData.width && hoveredY >= 0 && hoveredY < mapData.height) {
                g.setColor(new Color(255, 255, 255, 80));
                g.fillRect(hoveredX * ds, hoveredY * ds, ds, ds);
            }
        }
    }

    private void exportToPNG() {
        BufferedImage img = new BufferedImage(mapData.width * mapData.tileSize, mapData.height * mapData.tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        drawGrid(g2d, false);
        g2d.dispose();

        JFileChooser saver = new JFileChooser(WorkspaceManager.getAssetsDir());
        if (saver.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = saver.getSelectedFile();
            if (!file.getName().endsWith(".png")) file = new File(file.getAbsolutePath() + ".png");
            try {
                ImageIO.write(img, "PNG", file);
                JOptionPane.showMessageDialog(this, "Exported successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void drawGrid(Graphics g) {
        drawGrid(g, true);
    }


    private Color getFallbackColor(int id) {
        return new Color((id * 50) % 255, (id * 100) % 255, (id * 150) % 255);
    }

    private static class JButtonGroup extends ButtonGroup {
        @Override
        public void add(AbstractButton b) {
            super.add(b);
        }
    }

    public void setMapData(MapMakerData data) {
        this.mapData = data;
        loadTileImages();
        setupSidebar();
        repaint();
    }
    private void loadTileImages() {
        tileImages.clear();
        for (Map.Entry<Integer, String> entry : mapData.tileAssets.entrySet()) {
            File assetFile = new File(WorkspaceManager.getAssetsDir(), entry.getValue());
            if (assetFile.exists()) {
                try {
                    tileImages.put(entry.getKey(), ImageIO.read(assetFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void configureTile(int id) {
        File assetsDir = WorkspaceManager.getAssetsDir();
        String[] files = assetsDir.list((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg"));
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(this, "No assets found in assets folder.");
            return;
        }
        String selected = (String) JOptionPane.showInputDialog(this, "Select asset for tile " + id, "Tile Config", 
                JOptionPane.QUESTION_MESSAGE, null, files, mapData.tileAssets.get(id));
        
        if (selected != null) {
            mapData.tileAssets.put(id, selected);
            loadTileImages();
            setupSidebar();
            repaint();
        }
    }
}

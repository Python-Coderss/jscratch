package com.jscratch.ui;

import com.jscratch.WorkspaceManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.imageio.ImageIO;

public class CostumeEditorPanel extends JPanel {
    private BufferedImage currentImage;
    private String currentCostumeName;
    private Color currentColor = Color.BLACK;
    private int brushSize = 5;
    private String currentTool = "Brush";
    private double zoom = 1.0;

    private JPanel canvas;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> costumeList = new JList<>(listModel);
    private List<String> costumePaths;

    private JSpinner widthSpinner, heightSpinner;

    public CostumeEditorPanel(List<String> costumePaths) {
        this.costumePaths = costumePaths;
        setLayout(new BorderLayout());

        // --- Left Sidebar (Costume List) ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(200, 0));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));
        leftPanel.setBackground(new Color(249, 249, 249));
        
        refreshCostumeList();

        leftPanel.add(new JScrollPane(costumeList), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        btnPanel.setOpaque(false);
        
        JButton refreshBtn = new JButton("Refresh Assets");
        refreshBtn.addActionListener(e -> refreshCostumeList());
        
        JButton addBtn = new JButton("Paint New");
        addBtn.addActionListener(e -> createNewCostume());
        
        JButton importBtn = new JButton("Import Image");
        importBtn.addActionListener(e -> importCostume());
        
        btnPanel.add(refreshBtn);
        btnPanel.add(addBtn); 
        btnPanel.add(importBtn);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);
        
        // --- Toolbar ---
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(new Color(240, 240, 240));
        
        // Tools
        String[] tools = {"Brush", "Eraser", "Fill"};
        for (String tool : tools) {
            JButton b = new JButton(tool);
            b.addActionListener(e -> currentTool = tool);
            toolbar.add(b);
        }

        // Brush Size
        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        sizeSpinner.addChangeListener(e -> brushSize = (int) sizeSpinner.getValue());
        toolbar.add(new JLabel(" Size:")); toolbar.add(sizeSpinner);

        // Color
        JButton colorBtn = new JButton("Color");
        colorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Select Color", currentColor);
            if (c != null) currentColor = c;
        });
        toolbar.add(colorBtn);

        // Zoom
        JButton zoomIn = new JButton("+");
        JButton zoomOut = new JButton("-");
        zoomIn.addActionListener(e -> { zoom *= 1.2; updateCanvasLayout(); });
        zoomOut.addActionListener(e -> { zoom /= 1.2; updateCanvasLayout(); });
        toolbar.add(new JLabel(" Zoom:")); toolbar.add(zoomIn); toolbar.add(zoomOut);

        // Canvas Size
        widthSpinner = new JSpinner(new SpinnerNumberModel(480, 1, 2000, 10));
        heightSpinner = new JSpinner(new SpinnerNumberModel(360, 1, 2000, 10));
        JButton resizeBtn = new JButton("Resize");
        resizeBtn.addActionListener(e -> resizeImage((int)widthSpinner.getValue(), (int)heightSpinner.getValue()));
        
        toolbar.add(new JLabel(" W:")); toolbar.add(widthSpinner);
        toolbar.add(new JLabel(" H:")); toolbar.add(heightSpinner);
        toolbar.add(resizeBtn);

        // Save
        JButton saveBtn = new JButton("Save To Assets");
        saveBtn.addActionListener(e -> saveCurrentImage());
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(saveBtn);

        // --- Canvas ---
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentImage != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    int drawW = (int)(currentImage.getWidth() * zoom);
                    int drawH = (int)(currentImage.getHeight() * zoom);
                    
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, drawW, drawH);
                    g2.drawImage(currentImage, 0, 0, drawW, drawH, null);
                    g2.setColor(new Color(200, 200, 200));
                    g2.drawRect(0, 0, drawW, drawH);
                }
            }
        };
        canvas.setBackground(new Color(230, 230, 230));

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { useTool(e.getPoint()); }
            @Override public void mouseDragged(MouseEvent e) { useTool(e.getPoint()); }
            
            private void useTool(Point p) {
                if (currentImage == null) return;
                int imgX = (int)(p.x / zoom);
                int imgY = (int)(p.y / zoom);
                if (imgX < 0 || imgY < 0 || imgX >= currentImage.getWidth() || imgY >= currentImage.getHeight()) return;

                Graphics2D g2 = currentImage.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (currentTool.equals("Brush")) {
                    g2.setComposite(AlphaComposite.SrcOver);
                    g2.setColor(currentColor);
                    g2.fillOval(imgX - brushSize/2, imgY - brushSize/2, brushSize, brushSize);
                } else if (currentTool.equals("Eraser")) {
                    g2.setComposite(AlphaComposite.Clear);
                    g2.fillOval(imgX - brushSize/2, imgY - brushSize/2, brushSize, brushSize);
                } else if (currentTool.equals("Fill")) {
                    g2.setComposite(AlphaComposite.SrcOver);
                    g2.setColor(currentColor);
                    g2.fillRect(0, 0, currentImage.getWidth(), currentImage.getHeight());
                }
                g2.dispose();
                canvas.repaint();
            }
        };
        canvas.addMouseListener(ma);
        canvas.addMouseMotionListener(ma);

        costumeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && costumeList.getSelectedIndex() != -1) {
                String name = listModel.getElementAt(costumeList.getSelectedIndex());
                File f = new File(WorkspaceManager.getAssetsDir(), name);
                loadCostume(f.getAbsolutePath());
            }
        });

        add(leftPanel, BorderLayout.WEST);
        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(canvas), BorderLayout.CENTER);

        createNewCostume();
    }

    private void refreshCostumeList() {
        listModel.clear();
        File assetsDir = WorkspaceManager.getAssetsDir();
        if (assetsDir.exists()) {
            File[] files = assetsDir.listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
            });
            if (files != null) {
                for (File f : files) {
                    if (!listModel.contains(f.getName())) {
                        listModel.addElement(f.getName());
                        if (!costumePaths.contains(f.getAbsolutePath())) {
                            costumePaths.add(f.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

    private void updateCanvasLayout() {
        if (currentImage != null) {
            canvas.setPreferredSize(new Dimension((int)(currentImage.getWidth() * zoom), (int)(currentImage.getHeight() * zoom)));
            canvas.revalidate();
            canvas.repaint();
        }
    }

    private void resizeImage(int newW, int newH) {
        if (currentImage == null) return;
        BufferedImage newImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImg.createGraphics();
        g.drawImage(currentImage, 0, 0, null);
        g.dispose();
        currentImage = newImg;
        updateCanvasLayout();
    }

    private void createNewCostume() {
        int w = (int)widthSpinner.getValue();
        int h = (int)heightSpinner.getValue();
        currentImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        currentCostumeName = "costume" + (listModel.size() + 1) + ".png";
        updateCanvasLayout();
    }

    private void importCostume() {
        JFileChooser fc = new JFileChooser(WorkspaceManager.getRoot());
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File src = fc.getSelectedFile();
                File dest = new File(WorkspaceManager.getAssetsDir(), src.getName());
                Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                refreshCostumeList();
                loadCostume(dest.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error importing: " + e.getMessage());
            }
        }
    }

    private void loadCostume(String path) {
        try {
            File f = new File(path);
            if (f.exists()) {
                currentImage = ImageIO.read(f);
                currentCostumeName = f.getName();
                widthSpinner.setValue(currentImage.getWidth());
                heightSpinner.setValue(currentImage.getHeight());
                updateCanvasLayout();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCurrentImage() {
        if (currentImage == null) return;
        String name = JOptionPane.showInputDialog(this, "Save as (e.g. hero.png):", currentCostumeName);
        if (name == null || name.trim().isEmpty()) return;
        if (!name.toLowerCase().endsWith(".png")) name += ".png";
        
        try {
            File dest = new File(WorkspaceManager.getAssetsDir(), name);
            ImageIO.write(currentImage, "PNG", dest);
            currentCostumeName = name;
            refreshCostumeList();
            JOptionPane.showMessageDialog(this, "Saved to assets/" + name);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving: " + e.getMessage());
        }
    }
}


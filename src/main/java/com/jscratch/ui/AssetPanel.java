package com.jscratch.ui;

import com.jscratch.WorkspaceManager;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class AssetPanel extends JPanel {
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> list = new JList<>(listModel);
    private JLabel previewLabel = new JLabel("No Preview", SwingConstants.CENTER);
    private String type;
    private List<String> currentAssets;

    public AssetPanel(String type, List<String> assets) {
        this.type = type;
        this.currentAssets = assets;
        setLayout(new BorderLayout());
        
        refreshList(assets);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(200, 0));
        
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add " + type);
        addBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser(WorkspaceManager.getAssetsDir());
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    File dest = new File(WorkspaceManager.getAssetsDir(), file.getName());
                    Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    currentAssets.add(file.getName());
                    refreshList(currentAssets);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error copying asset: " + ex.getMessage());
                }
            }
        });
        toolbar.add(addBtn);
        
        leftPanel.add(toolbar, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(list), BorderLayout.CENTER);
        
        previewLabel.setBorder(BorderFactory.createTitledBorder("Preview"));
        
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedIndex() != -1) {
                updatePreview(list.getSelectedValue());
            }
        });

        add(leftPanel, BorderLayout.WEST);
        add(previewLabel, BorderLayout.CENTER);
    }

    public void refreshList(List<String> assets) {
        this.currentAssets = assets;
        listModel.clear();
        for (String a : assets) listModel.addElement(a);
        previewLabel.setIcon(null);
        previewLabel.setText("No Preview");
    }

    private void updatePreview(String filename) {
        File file = new File(WorkspaceManager.getAssetsDir(), filename);
        if (!file.exists()) {
            previewLabel.setText("File not found: " + filename);
            return;
        }

        if (type.equals("Costume")) {
            try {
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image img = icon.getImage();
                // Scale to fit while maintaining aspect ratio
                int w = img.getWidth(null);
                int h = img.getHeight(null);
                double scale = Math.min(400.0 / w, 400.0 / h);
                if (scale > 1.0) scale = 1.0;
                
                Image scaled = img.getScaledInstance((int)(w * scale), (int)(h * scale), Image.SCALE_SMOOTH);
                previewLabel.setIcon(new ImageIcon(scaled));
                previewLabel.setText("");
            } catch (Exception e) {
                previewLabel.setIcon(null);
                previewLabel.setText("Error loading image");
            }
        } else {
            previewLabel.setText("Audio Asset: " + filename);
        }
    }
}

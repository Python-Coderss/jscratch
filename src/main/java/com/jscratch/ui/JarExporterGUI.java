package com.jscratch.ui;

import com.jscratch.WorkspaceManager;
import com.jscratch.compiler.JarExporter;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * GUI for the JarExporter.
 */
public class JarExporterGUI extends JDialog {
    private JTextField targetField;
    private JLabel statusLabel;
    private JButton exportBtn;

    public JarExporterGUI(Frame parent) {
        super(parent, "Export Standalone JAR", true);
        setSize(500, 200);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Output JAR:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        targetField = new JTextField(new File(WorkspaceManager.getRoot(), "ExportedGame.jar").getAbsolutePath());
        panel.add(targetField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        JButton browseBtn = new JButton("Browse...");
        browseBtn.addActionListener(e -> browse());
        panel.add(browseBtn, gbc);

        add(panel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusLabel = new JLabel("Ready.");
        bottomPanel.add(statusLabel);
        
        exportBtn = new JButton("Export");
        exportBtn.addActionListener(e -> startExport());
        bottomPanel.add(exportBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void browse() {
        JFileChooser chooser = new JFileChooser(WorkspaceManager.getRoot());
        chooser.setSelectedFile(new File(targetField.getText()));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            targetField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void startExport() {
        exportBtn.setEnabled(false);
        statusLabel.setText("Exporting...");
        
        new Thread(() -> {
            try {
                File target = new File(targetField.getText());
                File bin = WorkspaceManager.getBinDir();
                File src = WorkspaceManager.getSrcDir();
                File assets = WorkspaceManager.getAssetsDir();
                
                // Automatically find engine classes directory
                File engineDir = findEngineClassesDir();
                
                if (engineDir == null || !engineDir.exists()) {
                    throw new Exception("Could not find engine classes directory. Make sure you are running from an IDE (bin or target/classes).");
                }

                JarExporter.export(target, bin, src, assets, engineDir);
                
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Export Successful!");
                    JOptionPane.showMessageDialog(this, "Project exported successfully to:\n" + target.getAbsolutePath());
                    dispose();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Export Failed.");
                    JOptionPane.showMessageDialog(this, "Export Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    exportBtn.setEnabled(true);
                });
            }
        }).start();
    }

    private File findEngineClassesDir() {
        try {
            // Get location of the JarExporter class
            URL location = JarExporter.class.getProtectionDomain().getCodeSource().getLocation();
            File file = new File(location.toURI());
            if (file.isDirectory()) {
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Fallbacks for standard Eclipse/Maven structures
        File targetClasses = new File("target/classes");
        if (targetClasses.exists()) return targetClasses;
        
        File bin = new File("bin");
        if (bin.exists()) return bin;
        
        return null;
    }
}

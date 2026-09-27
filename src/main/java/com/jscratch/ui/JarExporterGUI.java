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
    private final String projectMainClass;

    /** Export using the selected project's fully-qualified entry point. */
    public JarExporterGUI(Frame parent, String projectMainClass) {
        super(parent, "Export Standalone JAR", true);
        this.projectMainClass = projectMainClass == null || projectMainClass.trim().isEmpty()
                ? "generated.Main" : projectMainClass.trim();
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
                    throw new Exception("Could not find engine classes (running jar or target/classes).");
                }

                JarExporter.export(target, bin, src, assets, engineDir, projectMainClass);
                
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Export Successful!");
                    JOptionPane.showMessageDialog(this, "Project exported successfully to:\n" + target.getAbsolutePath()
                            + "\n\nAssets were copied next to the jar:\n" + new File(target.getAbsoluteFile().getParentFile(), "jscratch_ws/assets").getAbsolutePath());
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
            // Get location of the JarExporter class: a classes directory when
            // running from an IDE, or the editor jar file itself otherwise.
            // JarExporter reads engine entries straight out of the jar, so the
            // export always matches the running editor (no stale build tree).
            URL location = JarExporter.class.getProtectionDomain().getCodeSource().getLocation();
            File file = new File(location.toURI());
            if (file.isDirectory() || (file.isFile() && file.getName().endsWith(".jar"))) {
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
    /** Backward-compatible default for standalone use of this dialog. */
    public JarExporterGUI(Frame parent) {
        this(parent, "generated.Main");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JarExporterGUI gui = new JarExporterGUI(null);
            gui.setVisible(true);
        });
    }
}

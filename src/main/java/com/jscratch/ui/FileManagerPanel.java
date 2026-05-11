package com.jscratch.ui;

import com.jscratch.WorkspaceManager;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class FileManagerPanel extends JPanel {
    private JTree fileTree;
    private DefaultTreeModel treeModel;

    public FileManagerPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        setupToolbar();
        refresh();
    }

    private void setupToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(240, 240, 240));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        toolBar.add(refreshBtn);
        
        toolBar.addSeparator();
        
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteSelected());
        toolBar.add(deleteBtn);

        JButton renameBtn = new JButton("Rename");
        renameBtn.addActionListener(e -> renameSelected());
        toolBar.add(renameBtn);

        add(toolBar, BorderLayout.NORTH);
    }

    public void refresh() {
        File rootDir = WorkspaceManager.getRoot();
        if (!rootDir.exists()) {
            WorkspaceManager.ensureDirs();
        }
        
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new FileNode(rootDir));
        buildTree(rootDir, rootNode);
        
        if (fileTree == null) {
            treeModel = new DefaultTreeModel(rootNode);
            fileTree = new JTree(treeModel);
            fileTree.setCellRenderer(new FileTreeCellRenderer());
            fileTree.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        int selRow = fileTree.getRowForLocation(e.getX(), e.getY());
                        if (selRow != -1) {
                            fileTree.setSelectionRow(selRow);
                            showContextMenu(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        viewSelectedFile();
                    }
                }
            });
            JScrollPane scrollPane = new JScrollPane(fileTree);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            add(scrollPane, BorderLayout.CENTER);
        } else {
            treeModel.setRoot(rootNode);
            treeModel.reload();
        }
        revalidate();
        repaint();
    }

    private void buildTree(File file, DefaultMutableTreeNode node) {
        File[] list = file.listFiles();
        if (list == null) return;
        
        Arrays.sort(list, (a, b) -> {
            if (a.isDirectory() && !b.isDirectory()) return -1;
            if (!a.isDirectory() && b.isDirectory()) return 1;
            return a.getName().compareToIgnoreCase(b.getName());
        });

        for (File f : list) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(new FileNode(f));
            node.add(child);
            if (f.isDirectory()) buildTree(f, child);
        }
    }

    private void showContextMenu(Component invoker, int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem view = new JMenuItem("View Content");
        view.addActionListener(e -> viewSelectedFile());
        menu.add(view);
        
        JMenuItem rename = new JMenuItem("Rename");
        rename.addActionListener(e -> renameSelected());
        menu.add(rename);
        
        menu.addSeparator();
        
        JMenuItem delete = new JMenuItem("Delete");
        delete.addActionListener(e -> deleteSelected());
        menu.add(delete);
        
        menu.show(invoker, x, y);
    }

    private void viewSelectedFile() {
        File f = getSelectedFile();
        if (f != null && f.isFile()) {
            try {
                String content = new String(Files.readAllBytes(f.toPath()));
                JTextArea textArea = new JTextArea(content);
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scroll = new JScrollPane(textArea);
                scroll.setPreferredSize(new Dimension(600, 400));
                JOptionPane.showMessageDialog(this, scroll, "Viewing: " + f.getName(), JOptionPane.PLAIN_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Could not read file: " + e.getMessage());
            }
        }
    }

    private void renameSelected() {
        File f = getSelectedFile();
        if (f == null) return;
        
        String newName = JOptionPane.showInputDialog(this, "Enter new name:", f.getName());
        if (newName != null && !newName.trim().isEmpty()) {
            File dest = new File(f.getParentFile(), newName.trim());
            if (f.renameTo(dest)) {
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Rename failed. Ensure the name is valid and not in use.");
            }
        }
    }

    private void deleteSelected() {
        File f = getSelectedFile();
        if (f == null) return;
        
        int res = JOptionPane.showConfirmDialog(this, "Delete " + f.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            if (deleteRecursively(f)) {
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }
        }
    }

    private boolean deleteRecursively(File f) {
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File child : children) deleteRecursively(child);
            }
        }
        return f.delete();
    }

    private File getSelectedFile() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
        if (node == null) return null;
        FileNode fileNode = (FileNode) node.getUserObject();
        return fileNode.file;
    }

    private static class FileNode {
        File file;
        FileNode(File file) { this.file = file; }
        @Override public String toString() { 
            return file.getName().isEmpty() ? file.getPath() : file.getName();
        }
    }

    private class FileTreeCellRenderer extends DefaultTreeCellRenderer {
        private Icon folderIcon = UIManager.getIcon("FileView.directoryIcon");
        private Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node.getUserObject() instanceof FileNode) {
                File file = ((FileNode) node.getUserObject()).file;
                if (file.isDirectory()) {
                    setIcon(folderIcon);
                } else {
                    setIcon(fileIcon);
                }
            }
            return this;
        }
    }
}

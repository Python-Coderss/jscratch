package com.jscratch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jscratch.ui.*;
import com.jscratch.Project.SpriteData;
import com.jscratch.compiler.CompilerService;
import com.jscratch.compiler.ProjectRunner;
import com.jscratch.compiler.CompilationResult;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

public class ScratchEditor extends JFrame {
    private JTabbedPane tabbedPane;
    private Project project;
    private Stage stagePreview;
    
    private CodeWorkspace codeWorkspace;
    private CostumeEditorPanel costumePanel;
    private AssetPanel soundPanel;
    private MapMakerPanel mapMakerPanel;
    private FileManagerPanel fileManagerPanel;
    
    /** Package folders and Java source files shown in the Code workspace tree. */
    private DefaultMutableTreeNode sourceRoot = new DefaultMutableTreeNode("Sources");
    private DefaultTreeModel sourceTreeModel = new DefaultTreeModel(sourceRoot);
    private JTree spriteList = new JTree(sourceTreeModel);

    private JTextArea consoleArea;
    private File currentProjectFile;
    /** Package declaration/path mismatches are rejected before writing compiler sources. */
    private final List<String> sourceLayoutErrors = new ArrayList<>();

    public ScratchEditor() {
        WorkspaceManager.ensureDirs();
        setTitle("JScratch - Java IDE");
        setSize(1400, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        getContentPane().setBackground(new Color(77, 151, 255));

        project = new Project();
        
        stagePreview = new Stage();
        stagePreview.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2));
        
        setupTabs();
        setupConsole();
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel stageContainer = new JPanel(new BorderLayout());
        stageContainer.add(stagePreview, BorderLayout.CENTER);
        stageContainer.setOpaque(false);
        rightPanel.add(stageContainer, BorderLayout.NORTH);
        
        JPanel spritePanel = new JPanel(new BorderLayout());
        spritePanel.setBackground(Color.WHITE);
        spritePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        spriteList.getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
        spriteList.setRootVisible(false);
        spriteList.setShowsRootHandles(true);
        spritePanel.add(new JScrollPane(spriteList), BorderLayout.CENTER);
        
        JPopupMenu spriteMenu = new JPopupMenu();
        JMenuItem renameItem = new JMenuItem("Rename");
        renameItem.addActionListener(e -> renameSprite());
        spriteMenu.add(renameItem);
        
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> deleteSprite());
        spriteMenu.add(deleteItem);
        
        spriteList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) showMenu(e);
            }
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) showMenu(e);
            }
            private void showMenu(java.awt.event.MouseEvent e) {
                TreePath path = spriteList.getPathForLocation(e.getX(), e.getY());
                if (path != null && path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                    Object value = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                    if (value instanceof SpriteData) {
                        spriteList.setSelectionPath(path);
                        spriteMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
        
        JPanel sourceActions = new JPanel(new GridLayout(2, 1, 0, 4));
        JButton addSpriteBtn = new JButton("Add Source");
        addSpriteBtn.addActionListener(e -> addSprite());
        JButton movePackageBtn = new JButton("Move to Package...");
        movePackageBtn.addActionListener(e -> moveSelectedSourcePackage());
        sourceActions.add(addSpriteBtn);
        sourceActions.add(movePackageBtn);
        spritePanel.add(sourceActions, BorderLayout.SOUTH);
        
        rightPanel.add(spritePanel, BorderLayout.CENTER);

        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, new JScrollPane(consoleArea));
        leftSplit.setDividerLocation(600);
        leftSplit.setResizeWeight(0.7);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, rightPanel);
        mainSplit.setDividerLocation(1000);
        add(mainSplit, BorderLayout.CENTER);
        
        setJMenuBar(createMenuBar());
        
        spriteList.addTreeSelectionListener(e -> {
            SpriteData sd = getSelectedSource();
            if (sd != null) {
                codeWorkspace.saveCurrentCode();
                codeWorkspace.loadSpriteCode(sd);
                soundPanel.refreshList(sd.sounds);
            }
        });

        syncWithSourceFiles();
        ensureMainSprite();
        refreshSpriteList();
        
        // Initial compile to populate autocomplete and highlights
        autoCompile();
        
        setLocationRelativeTo(null);
    }

    private void setupConsole() {
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(30, 30, 30));
        consoleArea.setForeground(new Color(200, 200, 200));
        consoleArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        consoleArea.setMargin(new Insets(5, 5, 5, 5));

        PrintStream consoleStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                appendToConsole(String.valueOf((char) b));
            }
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                appendToConsole(new String(b, off, len));
            }
        });
        System.setOut(consoleStream);
        System.setErr(consoleStream);
    }

    private void appendToConsole(String text) {
        SwingUtilities.invokeLater(() -> {
            consoleArea.append(text);
            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
        });
    }

    private void logToConsole(String message) {
        appendToConsole(message + "\n");
    }

    private void clearConsole() {
        SwingUtilities.invokeLater(() -> consoleArea.setText(""));
    }

    private void refreshPackageErrors() {
        sourceLayoutErrors.clear();
        File srcRoot = WorkspaceManager.getSrcDir();
        List<File> files = new ArrayList<>();
        addJavaFilesRecursive(srcRoot, files);
        for (File file : files) {
            try {
                String declared = packageOf(new String(Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8));
                String folder = packageForFolder(srcRoot, file.getParentFile());
                if (!declared.equals(folder)) sourceLayoutErrors.add(file.getPath() + " declares package '"
                        + displayPackage(declared) + "' but is in package folder '" + displayPackage(folder) + "'.");
            } catch (IOException ex) {
                sourceLayoutErrors.add("Could not read " + file.getPath() + ": " + ex.getMessage());
            }
        }
    }

    private void ensureMainSprite() {
        boolean found = false;
        for (SpriteData sd : project.sprites) {
            if (sd.name.equals("Main")) {
                found = true;
                break;
            }
        }
        if (!found) {
            SpriteData main = new SpriteData("Main");
            main.sourceCode = generateDefaultMainCode();
            project.sprites.add(0, main);
        } else {
            for (SpriteData sprite : project.sprites) {
                if ("Main".equals(sprite.name) && sprite.sourceCode.trim().isEmpty()) {
                    sprite.packageName = "generated";
                    sprite.sourceCode = generateDefaultMainCode();
                    break;
                }
            }
        }
    }

    private void syncWithSourceFiles() {
        File srcDir = WorkspaceManager.getSrcDir();
        if (!srcDir.exists() || !srcDir.isDirectory()) return;

        List<File> allJavaFiles = new ArrayList<>();
        addJavaFilesRecursive(srcDir, allJavaFiles);
        sourceLayoutErrors.clear();
        java.util.Set<String> discoveredTypes = new java.util.HashSet<>();

        for (File f : allJavaFiles) {
            try {
                String content = new String(Files.readAllBytes(f.toPath()), java.nio.charset.StandardCharsets.UTF_8);
                String declaredPackage = packageOf(content);
                String folderPackage = packageForFolder(srcDir, f.getParentFile());
                if (!folderPackage.equals(declaredPackage)) {
                    sourceLayoutErrors.add(f.getPath() + " declares package '" + displayPackage(declaredPackage)
                            + "' but is in package folder '" + displayPackage(folderPackage) + "'.");
                }
                String className = f.getName().substring(0, f.getName().length() - ".java".length());
                String relativeSourcePath = srcDir.toPath().relativize(f.toPath()).toString().replace('\\', '/');
                String qualifiedName = qualifiedName(declaredPackage, className);
                String compilerTypeName = typeNameForPath(relativeSourcePath);
                discoveredTypes.add(compilerTypeName);
                // Every workspace source is migrated to comment-annotated Scratch blocks
                // in memory; compilation still sees ordinary Java because markers are comments.
                String blockSource = ScratchBlockAnnotator.annotate(content, className);
                boolean found = false;
                for (SpriteData sd : project.sprites) {
                    // Reuse the two default project placeholders when a source
                    // file is in the unnamed package; all other files match by FQN.
                    boolean sameIdentity = sd.getQualifiedName().equals(qualifiedName)
                            || compilerTypeName.equals(classNameForSourcePath(sd));
                    boolean emptyPlaceholder = sd.sourceCode.trim().isEmpty()
                            && sd.name.equals(className);
                    if (sameIdentity || emptyPlaceholder) {
                        sd.name = className;
                        sd.packageName = declaredPackage;
                        sd.sourcePath = relativeSourcePath;
                        sd.sourceCode = blockSource;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    SpriteData sd = new SpriteData(className);
                    sd.packageName = declaredPackage;
                    sd.sourcePath = relativeSourcePath;
                    sd.sourceCode = blockSource;
                    project.sprites.add(sd);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Retain editor-created sources that have not yet been written to the workspace.
        project.sprites.removeIf(sprite -> sprite.sourcePath != null && !sprite.sourcePath.trim().isEmpty()
                && !discoveredTypes.contains(classNameForSourcePath(sprite)));
        if (!sourceLayoutErrors.isEmpty()) {
            logToConsole("Source package layout needs attention before compiling:");
            for (String error : sourceLayoutErrors) logToConsole("  " + error);
        }
    }

    private void addJavaFilesRecursive(File dir, List<File> list) {
        File[] children = dir.listFiles();
        if (children == null) return;
        for (File child : children) {
            if (child.isDirectory()) {
                addJavaFilesRecursive(child, list);
            } else if (child.getName().endsWith(".java")) {
                list.add(child);
            }
        }
    }

    /** Rebuild the collapsible, segment-by-segment package tree from project sources. */
    private void refreshSpriteList() {
        sourceRoot.removeAllChildren();
        java.util.Map<String, DefaultMutableTreeNode> packages = new java.util.TreeMap<>();
        for (SpriteData sd : project.sprites) {
            String packageName = sd.packageName == null ? "" : sd.packageName.trim();
            DefaultMutableTreeNode parent = sourceRoot;
            if (!packageName.isEmpty()) {
                StringBuilder path = new StringBuilder();
                for (String segment : packageName.split("\\.")) {
                    if (path.length() > 0) path.append('.');
                    path.append(segment);
                    String key = path.toString();
                    DefaultMutableTreeNode packageNode = packages.get(key);
                    if (packageNode == null) {
                        packageNode = new DefaultMutableTreeNode(segment);
                        packages.put(key, packageNode);
                        parent.add(packageNode);
                    }
                    parent = packageNode;
                }
            } else if (packages.get("") == null) {
                DefaultMutableTreeNode defaultPackage = new DefaultMutableTreeNode("(default package)");
                packages.put("", defaultPackage);
                sourceRoot.add(defaultPackage);
                parent = defaultPackage;
            } else {
                parent = packages.get("");
            }
            parent.add(new DefaultMutableTreeNode(sd));
        }
        boolean hasUnselectedPackageErrors = false;
        for (String error : sourceLayoutErrors) {
            boolean represented = false;
            for (SpriteData source : project.sprites) {
                if (error.contains(source.name + ".java")) { represented = true; break; }
            }
            if (!represented) { hasUnselectedPackageErrors = true; break; }
        }
        if (hasUnselectedPackageErrors) {
            DefaultMutableTreeNode warning = new DefaultMutableTreeNode("Package path mismatch — see console");
            warning.setAllowsChildren(false);
            sourceRoot.add(warning);
        }
        sourceTreeModel.reload();
        // Keep package groups collapsible. Only reveal a path when selecting a
        // source file (or when there is no previous selection at all).
        SpriteData initial = null;
        for (SpriteData sd : project.sprites) if ("Main".equals(sd.name)) { initial = sd; break; }
        if (initial == null && !project.sprites.isEmpty()) initial = project.sprites.get(0);
        if (initial != null) {
            for (int row = 0; row < spriteList.getRowCount(); row++) {
                Object value = ((DefaultMutableTreeNode) spriteList.getPathForRow(row).getLastPathComponent()).getUserObject();
                if (value == initial) {
                    TreePath sourcePath = spriteList.getPathForRow(row);
                    if (sourcePath != null) spriteList.expandPath(sourcePath.getParentPath());
                    break;
                }
            }
            selectSource(initial);
            codeWorkspace.loadSpriteCode(initial);
            soundPanel.refreshList(initial.sounds);
        }
    }

    /** Return the selected source leaf (package folder selections are not editable). */
    private SpriteData getSelectedSource() {
        TreePath path = spriteList.getSelectionPath();
        if (path == null) return null;
        Object node = path.getLastPathComponent();
        if (!(node instanceof DefaultMutableTreeNode)) return null;
        Object value = ((DefaultMutableTreeNode) node).getUserObject();
        return value instanceof SpriteData ? (SpriteData) value : null;
    }

    /** Select a source file by object identity after rebuilding the package tree. */
    private void selectSource(SpriteData source) {
        java.util.Enumeration<?> nodes = sourceRoot.depthFirstEnumeration();
        while (nodes.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
            if (node.getUserObject() == source) {
                spriteList.setSelectionPath(new TreePath(node.getPath()));
                return;
            }
        }
    }

    /** Read a Java package declaration; an omitted declaration means the default package. */
    private static String packageOf(String source) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("(?m)^\\s*package\\s+([A-Za-z_$][\\w$]*(?:\\.[A-Za-z_$][\\w$]*)*)\\s*;")
                .matcher(source == null ? "" : source);
        return matcher.find() ? matcher.group(1) : "";
    }

    /** Translate a source directory into its expected dotted Java package. */
    private static String packageForFolder(File sourceRoot, File directory) {
        String relative = sourceRoot.getAbsoluteFile().toPath().relativize(directory.getAbsoluteFile().toPath()).toString();
        return relative.replace(File.separatorChar, '.').replace('/', '.').replace('\\', '.');
    }

    /** Infer a type name from a workspace-relative Java file path. */
    private static String typeNameForPath(String relativePath) {
        String normalized = relativePath.replace('\\', '/');
        if (normalized.endsWith(".java")) normalized = normalized.substring(0, normalized.length() - 5);
        return normalized.replace('/', '.');
    }

    private static String qualifiedName(String packageName, String typeName) {
        return packageName == null || packageName.trim().isEmpty()
                ? typeName : packageName.trim() + "." + typeName;
    }

    /** Strip the package from an FQN; default-package types have no separator. */
    private static String simpleName(String qualifiedType) {
        int separator = qualifiedType.lastIndexOf('.');
        return separator < 0 ? qualifiedType : qualifiedType.substring(separator + 1);
    }

    private static String displayPackage(String packageName) {
        return packageName == null || packageName.isEmpty() ? "(default package)" : packageName;
    }

    /** Extract the Java type name (package and class) from a qualified compiler identity. */
    private static String sourceTypeForPackageAndClass(String packageName, String typeName) {
        return packageName == null || packageName.isEmpty() ? typeName : packageName + "." + typeName;
    }

    /** Ensure the package field in the navigator agrees with the actual Java source. */
    private List<String> validateProjectPackages() {
        List<String> errors = new ArrayList<>();
        for (SpriteData sprite : project.sprites) {
            String declared = packageOf(sprite.sourceCode);
            String selected = sprite.packageName == null ? "" : sprite.packageName.trim();
            if (sprite.sourcePath == null || sprite.sourcePath.trim().isEmpty()) {
                // Older project JSON uses its class identity as its path.
                sprite.sourcePath = sprite.getQualifiedName().replace('.', '/') + ".java";
            }
            String sourcePathPackage = packageForSourcePath(sprite.getSourcePath());
            if (!declared.equals(selected)) {
                errors.add(sprite.getQualifiedName() + " declares package '" + displayPackage(declared)
                        + "' but the source tree identifies it as '" + displayPackage(selected) + "'.");
            }
            if (!sourcePathPackage.equals(selected)) {
                errors.add(sprite.getSourcePath() + " is under package folder '" + displayPackage(sourcePathPackage)
                        + "' but its type belongs to '" + displayPackage(selected) + "'.");
            }
        }
        errors.addAll(sourceLayoutErrors);
        return errors;
    }

    /** Expected Java package derived from a project-relative .java path. */
    private static String packageForSourcePath(String sourcePath) {
        if (sourcePath == null || sourcePath.trim().isEmpty()) return "";
        String normalized = sourcePath.replace('\\', '/');
        int separator = normalized.lastIndexOf('/');
        return separator < 0 ? "" : normalized.substring(0, separator).replace('/', '.');
    }

    private String generateDefaultMainCode() {
        return "package generated;\n" +
               "import com.jscratch.Stage;\n" +
               "import com.jscratch.Sprite;\n\n" +
               "public class Main {\n" +
               "    public static void main(String[] args) {\n" +
               "        Stage stage = Stage.getInstance();\n" +
               "        // Auto-generated: add all sprites\n" +
               "        /* slot start [id:\"init\" tags:\"init\"] */\n" +
               "        \n" +
               "        /* slot end [id:\"init\"] */\n" +
               "    }\n" +
               "}";
    }

    private void addSprite() {
        String qualifiedType = JOptionPane.showInputDialog(this,
                "Enter a Java type or fully qualified name (for example: game.ui.HudSprite):",
                "Sprite" + (project.sprites.size() + 1));
        if (qualifiedType == null || qualifiedType.trim().isEmpty()) return;
        qualifiedType = qualifiedType.trim();
        String[] nameParts = qualifiedType.split("\\.");
        String name = nameParts[nameParts.length - 1];
        String packageName = nameParts.length == 1 ? "generated"
                : qualifiedType.substring(0, qualifiedType.length() - name.length() - 1);
        if (!name.matches("[A-Za-z_$][A-Za-z0-9_$]*")
                || (!packageName.isEmpty() && !packageName.matches("[A-Za-z_$][A-Za-z0-9_$]*(\\.[A-Za-z_$][A-Za-z0-9_$]*)*"))) {
            JOptionPane.showMessageDialog(this, "Use valid Java identifiers separated by dots.", "Invalid type name", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (SpriteData other : project.sprites) {
            if (other.getQualifiedName().equals(qualifiedName(packageName, name))) {
                JOptionPane.showMessageDialog(this, "That fully qualified type already exists.", "Duplicate type", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        SpriteData sd = new SpriteData(name);
        sd.packageName = packageName;
        String packageLine = packageName.isEmpty() ? "" : "package " + packageName + ";\n\n";
        String template = packageLine +
            "import com.jscratch.*;\n" +
            "import java.awt.Color;\n" +
            "import java.awt.event.KeyEvent;\n\n" +
            "public class " + name + " extends Sprite {\n" +
            "    /* slot start [id:\"" + name + "_members\" tags:\"inClassNotMethod\"] */\n" +
            "    public " + name + "() {\n" +
            "        super(\"" + name + "\");\n" +
            "        \n" +
            "        // Example: addCostume(new Costume(\"costume1\", \"path/to/img.png\"));\n\n" +
            "        /* slot start [id:\"" + name + "_init\" tags:\"inInstanceMethod\"] */\n" +
            "        /* slot end [id:\"" + name + "_init\"] */\n" +
            "    }\n" +
            "    /* slot end [id:\"" + name + "_members\"] */\n" +
            "}";
        sd.sourceCode = template;
        sd.sourcePath = qualifiedName(packageName, name).replace('.', '/') + ".java";
        project.sprites.add(sd);
        refreshSpriteList();
        selectSource(sd);
    }

    /** Change a source's package and its declaration together, keeping folder identity valid. */
    private void moveSelectedSourcePackage() {
        SpriteData source = getSelectedSource();
        if (source == null) return;
        codeWorkspace.saveCurrentCode();
        String currentPackage = source.packageName == null ? "" : source.packageName;
        String requested = JOptionPane.showInputDialog(this,
                "Enter the package name (blank for the default package):", currentPackage);
        if (requested == null) return;
        String newPackage = requested.trim();
        if (!newPackage.isEmpty() && !newPackage.matches("[A-Za-z_$][A-Za-z0-9_$]*(\\.[A-Za-z_$][A-Za-z0-9_$]*)*")) {
            JOptionPane.showMessageDialog(this, "Use valid Java identifiers separated by dots.", "Invalid package", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String oldPackage = source.packageName == null ? "" : source.packageName.trim();
        String newQualifiedName = qualifiedName(newPackage, source.name);
        String sourceTypeName = classNameForSourcePath(source);
        String sourceSimpleName = simpleName(sourceTypeName);
        String newSourceType = sourceTypeForPackageAndClass(newPackage, sourceSimpleName);
        for (SpriteData other : project.sprites) {
            if (other != source && (newQualifiedName.equals(other.getQualifiedName())
                    || newSourceType.equals(classNameForSourcePath(other)))) {
                JOptionPane.showMessageDialog(this, "That fully qualified type already exists.", "Duplicate type", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        java.util.regex.Pattern declaration = java.util.regex.Pattern.compile("(?m)^\\s*package\\s+[A-Za-z_$][\\w$]*(?:\\.[A-Za-z_$][\\w$]*)*\\s*;\\s*");
        java.util.regex.Matcher matcher = declaration.matcher(source.sourceCode);
        String oldSourceCode = source.sourceCode;
        String oldSourcePath = source.getSourcePath();
        String packageLine = newPackage.isEmpty() ? "" : "package " + newPackage + ";\n";
        if (matcher.find()) source.sourceCode = matcher.replaceFirst(java.util.regex.Matcher.quoteReplacement(packageLine));
        else if (!newPackage.isEmpty()) source.sourceCode = packageLine + source.sourceCode;
        source.packageName = newPackage;
        source.sourcePath = newSourceType.replace('.', '/') + ".java";
        try {
            relocateWorkspaceSource(oldSourcePath, source, newSourceType);
        } catch (IOException ex) {
            source.packageName = oldPackage;
            source.sourceCode = oldSourceCode;
            source.sourcePath = oldSourcePath;
            JOptionPane.showMessageDialog(this, "Could not move the source file: " + ex.getMessage(), "Move failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Custom basenames survive moves; only their package folders change.
        refreshPackageErrors();
        refreshSpriteList();
        selectSource(source);
        codeWorkspace.reloadSpriteCode(source);
        autoCompile();
    }

    /** Move an on-disk source file along with its package metadata. */
    private void relocateWorkspaceSource(String oldRelativePath, SpriteData source, String newQualifiedName) throws IOException {
        File root = WorkspaceManager.getSrcDir();
        File oldFile = new File(root, oldRelativePath.replace('/', File.separatorChar));
        File newFile = new File(root, newQualifiedName.replace('.', File.separatorChar) + ".java");


        if (!oldFile.exists()) return;
        File parent = newFile.getParentFile();
        if (parent != null) Files.createDirectories(parent.toPath());
        if (!oldFile.equals(newFile)) {
            try {
                Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException ignored) {
                Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        source.sourcePath = root.toPath().relativize(newFile.toPath()).toString().replace('\\', '/');
    }

    private void renameSprite() {
        SpriteData sd = getSelectedSource();
        if (sd == null) return;
        if (sd.name.equals("Main")) {
            JOptionPane.showMessageDialog(this, "Cannot rename Main sprite.");
            return;
        }

        String newName = JOptionPane.showInputDialog(this, "Enter New Sprite Name:", sd.name);
        if (newName == null || newName.trim().isEmpty() || newName.equals(sd.name)) return;
        newName = newName.trim();

        // Check for duplicates
        for (SpriteData other : project.sprites) {
            if (other != sd && other.getQualifiedName().equals(qualifiedName(sd.packageName, newName))) {
                JOptionPane.showMessageDialog(this, "A sprite with this name already exists.");
                return;
            }
        }

        String oldName = sd.name;
        String oldQualifiedName = sd.getQualifiedName();
        String oldSourcePath = sd.getSourcePath();
        String oldSourceCode = sd.sourceCode;
        sd.name = newName;
        sd.sourcePath = qualifiedName(sd.packageName, newName).replace('.', '/') + ".java";
        // Update only declarations and constructor names, never comments or calls.
        sd.sourceCode = sd.sourceCode.replaceAll("(\\bclass\\s+)" + java.util.regex.Pattern.quote(oldName) + "\\b", java.util.regex.Matcher.quoteReplacement("$1" + newName))
                .replaceAll("(\\bpublic\\s+)" + java.util.regex.Pattern.quote(oldName) + "(\\s*\\()", java.util.regex.Matcher.quoteReplacement("$1" + newName + "$2"));
        try {
            relocateWorkspaceSource(oldSourcePath, sd, sd.getQualifiedName());
        } catch (IOException ex) {
            sd.name = oldName;
            sd.sourcePath = oldSourcePath;
            sd.sourceCode = oldSourceCode;
            JOptionPane.showMessageDialog(this, "Could not move the source file: " + ex.getMessage(), "Rename failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        refreshPackageErrors();
        refreshSpriteList();
        selectSource(sd);
        codeWorkspace.loadSpriteCode(sd);
        autoCompile();
    }

    private void deleteSprite() {
        SpriteData sd = getSelectedSource();
        if (sd == null) return;
        if (sd.name.equals("Main")) {
            JOptionPane.showMessageDialog(this, "Cannot delete Main sprite.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete sprite '" + sd.name + "'?", "Delete Sprite", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            File sourceFile = new File(WorkspaceManager.getSrcDir(), sd.getSourcePath().replace('/', File.separatorChar));
            Files.deleteIfExists(sourceFile.toPath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not remove the source file: " + ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        project.sprites.remove(sd);
        refreshSpriteList();
        autoCompile();
    }

    private void setupTabs() {
        tabbedPane = new JTabbedPane();
        codeWorkspace = new CodeWorkspace(project, () -> autoCompile());
        costumePanel = new CostumeEditorPanel(new ArrayList<>());
        soundPanel = new AssetPanel("Sound", new ArrayList<>());
        mapMakerPanel = new MapMakerPanel(project.mapMaker);
        fileManagerPanel = new FileManagerPanel();
        
        tabbedPane.addTab("Code", codeWorkspace);
        tabbedPane.addTab("Costumes", costumePanel);
        tabbedPane.addTab("Sound", soundPanel);
        tabbedPane.addTab("Map Maker", mapMakerPanel);
        tabbedPane.addTab("Files", fileManagerPanel);
    }
    
    private void autoCompile() {
        new Thread(() -> {
            File srcBackup = createSrcBackup();
            CompilationResult res = runCompile();
            SwingUtilities.invokeLater(() -> {
                codeWorkspace.setCompilationErrors(res.errors);
                if (res.success) {
                    ProjectRunner.mount(WorkspaceManager.getBinDir());
                    deleteRecursive(srcBackup);
                } else {
                    restoreSrcBackup(srcBackup);
                }
            });
        }).start();
    }

    private JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New Project");
        newItem.addActionListener(e -> newProject());
        JMenuItem openItem = new JMenuItem("Open Project...");
        openItem.addActionListener(e -> openProject());
        JMenuItem saveItem = new JMenuItem("Save Project");
        saveItem.addActionListener(e -> saveProject());
        JMenuItem saveAsItem = new JMenuItem("Save Project As...");
        saveAsItem.addActionListener(e -> saveProjectAs());
        JMenuItem exportJarItem = new JMenuItem("Export Standalone JAR...");
        exportJarItem.addActionListener(e -> exportJar());
        
        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exportJarItem);
        
        JMenu runMenu = new JMenu("Run");
        JMenuItem runItem = new JMenuItem("Run Project");
        runItem.addActionListener(e -> runAndRunProject());
        runMenu.add(runItem);
        
        mb.add(fileMenu);
        mb.add(runMenu);
        return mb;
    }

    private void newProject() {
        project = new Project();
        ensureMainSprite();
        refreshSpriteList();
        currentProjectFile = null;
        clearConsole();
        logToConsole("New Project Created.");
    }

    private void openProject() {
        JFileChooser chooser = new JFileChooser(WorkspaceManager.getRoot());
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                unzipProject(file);
                currentProjectFile = file;
                ensureMainSprite();
                refreshSpriteList();
                mapMakerPanel.setMapData(project.mapMaker);
                clearConsole();
                logToConsole("Project Loaded: " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading project: " + e.getMessage());
            }
        }
    }

    private void unzipProject(File zipFile) throws IOException {
        File assetsDir = WorkspaceManager.getAssetsDir();
        if (assetsDir.exists()) {
            File[] files = assetsDir.listFiles();
            if (files != null) for (File f : files) deleteRecursive(f);
        } else {
            assetsDir.mkdirs();
        }

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("project.json")) {
                    InputStreamReader reader = new InputStreamReader(zis);
                    project = new Gson().fromJson(reader, Project.class);
                } else if (entry.getName().startsWith("assets/")) {
                    String filename = entry.getName().substring(7);
                    if (filename.isEmpty()) {
                        zis.closeEntry();
                        continue;
                    }
                    File outFile = new File(assetsDir, filename);
                    Files.copy(zis, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    private void saveProject() {
        if (currentProjectFile == null) {
            saveProjectAs();
        } else {
            saveProjectToFile(currentProjectFile);
        }
    }

    private void saveProjectAs() {
        JFileChooser chooser = new JFileChooser(WorkspaceManager.getRoot());
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".jsz")) {
                file = new File(file.getAbsolutePath() + ".jsz");
            }
            saveProjectToFile(file);
            currentProjectFile = file;
        }
    }

    private void saveProjectToFile(File file) {
        codeWorkspace.saveCurrentCode();
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
            zos.putNextEntry(new ZipEntry("project.json"));
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(project);
            zos.write(json.getBytes());
            zos.closeEntry();

            File assetsDir = WorkspaceManager.getAssetsDir();
            if (assetsDir.exists()) {
                File[] files = assetsDir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        zos.putNextEntry(new ZipEntry("assets/" + f.getName()));
                        Files.copy(f.toPath(), zos);
                        zos.closeEntry();
                    }
                }
            }
            logToConsole("Project Saved: " + file.getName());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving project: " + e.getMessage());
        }
    }

    private void exportJar() {
        logToConsole("Compiling project before export...");
        new Thread(() -> {
            File srcBackup = createSrcBackup();
            CompilationResult res = runCompile();
            SwingUtilities.invokeLater(() -> {
                codeWorkspace.setCompilationErrors(res.errors);
                if (res.success) {
                    ProjectRunner.mount(WorkspaceManager.getBinDir());
                    deleteRecursive(srcBackup);
                    new JarExporterGUI(this, findMainClass()).setVisible(true);
                } else {
                    restoreSrcBackup(srcBackup);
                    logToConsole("--- Export aborted: build failed ---");
                    logToConsole(res.output);
                }
            });
        }).start();
    }

    private void runAndRunProject() {
        new Thread(() -> {
            File srcBackup = createSrcBackup();
            CompilationResult res = runCompile();
            SwingUtilities.invokeLater(() -> {
                codeWorkspace.setCompilationErrors(res.errors);
                if (res.success) {
                    logToConsole("--- Build Successful ---");
                    ProjectRunner.mount(WorkspaceManager.getBinDir());
                    ProjectRunner.run(WorkspaceManager.getBinDir(), findMainPackage(), "Main");
                    deleteRecursive(srcBackup);
                } else {
                    logToConsole("--- Build Failed ---");
                    logToConsole(res.output);
                    restoreSrcBackup(srcBackup);
                }
            });
        }).start();
    }
    
    /** Fully-qualified project entry point used by the runner and exported game. */
    private String findMainClass() {
        for (SpriteData sd : project.sprites) if ("Main".equals(sd.name)) return classNameForSourcePath(sd);
        return "generated.Main";
    }

    private String findMainPackage() {
        for (SpriteData sd : project.sprites) if ("Main".equals(sd.name)) return packageForSourcePath(sd.getSourcePath());
        return "generated";
    }

    private CompilationResult runCompile() {
        codeWorkspace.saveCurrentCode();
        File binDir = WorkspaceManager.getBinDir();
        File srcDir = WorkspaceManager.getSrcDir();
        List<String> packageErrors = validateProjectPackages();
        if (!packageErrors.isEmpty()) {
            CompilationResult invalid = new CompilationResult();
            invalid.success = false;
            StringBuilder message = new StringBuilder("Package declarations and source folders must match:\n");
            for (String error : packageErrors) message.append(" - ").append(error).append('\n');
            invalid.output = message.toString();
            return invalid;
        }

        // 1. Clear ONLY bin
        if (binDir.exists()) {
            File[] files = binDir.listFiles();
            if (files != null) for (File f : files) deleteRecursive(f);
        }
        binDir.mkdirs();

        // 2. Remove stale source files from all project package folders while
        // preserving unrelated files and package trees outside this project.
        cleanupProjectSources(srcDir);
        
        // The declared Java package, not a UI category, determines where each source file is built.
        int spriteCount = project.sprites.size();
        String[] sourceCode = new String[spriteCount];
        String[] classNames = new String[spriteCount];
        for (int i = 0; i < spriteCount; i++) {
            SpriteData sprite = project.sprites.get(i);
            sourceCode[i] = sprite.sourceCode;
            classNames[i] = classNameForSourcePath(sprite);
        }

        return CompilerService.compile(sourceCode, classNames, srcDir, binDir);
    }

    /** Compiler identity follows the source path; normally this equals the declared FQN. */
    private String classNameForSourcePath(SpriteData sprite) {
        String path = sprite.getSourcePath().replace('\\', '/');
        if (path.endsWith(".java")) path = path.substring(0, path.length() - 5);
        return path.replace('/', '.');
    }

    /** Remove stale Java files only from package folders owned by this project. */
    private void cleanupProjectSources(File directory) {
        File[] children = directory.listFiles();
        if (children == null) return;
        for (File child : children) {
            if (child.isDirectory()) {
                cleanupProjectSources(child);
                String[] remaining = child.list();
                if (remaining != null && remaining.length == 0) child.delete();
            } else if (child.getName().endsWith(".java")) {
                String relative = WorkspaceManager.getSrcDir().toPath().relativize(child.toPath()).toString().replace('\\', '/');
                String qualified = typeNameForPath(relative);
                boolean inProject = false;
                for (SpriteData sd : project.sprites) {
                    if (qualified.equals(classNameForSourcePath(sd))) { inProject = true; break; }
                }
                if (!inProject && belongsToProjectPackage(child)) child.delete();
            }
        }
    }

    /** Do not clean arbitrary source packages; only remove stale files from packages represented here. */
    private boolean belongsToProjectPackage(File sourceFile) {
        String folderPackage = packageForFolder(WorkspaceManager.getSrcDir(), sourceFile.getParentFile());
        for (SpriteData sprite : project.sprites) {
            if (folderPackage.equals(packageForSourcePath(sprite.getSourcePath()))) return true;
        }
        return false;
    }

    private File createSrcBackup() {
        File srcDir = WorkspaceManager.getSrcDir();
        File backupDir = new File(srcDir.getParentFile(), "src_backup_" + System.currentTimeMillis());
        try {
            copyRecursive(srcDir, backupDir);
        } catch (IOException e) { e.printStackTrace(); }
        return backupDir;
    }

    private void restoreSrcBackup(File backupDir) {
        File srcDir = WorkspaceManager.getSrcDir();
        deleteRecursive(srcDir);
        try {
            copyRecursive(backupDir, srcDir);
        } catch (IOException e) { e.printStackTrace(); }
        deleteRecursive(backupDir);
    }

    private void copyRecursive(File source, File dest) throws IOException {
        if (source.isDirectory()) {
            if (!dest.exists()) dest.mkdirs();
            String[] children = source.list();
            if (children != null) {
                for (String child : children) {
                    copyRecursive(new File(source, child), new File(dest, child));
                }
            }
        } else {
            Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteRecursive(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) for (File child : files) deleteRecursive(child);
        }
        f.delete();
    }

    public static void main(String[] args) {
        try { 
            // CHANGE THIS LINE: Use CrossPlatform instead of System
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); 
        } catch (Exception e) {
            // Fallback if needed
        }
        SwingUtilities.invokeLater(() -> new ScratchEditor().setVisible(true));
    }
}

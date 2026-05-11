package com.jscratch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jscratch.ui.*;
import com.jscratch.Project.SpriteData;
import com.jscratch.compiler.CompilerService;
import com.jscratch.compiler.ProjectRunner;
import com.jscratch.compiler.CompilationResult;
import javax.swing.*;
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
    
    private DefaultListModel<String> spriteListModel = new DefaultListModel<>();
    private JList<String> spriteList = new JList<>(spriteListModel);

    private JTextArea consoleArea;
    private File currentProjectFile;

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
        
        spriteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
                int index = spriteList.locationToIndex(e.getPoint());
                if (index != -1) {
                    spriteList.setSelectedIndex(index);
                    spriteMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        JButton addSpriteBtn = new JButton("Add Sprite");
        addSpriteBtn.addActionListener(e -> addSprite());
        spritePanel.add(addSpriteBtn, BorderLayout.SOUTH);
        
        rightPanel.add(spritePanel, BorderLayout.CENTER);

        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, new JScrollPane(consoleArea));
        leftSplit.setDividerLocation(600);
        leftSplit.setResizeWeight(0.7);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, rightPanel);
        mainSplit.setDividerLocation(1000);
        add(mainSplit, BorderLayout.CENTER);
        
        setJMenuBar(createMenuBar());
        
        spriteList.addListSelectionListener(e -> {
            int idx = spriteList.getSelectedIndex();
            if (idx != -1) {
                codeWorkspace.saveCurrentCode();
                SpriteData sd = project.sprites.get(idx);
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
        }
    }

    private void syncWithSourceFiles() {
        File srcDir = WorkspaceManager.getSrcDir();
        if (!srcDir.exists() || !srcDir.isDirectory()) return;

        List<File> allJavaFiles = new ArrayList<>();
        addJavaFilesRecursive(srcDir, allJavaFiles);

        for (File f : allJavaFiles) {
            String className = f.getName().replace(".java", "");
            try {
                String content = new String(Files.readAllBytes(f.toPath()));
                boolean found = false;
                for (SpriteData sd : project.sprites) {
                    if (sd.name.equals(className)) {
                        sd.sourceCode = content;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    SpriteData sd = new SpriteData(className);
                    sd.sourceCode = content;
                    project.sprites.add(sd);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void refreshSpriteList() {
        spriteListModel.clear();
        for (SpriteData sd : project.sprites) {
            spriteListModel.addElement(sd.name);
        }
        if (!project.sprites.isEmpty()) {
            spriteList.setSelectedIndex(0);
            SpriteData sd = project.sprites.get(0);
            codeWorkspace.loadSpriteCode(sd);
            soundPanel.refreshList(sd.sounds);
        }
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
        String name = JOptionPane.showInputDialog(this, "Enter Sprite Name:", "Sprite" + (project.sprites.size() + 1));
        if (name == null || name.trim().isEmpty()) return;
        name = name.trim();
        
        SpriteData sd = new SpriteData(name);
        String template = "package generated;\n\n" +
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
        project.sprites.add(sd);
        spriteListModel.addElement(name);
        spriteList.setSelectedIndex(project.sprites.size() - 1);
    }

    private void renameSprite() {
        int idx = spriteList.getSelectedIndex();
        if (idx == -1) return;
        SpriteData sd = project.sprites.get(idx);
        if (sd.name.equals("Main")) {
            JOptionPane.showMessageDialog(this, "Cannot rename Main sprite.");
            return;
        }

        String newName = JOptionPane.showInputDialog(this, "Enter New Sprite Name:", sd.name);
        if (newName == null || newName.trim().isEmpty() || newName.equals(sd.name)) return;
        newName = newName.trim();

        // Check for duplicates
        for (SpriteData other : project.sprites) {
            if (other.name.equals(newName)) {
                JOptionPane.showMessageDialog(this, "A sprite with this name already exists.");
                return;
            }
        }

        String oldName = sd.name;
        sd.name = newName;
        
        // Update source code
        sd.sourceCode = sd.sourceCode.replace(oldName, newName);

        spriteListModel.set(idx, newName);
        codeWorkspace.loadSpriteCode(sd);
        autoCompile();
    }

    private void deleteSprite() {
        int idx = spriteList.getSelectedIndex();
        if (idx == -1) return;
        SpriteData sd = project.sprites.get(idx);
        if (sd.name.equals("Main")) {
            JOptionPane.showMessageDialog(this, "Cannot delete Main sprite.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete sprite '" + sd.name + "'?", "Delete Sprite", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        project.sprites.remove(idx);
        spriteListModel.remove(idx);

        if (project.sprites.size() > 0) {
            int newIdx = Math.min(idx, project.sprites.size() - 1);
            spriteList.setSelectedIndex(newIdx);
            codeWorkspace.loadSpriteCode(project.sprites.get(newIdx));
        }
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
        
        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        
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

    private void runAndRunProject() {
        new Thread(() -> {
            File srcBackup = createSrcBackup();
            CompilationResult res = runCompile();
            SwingUtilities.invokeLater(() -> {
                codeWorkspace.setCompilationErrors(res.errors);
                if (res.success) {
                    logToConsole("--- Build Successful ---");
                    ProjectRunner.mount(WorkspaceManager.getBinDir());
                    ProjectRunner.run(WorkspaceManager.getBinDir(), "Main");
                    deleteRecursive(srcBackup);
                } else {
                    logToConsole("--- Build Failed ---");
                    logToConsole(res.output);
                    restoreSrcBackup(srcBackup);
                }
            });
        }).start();
    }
    
    private CompilationResult runCompile() {
        codeWorkspace.saveCurrentCode();
        File binDir = WorkspaceManager.getBinDir();
        File srcDir = WorkspaceManager.getSrcDir();
        File generatedSrcDir = new File(srcDir, "generated");
        
        // 1. Clear ONLY bin
        if (binDir.exists()) {
            File[] files = binDir.listFiles();
            if (files != null) for (File f : files) deleteRecursive(f);
        }
        binDir.mkdirs();

        // 2. Targeted cleanup of 'generated' package source (orphaned files)
        // This removes .java files that are no longer in our project sprite list
        if (generatedSrcDir.exists()) {
            File[] files = generatedSrcDir.listFiles((dir, name) -> name.endsWith(".java"));
            if (files != null) {
                for (File f : files) {
                    String className = f.getName().replace(".java", "");
                    boolean existsInProject = false;
                    for (SpriteData sd : project.sprites) {
                        if (sd.name.equals(className)) { existsInProject = true; break; }
                    }
                    if (!existsInProject) f.delete();
                }
            }
        } else {
            generatedSrcDir.mkdirs();
        }
        
        int spriteCount = project.sprites.size();
        String[] sourceCode = new String[spriteCount];
        String[] classNames = new String[spriteCount];
        for (int i = 0; i < spriteCount; i++) {
            SpriteData sprite = project.sprites.get(i);
            sourceCode[i] = sprite.sourceCode;
            classNames[i] = "generated." + sprite.name;
        }

        return CompilerService.compile(sourceCode, classNames, srcDir, binDir);
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

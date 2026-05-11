package com.jscratch.ui;

import com.jscratch.Project;
import com.jscratch.Project.SpriteData;
import com.jscratch.compiler.ProjectRunner;
import com.jscratch.compiler.CompilationResult;
import java.lang.reflect.*;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rtextarea.RTextScrollPane;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Set;

public class CodeWorkspace extends JPanel {
    private RSyntaxTextArea codeEditor;
    private JTabbedPane paletteTabs;
    private Project project;
    private SpriteData currentSprite;
    private AutoCompletion autoCompletion;
    
    private final List<SlotRange> currentSlots = new ArrayList<>();
    private Object activeHoverHighlight;
    private Object activeDropHighlight;
    
    private final Highlighter.HighlightPainter slotPainter = new Highlighter.HighlightPainter() {
        @Override public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
            try {
                Rectangle r0 = c.modelToView(p0);
                Rectangle r1 = c.modelToView(p1);
                if (r0 == null || r1 == null) return;
                g.setColor(new Color(0, 120, 215, 30));
                g.fillRect(0, r0.y, c.getWidth(), (r1.y + r1.height) - r0.y);
            } catch (Exception e) {}
        }
    };
    
    private final Highlighter.HighlightPainter dropPainter = new Highlighter.HighlightPainter() {
        @Override public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
            try {
                Rectangle r0 = c.modelToView(p0);
                Rectangle r1 = c.modelToView(p1);
                if (r0 == null || r1 == null) return;
                g.setColor(new Color(0, 255, 0, 50));
                g.fillRect(0, r0.y, c.getWidth(), (r1.y + r1.height) - r0.y);
            } catch (Exception e) {}
        }
    };
    
    private boolean isUpdatingSlots = false;
    
    private Timer autoCompileTimer;
    private Runnable onAutoCompile;
    private CustomParser errorParser;
    private String lastCompiledText = "";

    public static final DataFlavor BLOCK_FLAVOR = new DataFlavor(BlockSnippet.class, "BlockSnippet");

    public static class BlockSnippet implements Serializable {
        public String name;
        public String category;
        public String code;
        public List<String> tags = new ArrayList<>();
    }

    private static class SlotRange {
        int start;
        int end;
        int startCommentEnd;
        int endCommentStart;
        String id;
        List<String> tags = new ArrayList<>();
        boolean isOnlyOne = false;
        
        boolean hasTag(String tag) { return tags.contains(tag); }
        boolean contains(int offset) { return offset >= start && offset <= end; }
        int length() { return end - start; }
    }

    public CodeWorkspace(Project project, Runnable onAutoCompile) {
        this.project = project;
        this.onAutoCompile = onAutoCompile;
        setLayout(new BorderLayout());
        
        // 1. Setup Editor
        codeEditor = new RSyntaxTextArea(20, 60);
        
        // 1. Set tab size to 4
        codeEditor.setTabSize(4);
        
        // 2. Make Tab insert spaces instead of the \t character
        codeEditor.setTabsEmulated(true); 
        codeEditor.setPaintTabLines(true);
	    
	    // 3. (Optional) Auto-indent new lines based on the previous line's indent
	    codeEditor.setAutoIndentEnabled(true);
	    codeEditor.setCloseCurlyBraces(true);
	    codeEditor.setLineWrap(false);
	    codeEditor.setShowMatchedBracketPopup(true);
	    
	    Action toggleContentAction = new AbstractAction() {
	        @Override
	        public void actionPerformed(java.awt.event.ActionEvent e) {
	            // Check current text to decide which direction to convert
	            if (codeEditor.getText().contains("\t")) {
	                codeEditor.convertTabsToSpaces();
	            } else {
	                codeEditor.convertSpacesToTabs();
	            }
	        }
	    };

	    // Map Ctrl+P (Cmd+P on Mac) to the action
	    KeyStroke ctrlP = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, 
	        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

	    codeEditor.getInputMap().put(ctrlP, "toggleContentFormat");
	    codeEditor.getActionMap().put("toggleContentFormat", toggleContentAction);

	    
        codeEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        codeEditor.setCodeFoldingEnabled(true);
        codeEditor.setAntiAliasingEnabled(true);
        codeEditor.setTransferHandler(new RestrictedTransferHandler());
        
        errorParser = new CustomParser();
        codeEditor.addParser(errorParser);
        
        autoCompileTimer = new Timer(1000, e -> {
            if (this.onAutoCompile != null) {
                lastCompiledText = codeEditor.getText();
                this.onAutoCompile.run();
            }
        });
        autoCompileTimer.setRepeats(false);
        
        setupAutocomplete();
        
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml"));
            theme.apply(codeEditor);
        } catch (Exception ioe) {
            codeEditor.setBackground(Color.WHITE);
            codeEditor.setForeground(Color.BLACK);
            codeEditor.setCaretColor(Color.BLACK);
        }

        codeEditor.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { scheduleSlotRefresh(); triggerAutoCompile(); }
            public void removeUpdate(DocumentEvent e) { scheduleSlotRefresh(); triggerAutoCompile(); }
            public void changedUpdate(DocumentEvent e) { scheduleSlotRefresh(); triggerAutoCompile(); }
        });

        MouseAdapter hoverAdapter = new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) { updateHoverHighlight(e.getPoint()); }
            @Override public void mouseExited(MouseEvent e) { clearHoverHighlight(); }
        };
        codeEditor.addMouseListener(hoverAdapter);
        codeEditor.addMouseMotionListener(hoverAdapter);
        
        // Add DropTargetListener to clear highlights when drag leaves
        try {
            codeEditor.getDropTarget().addDropTargetListener(new java.awt.dnd.DropTargetAdapter() {
                @Override public void dragExit(java.awt.dnd.DropTargetEvent dte) { clearDropHighlight(); }
                @Override public void drop(java.awt.dnd.DropTargetDropEvent dtde) { clearDropHighlight(); }
            });
        } catch (Exception e) {}
        
        // 2. Setup Palette Tabs
        paletteTabs = new JTabbedPane();
        paletteTabs.setPreferredSize(new Dimension(250, 0));
        loadLiteralBlocks();

        // 3. Layout with SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, paletteTabs, new RTextScrollPane(codeEditor));
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void triggerAutoCompile() {
        if (codeEditor.getText().equals(lastCompiledText)) return;
        autoCompileTimer.restart();
    }
    
    public void setCompilationErrors(List<CompilationResult.CompilationError> errors) {
        errorParser.setErrors(errors);
        codeEditor.repaint();
    }

    private class CustomParser extends AbstractParser {
        private List<CompilationResult.CompilationError> errors = new ArrayList<>();
        
        public void setErrors(List<CompilationResult.CompilationError> errors) {
            this.errors = errors;
        }

        @Override
        public ParseResult parse(RSyntaxDocument doc, String styleKey) {
            DefaultParseResult result = new DefaultParseResult(this);
            if (currentSprite == null) return result;
            
            for (CompilationResult.CompilationError error : errors) {
                if (error.fileName.equals(currentSprite.name + ".java")) {
                    try {
                        int line = error.line - 1;
                        if (line >= 0 && line < doc.getDefaultRootElement().getElementCount()) {
                            int start = doc.getDefaultRootElement().getElement(line).getStartOffset();
                            int end = doc.getDefaultRootElement().getElement(line).getEndOffset();
                            DefaultParserNotice notice = new DefaultParserNotice(this, error.message, line, start, end - start);
                            notice.setLevel(ParserNotice.Level.ERROR);
                            result.addNotice(notice);
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            }
            return result;
        }
    }

    private void updateHoverHighlight(Point p) {
        int offset = codeEditor.viewToModel(p);
        SlotRange bestMatch = null;
        for (SlotRange slot : currentSlots) {
            if (slot.contains(offset)) {
                if (bestMatch == null || slot.length() < bestMatch.length()) {
                    bestMatch = slot;
                }
            }
        }
        clearHoverHighlight();
        if (bestMatch != null) {
            try {
                activeHoverHighlight = codeEditor.getHighlighter().addHighlight(bestMatch.start, bestMatch.end, slotPainter);
            } catch (BadLocationException ex) {}
        }
    }

    private void clearHoverHighlight() {
        if (activeHoverHighlight != null) {
            codeEditor.getHighlighter().removeHighlight(activeHoverHighlight);
            activeHoverHighlight = null;
        }
    }

    private void clearDropHighlight() {
        if (activeDropHighlight != null) {
            codeEditor.getHighlighter().removeHighlight(activeDropHighlight);
            activeDropHighlight = null;
        }
    }

    private void applyDropHighlight(SlotRange slot) {
        if (activeDropHighlight != null) return;
        try {
            activeDropHighlight = codeEditor.getHighlighter().addHighlight(slot.start, slot.end, dropPainter);
        } catch (BadLocationException ex) {}
    }

    private void loadLiteralBlocks() {
        Map<String, JPanel> categoryPanels = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/blocks.txt")))) {
            String line;
            BlockSnippet currentBlock = null;
            StringBuilder codeBuffer = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("# ")) {
                    String meta = line.substring(2);
                    String[] parts = meta.split(" : ");
                    currentBlock = new BlockSnippet();
                    currentBlock.name = parts[0].trim();
                    currentBlock.category = parts[1].trim();
                    if (parts.length > 2) {
                        for (String t : parts[2].split(",")) currentBlock.tags.add(t.trim());
                    }
                    codeBuffer.setLength(0);
                } else if (line.equals("###")) {
                    if (currentBlock != null) {
                        currentBlock.code = codeBuffer.toString().trim();
                        JPanel panel = categoryPanels.computeIfAbsent(currentBlock.category, k -> {
                            JPanel p = new JPanel();
                            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
                            p.setBackground(Color.WHITE);
                            p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                            paletteTabs.addTab(k, new JScrollPane(p));
                            return p;
                        });
                        panel.add(createBlockUI(currentBlock));
                        panel.add(Box.createVerticalStrut(5));
                        currentBlock = null;
                    }
                } else if (currentBlock != null) {
                    codeBuffer.append(line).append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JComponent createBlockUI(final BlockSnippet block) {
        JLabel label = new JLabel(block.name);
        label.setOpaque(true);
        label.setBackground(getCategoryColor(block.category));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        label.setMaximumSize(new Dimension(220, 40));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setTransferHandler(new TransferHandler() {
            @Override public int getSourceActions(JComponent c) { return COPY; }
            @Override protected Transferable createTransferable(JComponent c) { return new BlockTransferable(block); }
        });
        label.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                ((JComponent)e.getSource()).getTransferHandler().exportAsDrag((JComponent)e.getSource(), e, TransferHandler.COPY);
            }
        });
        return label;
    }

    private Color getCategoryColor(String category) {
        switch (category) {
            case "Motion": return new Color(76, 151, 255);
            case "Looks": return new Color(153, 102, 255);
            case "Sound": return new Color(207, 99, 207);
            case "Events": return new Color(255, 191, 0);
            case "Control": return new Color(255, 171, 25);
            case "Sensing": return new Color(92, 177, 214);
            case "Operators": return new Color(89, 192, 89);
            case "Variables": return new Color(255, 140, 26);
            case "My Blocks": return new Color(255, 102, 128);
            case "Structure": return new Color(214, 92, 214);
            case "Helpers": return new Color(235, 64, 52);
            
            default: return Color.GRAY;
        }
    }

    private void scheduleSlotRefresh() {
        if (isUpdatingSlots) return;
        SwingUtilities.invokeLater(this::refreshSlots);
    }

    private void refreshSlots() {
        isUpdatingSlots = true;
        currentSlots.clear();
        String text = codeEditor.getText();

        Pattern startPattern = Pattern.compile("/\\* slot start \\[(.*?)\\] \\*/");
        Matcher matcher = startPattern.matcher(text);
        while (matcher.find()) {
            String attrContent = matcher.group(1);
            SlotRange slot = new SlotRange();
            slot.start = matcher.start();
            slot.startCommentEnd = matcher.end();

            Matcher idM = Pattern.compile("id:\"(.*?)\"").matcher(attrContent);
            if (idM.find()) slot.id = idM.group(1);

            Matcher tagsM = Pattern.compile("tags:\"(.*?)\"").matcher(attrContent);
            if (tagsM.find()) {
                for (String t : tagsM.group(1).split(",")) slot.tags.add(t.trim());
            }

            Matcher onlyOneM = Pattern.compile("onlyOne:(true|false)").matcher(attrContent);
            if (onlyOneM.find()) slot.isOnlyOne = Boolean.parseBoolean(onlyOneM.group(1));

            if (slot.id == null) continue;

            String endRegex = "/\\* slot end \\[(.*?id:\"" + Pattern.quote(slot.id) + "\".*?)\\] \\*/";
            Pattern endPattern = Pattern.compile(endRegex);
            Matcher endMatcher = endPattern.matcher(text);
            if (endMatcher.find(matcher.end())) {
                slot.end = endMatcher.end();
                slot.endCommentStart = endMatcher.start();
                currentSlots.add(slot);
            }
        }
        isUpdatingSlots = false;
    }

    private static class BlockTransferable implements Transferable {
        private final BlockSnippet block;
        public BlockTransferable(BlockSnippet block) { this.block = block; }
        @Override public DataFlavor[] getTransferDataFlavors() { return new DataFlavor[]{BLOCK_FLAVOR, DataFlavor.stringFlavor}; }
        @Override public boolean isDataFlavorSupported(DataFlavor flavor) { return flavor.equals(BLOCK_FLAVOR) || flavor.equals(DataFlavor.stringFlavor); }
        @Override public Object getTransferData(DataFlavor flavor) {
            if (flavor.equals(BLOCK_FLAVOR)) return block;
            return (flavor.equals(DataFlavor.stringFlavor)) ? block.code : null;
        }
    }

    private class RestrictedTransferHandler extends TransferHandler {
        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new StringSelection(codeEditor.getSelectedText());
        }

        @Override
        public boolean canImport(TransferSupport support) {
            // Allow string paste
            if (!support.isDrop() && support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return true;
            }

            if (!support.isDrop() || !support.isDataFlavorSupported(BLOCK_FLAVOR)) return false;
            try {
                BlockSnippet block = (BlockSnippet) support.getTransferable().getTransferData(BLOCK_FLAVOR);
                JTextComponent.DropLocation dl = (JTextComponent.DropLocation) support.getDropLocation();
                int offset = dl.getIndex();
                String text = codeEditor.getText();
                
                SlotRange activeSlot = null;
                for (SlotRange slot : currentSlots) {
                    if (slot.contains(offset)) {
                        if (activeSlot == null || slot.length() < activeSlot.length()) activeSlot = slot;
                    }
                }
                
                boolean isValid = false;
                if (text.trim().isEmpty() && block.tags.contains("allowEmpty")) {
                    isValid = true;
                } else if (activeSlot == null) {
                    isValid = block.tags.contains("anywhere");
                } else {
                    boolean tagMatch = false;
                    for (String bt : block.tags) if (activeSlot.hasTag(bt)) { tagMatch = true; break; }
                    
                    if (tagMatch) {
                        if (activeSlot.isOnlyOne) {
                            String body = text.substring(activeSlot.startCommentEnd, activeSlot.endCommentStart).trim();
                            isValid = body.isEmpty();
                        } else {
                            // --- Extra Safe Insertion Checks ---
                            int line = codeEditor.getLineOfOffset(offset);
                            int lineStart = codeEditor.getLineStartOffset(line);
                            int lineEnd = codeEditor.getLineEndOffset(line);
                            String lineText = text.substring(lineStart, lineEnd);
                            
                            boolean isEmptyLine = lineText.trim().isEmpty();
                            boolean isInline = codeEditor.getLineOfOffset(activeSlot.start) == codeEditor.getLineOfOffset(activeSlot.end);
                            
                            if (isInline) {
                                if (offset >= 2 && offset <= text.length() - 2) {
                                    boolean leftSafe = Character.isWhitespace(text.charAt(offset - 1)) && Character.isWhitespace(text.charAt(offset - 2));
                                    boolean rightSafe = Character.isWhitespace(text.charAt(offset)) && Character.isWhitespace(text.charAt(offset + 1));
                                    isValid = leftSafe && rightSafe;
                                }
                            } else {
                                isValid = isEmptyLine;
                            }
                        }
                    }
                }

                clearDropHighlight();
                if (isValid && activeSlot != null) {
                    applyDropHighlight(activeSlot);
                }
                return isValid;
            } catch (Exception e) { return false; }
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) return false;

            if (!support.isDrop() && support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    String text = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    codeEditor.replaceSelection(text);
                    return true;
                } catch (Exception e) { return false; }
            }

            try {
                BlockSnippet block = (BlockSnippet) support.getTransferable().getTransferData(BLOCK_FLAVOR);
                JTextComponent.DropLocation dl = (JTextComponent.DropLocation) support.getDropLocation();
                
                String code = block.code;
                if (code.contains("${id}")) {
                    code = code.replace("${id}", String.valueOf(project.blockIdCounter++));
                }
                
                codeEditor.insert(code + "\n", dl.getIndex());
                return true;
            } catch (Exception e) { return false; }
        }
    }

    public void loadSpriteCode(SpriteData sprite) {
        if (currentSprite != null) saveCurrentCode();
        this.currentSprite = sprite;
        codeEditor.setText(sprite.sourceCode);
        lastCompiledText = sprite.sourceCode;
        codeEditor.setCaretPosition(0);
        codeEditor.discardAllEdits();
        refreshSlots();
    }

    public void saveCurrentCode() {
        if (currentSprite != null) currentSprite.sourceCode = codeEditor.getText();
    }

    private void setupAutocomplete() {
        DefaultCompletionProvider provider = new DefaultCompletionProvider() {
            @Override
            protected List<Completion> getCompletionsImpl(JTextComponent comp) {
                List<Completion> ret = new ArrayList<>();
                String text = comp.getText();
                int caret = comp.getCaretPosition();
                
                String prefix = getAlreadyEnteredText(comp);
                int lineStart = 0;
                try { lineStart = codeEditor.getLineStartOffset(codeEditor.getLineOfOffset(caret)); } catch (Exception e) {}
                String currentLine = text.substring(lineStart, caret);

                int dotIdx = -1;
                for (int i = caret - 1; i >= 0 && i >= caret - 50; i--) {
                    char c = text.charAt(i);
                    if (c == '.') { dotIdx = i; break; }
                    if (!Character.isJavaIdentifierPart(c)) break;
                }

                if (currentLine.trim().startsWith("import ")) {
                    // --- IMPORT COMPLETION ---
                    addImportPathCompletions(this, ret, currentLine, prefix);
                } else if (dotIdx != -1) {
                    // --- DOT COMPLETION ---
                    String expr = getExpressionBeforeDot(text, dotIdx);
                    Class<?> type = resolveType(expr);
                    if (type != null) {
                        boolean isStatic = isClass(expr);
                        addTypeMembers(this, ret, type, isStatic);
                    }
                } else {
                    // --- GLOBAL COMPLETION ---
                    addGlobalCompletions(this, ret);
                }
                
                List<Completion> filtered = new ArrayList<>();
                for (Completion c : ret) {
                    if (c.getReplacementText().startsWith(prefix)) filtered.add(c);
                }
                filtered.sort((a, b) -> a.getReplacementText().compareToIgnoreCase(b.getReplacementText()));
                return filtered;
            }
        };
        
        autoCompletion = new AutoCompletion(provider);
        autoCompletion.install(codeEditor);

        autoCompletion.setListCellRenderer(new CompletionCellRenderer());
        // CheerpJ Fixes:
        // 1. Use default renderer (custom one often fails to size correctly in CheerpJ)
        // autoCompletion.setListCellRenderer(new CompletionCellRenderer());
        
        // 2. Disable description window (prevents focus-fighting between two popup windows)
        autoCompletion.setShowDescWindow(false);
        
        // 3. Change trigger to Ctrl+I (Ctrl+Space often conflicts with Browser/OS IME shortcuts)
        autoCompletion.setTriggerKey(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    }

    private void addImportPathCompletions(CompletionProvider provider, List<Completion> list, String line, String prefix) {
        String trimmedLine = line.trim();
        if (trimmedLine.length() < 7) return;
        String path = trimmedLine.substring(7).trim(); // remove "import "
        if (path.endsWith(".")) path = path.substring(0, path.length() - 1);
        
        Set<String> suggestions = new HashSet<>();
        Package[] packages = Package.getPackages();
        for (Package p : packages) {
            String name = p.getName();
            if (name.startsWith(path)) {
                int startIdx = path.isEmpty() ? 0 : path.length();
                if (!path.isEmpty() && name.length() > startIdx && name.charAt(startIdx) == '.') startIdx++;
                
                if (startIdx < name.length()) {
                    String sub = name.substring(startIdx);
                    int nextDot = sub.indexOf('.');
                    suggestions.add(nextDot == -1 ? sub : sub.substring(0, nextDot));
                }
            }
        }
        
        // Also add classes in that package (if path is a full package)
        try {
            // This is limited as we can't easily list all classes in a package in standard Java
            // But we can add common ones or project ones
            if (path.equals("com.jscratch")) {
                suggestions.add("Sprite"); suggestions.add("Stage"); suggestions.add("Costume");
            } else if (path.equals("java.util")) {
                suggestions.add("List"); suggestions.add("ArrayList"); suggestions.add("HashMap"); suggestions.add("Map");
            } else if (path.equals("java.awt")) {
                suggestions.add("Color"); suggestions.add("Graphics2D");
            }
        } catch (Exception e) {}

        for (String s : suggestions) list.add(new BasicCompletion(provider, s));
    }

    private String getExpressionBeforeDot(String text, int dotIdx) {
        int start = dotIdx - 1;
        while (start >= 0) {
            char c = text.charAt(start);
            if (!Character.isJavaIdentifierPart(c) && c != '.') break;
            start--;
        }
        return text.substring(start + 1, dotIdx);
    }

    private boolean isClass(String expr) {
        try {
            Class.forName(expr); return true;
        } catch (Exception e) {
            try { Class.forName("java.lang." + expr); return true; } catch (Exception e2) {}
            try { Class.forName("java.util." + expr); return true; } catch (Exception e2) {}
            ClassLoader loader = ProjectRunner.getProjectClassLoader();
            if (loader != null) {
                try { loader.loadClass("generated." + expr); return true; } catch (Exception e3) {}
            }
        }
        return false;
    }

    private Class<?> resolveType(String expr) {
        if (expr.equals("this")) {
            if (currentSprite == null) return null;
            ClassLoader loader = ProjectRunner.getProjectClassLoader();
            if (loader != null) {
                try { return loader.loadClass("generated." + currentSprite.name); } catch (Exception e) {}
            }
            // If it's the "Main" class, don't fall back to Sprite.class
            if ("Main".equals(currentSprite.name)) return null;
            return com.jscratch.Sprite.class;
        }
        if (expr.equals("Stage") || expr.equals("Stage.getInstance()")) return com.jscratch.Stage.class;
        
        try { return Class.forName(expr); } catch (Exception e) {}
        try { return Class.forName("java.lang." + expr); } catch (Exception e) {}
        try { return Class.forName("java.util." + expr); } catch (Exception e) {}
        
        ClassLoader loader = ProjectRunner.getProjectClassLoader();
        if (loader != null) {
            try { return loader.loadClass("generated." + expr); } catch (Exception e) {}
        }

        String varType = findVariableType(expr);
        if (varType != null) return resolveType(varType);

        return null;
    }

    private String findVariableType(String varName) {
        String text = codeEditor.getText();
        Pattern p = Pattern.compile("\\b([A-Z][a-zA-Z0-9_]*)\\s+\\b" + Pattern.quote(varName) + "\\b");
        Matcher m = p.matcher(text);
        if (m.find()) return m.group(1);
        
        p = Pattern.compile("(?:public|private|protected)?\\s+([A-Z][a-zA-Z0-9_]*)\\s+\\b" + Pattern.quote(varName) + "\\b\\s*[;=]");
        m = p.matcher(text);
        if (m.find()) return m.group(1);

        return null;
    }

    private void addTypeMembers(CompletionProvider provider, List<Completion> list, Class<?> clazz, boolean staticOnly) {
        Class<?> current = clazz;
        Set<String> seenMethods = new HashSet<>();
        Set<String> seenFields = new HashSet<>();
        
        while (current != null && current != Object.class) {
            try {
                for (Method m : current.getDeclaredMethods()) {
                    boolean isStatic = Modifier.isStatic(m.getModifiers());
                    if (staticOnly == isStatic) {
                        // Use name + parameter types as signature to handle overrides
                        String sig = m.getName() + java.util.Arrays.toString(m.getParameterTypes());
                        if (seenMethods.add(sig)) {
                            FunctionCompletion fc = new FunctionCompletion(provider, m.getName(), m.getReturnType().getSimpleName());
                            fc.setShortDescription(createMethodDesc(m));
                            list.add(fc);
                        }
                    }
                }
                for (Field f : current.getDeclaredFields()) {
                    boolean isStatic = Modifier.isStatic(f.getModifiers());
                    if (staticOnly == isStatic) {
                        if (seenFields.add(f.getName())) {
                            VariableCompletion vc = new VariableCompletion(provider, f.getName(), f.getType().getSimpleName());
                            list.add(vc);
                        }
                    }
                }
            } catch (SecurityException e) {
                // Fallback to public members if security restriction applies
                if (current == clazz) { // Only fallback for the main class once
                    for (Method m : clazz.getMethods()) {
                        boolean isStatic = Modifier.isStatic(m.getModifiers());
                        if (staticOnly == isStatic) {
                            String sig = m.getName() + java.util.Arrays.toString(m.getParameterTypes());
                            if (seenMethods.add(sig)) {
                                FunctionCompletion fc = new FunctionCompletion(provider, m.getName(), m.getReturnType().getSimpleName());
                                fc.setShortDescription(createMethodDesc(m));
                                list.add(fc);
                            }
                        }
                    }
                    for (Field f : clazz.getFields()) {
                        boolean isStatic = Modifier.isStatic(f.getModifiers());
                        if (staticOnly == isStatic) {
                            if (seenFields.add(f.getName())) {
                                VariableCompletion vc = new VariableCompletion(provider, f.getName(), f.getType().getSimpleName());
                                list.add(vc);
                            }
                        }
                    }
                    break;
                }
            }
            current = current.getSuperclass();
        }
    }

    private boolean isInStaticContext() {
        try {
            int caret = codeEditor.getCaretPosition();
            int line = codeEditor.getLineOfOffset(caret);
            for (int i = line; i >= 0; i--) {
                int start = codeEditor.getLineStartOffset(i);
                int end = codeEditor.getLineEndOffset(i);
                String lineText = codeEditor.getText(start, end - start);
                if (lineText.contains("{")) {
                    // Heuristic: If the line containing the last opening brace has 'static', we are in a static block/method
                    if (lineText.contains("static")) return true;
                    // If we see 'class', we are at class level (not inside a method)
                    if (lineText.contains("class")) return false;
                    // If we see a brace but no 'static', it might be an instance method or a control block.
                    // We continue searching up to find the method header.
                }
            }
        } catch (Exception e) {}
        return false;
    }

    private void addGlobalCompletions(CompletionProvider provider, List<Completion> list) {
        Set<String> suggestions = new HashSet<>();
        
        // Add common core classes
        String[] coreClasses = {"String", "Integer", "Math", "System", "ArrayList", "List", "Color", "KeyEvent", "Sprite", "Stage", "Thread"};
        for (String c : coreClasses) suggestions.add(c);

        // Add all sprites from the project
        for (Project.SpriteData sd : project.sprites) {
            suggestions.add(sd.name);
        }

        for (String s : suggestions) list.add(new BasicCompletion(provider, s));

        boolean isStatic = isInStaticContext();
        Class<?> thisType = resolveType("this");
        
        if (thisType != null) {
            // Only suggest instance members if we aren't in a static context
            if (!isStatic) {
                addTypeMembers(provider, list, thisType, false);
            }
            // Static members of 'this' are always valid
            addTypeMembers(provider, list, thisType, true);
        }
        
        // Also pick up variables/fields from the text (useful before first compile)
        Pattern p = Pattern.compile("\\b(?:[A-Z][a-zA-Z0-9_]*|int|double|float|long|boolean|char|byte|short|String)\\s+\\b([a-z][a-zA-Z0-9_]*)\\b");
        Matcher m = p.matcher(codeEditor.getText());
        Set<String> seen = new HashSet<>();
        while (m.find()) {
            String name = m.group(1);
            if (seen.add(name)) {
                list.add(new VariableCompletion(provider, name, ""));
            }
        }
        
        String[] imports = {"import java.util.*;", "import java.awt.Color;", "import com.jscratch.*;"};
        for (String i : imports) list.add(new BasicCompletion(provider, i));
    }

    private String createMethodDesc(Method m) {
        StringBuilder desc = new StringBuilder("<html><b>" + m.getName() + "</b>(");
        Class<?>[] params = m.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            desc.append(params[i].getSimpleName());
            if (i < params.length - 1) desc.append(", ");
        }
        desc.append(")<br>Returns: <i>" + m.getReturnType().getSimpleName() + "</i></html>");
        return desc.toString();
    }
}

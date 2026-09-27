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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
    private JTabbedPane sourceViews;
    private RSyntaxTextArea readableJavaEditor;
    private JPanel blockCanvas;
    private JScrollPane blockScrollPane;
    private Timer blockRefreshTimer;
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
    /** Suppress block-pane rerenders while block inputs write back into the Java document. */
    private boolean writingBlockParameter;

    public static final DataFlavor BLOCK_FLAVOR = new DataFlavor(BlockSnippet.class, "BlockSnippet");

    public static class BlockSnippet implements Serializable {
        public String name;
        public String category;
        public String code;
        public String docs = "";
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
            public void insertUpdate(DocumentEvent e) { refreshReadableJavaView(); scheduleSlotRefresh(); if (!writingBlockParameter) scheduleBlockRefresh(); triggerAutoCompile(); }
            public void removeUpdate(DocumentEvent e) { refreshReadableJavaView(); scheduleSlotRefresh(); if (!writingBlockParameter) scheduleBlockRefresh(); triggerAutoCompile(); }
            public void changedUpdate(DocumentEvent e) { refreshReadableJavaView(); scheduleSlotRefresh(); if (!writingBlockParameter) scheduleBlockRefresh(); triggerAutoCompile(); }
        });
        blockRefreshTimer = new Timer(250, e -> rebuildBlockCanvas());
        blockRefreshTimer.setRepeats(false);

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

        // Keep one source document and expose two synchronized views of it.
        // Java is the editable source; Blocks renders annotations and writes
        // edited parameter slots straight back into this same document.
        sourceViews = new JTabbedPane();
        readableJavaEditor = new RSyntaxTextArea(20, 60);
        readableJavaEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        readableJavaEditor.setCodeFoldingEnabled(true);
        readableJavaEditor.setAntiAliasingEnabled(true);
        readableJavaEditor.setTabSize(4);
        readableJavaEditor.setTabsEmulated(true);
        readableJavaEditor.setEditable(false);
        readableJavaEditor.setFocusable(true);
        readableJavaEditor.setLineWrap(false);
        readableJavaEditor.setToolTipText("Java source with JScratch-only block and slot markers hidden.");
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml"));
            theme.apply(readableJavaEditor);
        } catch (Exception ignored) { }
        sourceViews.addTab("Java (readable)", new RTextScrollPane(readableJavaEditor));
        sourceViews.addTab("Java Source (editable)", new RTextScrollPane(codeEditor));
        blockCanvas = new JPanel();
        blockCanvas.setLayout(new BoxLayout(blockCanvas, BoxLayout.Y_AXIS));
        blockCanvas.setBackground(new Color(242, 246, 252));
        blockCanvas.setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));
        blockScrollPane = new JScrollPane(blockCanvas);
        blockScrollPane.getVerticalScrollBar().setUnitIncrement(18);
        ToolTipManager.sharedInstance().registerComponent(blockCanvas);
        sourceViews.addTab("Blocks", blockScrollPane);
        sourceViews.addChangeListener(e -> { if (sourceViews.getSelectedIndex() == 2) rebuildBlockCanvas(); });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, paletteTabs, sourceViews);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void refreshReadableJavaView() {
        if (readableJavaEditor == null || codeEditor == null) return;
        String readable = ScratchBlockAnnotator.readableJava(codeEditor.getText());
        if (!readable.equals(readableJavaEditor.getText())) {
            int caret = readableJavaEditor.getCaretPosition();
            readableJavaEditor.setText(readable);
            readableJavaEditor.setCaretPosition(Math.min(caret, readable.length()));
        }
    }

    private void scheduleBlockRefresh() {
        if (blockRefreshTimer != null) blockRefreshTimer.restart();
    }

    /** Rebuild visible Scratch blocks from comment annotations in the source. */
    private void rebuildBlockCanvas() {
        if (blockCanvas == null || codeEditor == null) return;
        String source = codeEditor.getText();
        List<RenderedBlock> blocks = findRenderedBlocks(source);
        blockCanvas.removeAll();
        JPanel header = new JPanel(new BorderLayout(10, 2));
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sourceIdentity = new JLabel(currentSprite == null ? "Blocks" : currentSprite.getQualifiedName());
        sourceIdentity.setFont(new Font("SansSerif", Font.BOLD, 17));
        sourceIdentity.setForeground(new Color(36, 52, 76));
        header.add(sourceIdentity, BorderLayout.WEST);
        JLabel count = new JLabel(blocks.size() + (blocks.size() == 1 ? " block" : " blocks"));
        count.setFont(new Font("SansSerif", Font.PLAIN, 12));
        count.setForeground(new Color(90, 107, 130));
        header.add(count, BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        blockCanvas.add(header);
        JSeparator divider = new JSeparator();
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);
        blockCanvas.add(divider);
        blockCanvas.add(Box.createVerticalStrut(14));
        if (blocks.isEmpty()) {
            JLabel empty = new JLabel("No Scratch blocks found. Add a /* block start [...] */ annotation to Java statements.");
            empty.setForeground(new Color(95, 105, 120));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            blockCanvas.add(empty);
        } else {
            for (RenderedBlock block : blocks) {
                // Start markers for containing control blocks are rendered as
                // normal blocks too; only top-level stacks are emitted here.
                if (block.parent != null) continue;
                JPanel stack = createBlockStack(block, source);
                stack.setAlignmentX(Component.LEFT_ALIGNMENT);
                stack.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
                blockCanvas.add(stack);
            }
        }
        blockCanvas.add(Box.createVerticalGlue());
        blockCanvas.revalidate();
        blockCanvas.repaint();
    }

    /** Recursively render a control body as indented child blocks. */
    private JPanel createBlockStack(RenderedBlock parent, String source) {
        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.add(createRenderedBlock(parent, source));
        for (RenderedBlock child : parent.children) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(4, 18, 0, 0),
                    BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(196, 210, 229))));
            row.add(createBlockStack(child, source), BorderLayout.CENTER);
            stack.add(row);
        }
        return stack;
    }

    /** Parsed view model for a single annotated Java statement. */
    private static class RenderedBlock {
        String id, category, label, shape, docs, comment;
        int start, bodyStart, bodyEnd, end;
        RenderedBlock parent;
        List<RenderedBlock> children = new ArrayList<>();
        List<RenderedParameter> parameters = new ArrayList<>();
    }

    /** Source offsets around one editable literal/value. */
    private static class RenderedParameter {
        String id, type, value;
        int valueStart, valueEnd;
    }

    private List<RenderedBlock> findRenderedBlocks(String source) {
        List<RenderedBlock> result = new ArrayList<>();
        Pattern startPattern = Pattern.compile("/\\* block start \\[([^]]*)\\] \\*/");
        Matcher starts = startPattern.matcher(source);
        while (starts.find()) {
            String attrs = starts.group(1);
            String id = annotationValue(attrs, "id");
            if (id == null) continue;
            Pattern endPattern = Pattern.compile("/\\* block end \\[id:\"" + Pattern.quote(id) + "\"\\] \\*/");
            Matcher ends = endPattern.matcher(source);
            if (!ends.find(starts.end())) continue;
            RenderedBlock block = new RenderedBlock();
            block.id = id; block.category = valueOr(annotationValue(attrs, "category"), "My Blocks");
            block.label = valueOr(annotationValue(attrs, "label"), id);
            block.shape = valueOr(annotationValue(attrs, "shape"), "command");
            block.docs = valueOr(annotationValue(attrs, "docs"), "Java statement block");
            block.start = starts.start(); block.bodyStart = starts.end();
            block.bodyEnd = ends.start(); block.end = ends.end();
            String commentPrefix = "/* block comment [block:\"" + id + "\"";
            int commentStart = source.lastIndexOf(commentPrefix, starts.start());
            if (commentStart >= 0) {
                int commentEnd = source.indexOf("*/", commentStart);
                if (commentEnd >= 0 && commentEnd < starts.start()) {
                    String commentAttributes = source.substring(commentStart + commentPrefix.length(), commentEnd);
                    block.comment = annotationValue(commentAttributes, "text");
                }
            }
            if (block.comment == null) block.comment = block.docs;
            result.add(block);
        }
        Collections.sort(result, (a, b) -> Integer.compare(a.start, b.start));
        for (RenderedBlock child : result) {
            RenderedBlock best = null;
            for (RenderedBlock candidate : result) {
                if (candidate == child || candidate.start > child.start || candidate.end < child.end) continue;
                if (best == null || candidate.end - candidate.start < best.end - best.start) best = candidate;
            }
            child.parent = best;
            if (best != null) best.children.add(child);
        }

        Pattern paramStart = Pattern.compile("/\\* param start \\[([^]]*)\\] \\*/");
        Matcher params = paramStart.matcher(source);
        List<RenderedParameter> allParams = new ArrayList<>();
        while (params.find()) {
            String attrs = params.group(1), id = annotationValue(attrs, "id");
            if (id == null) continue;
            Pattern paramEnd = Pattern.compile("/\\* param end \\[id:\"" + Pattern.quote(id) + "\"\\] \\*/");
            Matcher end = paramEnd.matcher(source);
            if (!end.find(params.end())) continue;
            RenderedParameter param = new RenderedParameter();
            param.id = id; param.type = valueOr(annotationValue(attrs, "type"), "value");
            param.valueStart = params.end(); param.valueEnd = end.start();
            param.value = source.substring(param.valueStart, param.valueEnd).trim();
            allParams.add(param);
        }
        for (RenderedParameter param : allParams) {
            RenderedBlock owner = null;
            for (RenderedBlock candidate : result) {
                if (param.valueStart < candidate.bodyStart || param.valueEnd > candidate.bodyEnd) continue;
                if (owner == null || candidate.end - candidate.start < owner.end - owner.start) owner = candidate;
            }
            if (owner != null) owner.parameters.add(param);
        }
        return result;
    }

    private static String annotationValue(String attrs, String key) {
        String prefix = key + ":\"";
        int start = attrs.indexOf(prefix);
        if (start < 0) return null;
        start += prefix.length();
        StringBuilder value = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < attrs.length(); i++) {
            char ch = attrs.charAt(i);
            if (escaped) { value.append(ch); escaped = false; }
            else if (ch == 92) escaped = true;
            else if (ch == 34) return value.toString();
            else value.append(ch);
        }
        return null;
    }

    private static String valueOr(String value, String fallback) {
        return value == null || value.length() == 0 ? fallback : value;
    }

    /** Draw one block and its attached, pinned documentation comment. */
    private JComponent createRenderedBlock(RenderedBlock block, String source) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setOpaque(true);
        boolean reporter = "reporter".equals(block.shape) || "boolean".equals(block.shape);
        Color accent = getCategoryColor(block.category);
        card.setBackground(reporter ? new Color(255, 255, 255) : new Color(255, 255, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 5, 1, 1, accent),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(216, 224, 235)),
                        BorderFactory.createEmptyBorder(8, 11, 8, 11))));

        card.setMaximumSize(new Dimension(760, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setToolTipText("<html><b>" + html(block.label) + "</b><br>" + html(block.docs) + "</html>");
        if ("control".equals(block.shape)) card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 5, 1, 1, accent.darker()),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(198, 211, 229)),
                        BorderFactory.createEmptyBorder(8, 11, 8, 11))));
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        JLabel label = new JLabel(block.label);
        label.setForeground(new Color(37, 51, 72));
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        titleRow.add(label);
        JLabel category = new JLabel(block.category);
        category.setOpaque(true);
        category.setBackground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 35));
        category.setForeground(accent.darker());
        category.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
        category.setFont(new Font("SansSerif", Font.BOLD, 10));
        titleRow.add(category);
        card.add(titleRow, BorderLayout.NORTH);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        JLabel sourceInfo = new JLabel(block.shape + "  |  lines " + lineNumber(source, block.start)
                + "-" + lineNumber(source, block.end) + "  |  " + block.id);
        sourceInfo.setForeground(new Color(103, 116, 135));
        sourceInfo.setFont(new Font("SansSerif", Font.PLAIN, 10));
        sourceInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        details.add(sourceInfo);

        if (!block.parameters.isEmpty()) {
            JLabel inputsTitle = new JLabel("Parameters");
            inputsTitle.setForeground(new Color(75, 89, 108));
            inputsTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
            inputsTitle.setBorder(BorderFactory.createEmptyBorder(7, 0, 1, 0));
            inputsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            details.add(inputsTitle);
            JPanel inputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 4));
            inputs.setOpaque(false);
            int inputNumber = 1;
            for (RenderedParameter parameter : block.parameters) {
                JTextField field = new JTextField(displayParameter(parameter), parameter.type.equals("text") ? 12 : 7);
                String ownerDoc = valueOr(block.comment, block.docs);
                field.setToolTipText("<html>" + html(ownerDoc) + "<br>Editable " + html(parameter.type)
                        + " input (" + html(parameter.id) + ").</html>");
                field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(195, 207, 223)), BorderFactory.createEmptyBorder(4, 7, 4, 7)));
                field.setBackground(Color.WHITE);
                field.addActionListener(e -> writeParameter(parameter, field.getText()));
                field.addFocusListener(new FocusAdapter() {
                    @Override public void focusLost(FocusEvent e) { writeParameter(parameter, field.getText()); }
                });
                inputs.add(new JLabel("#" + inputNumber++ + " | " + parameter.type + ":"));
                inputs.add(field);
            }
            inputs.setAlignmentX(Component.LEFT_ALIGNMENT);
            details.add(inputs);
        }

        final String javaCode = reconstructableJava(block, source);
        JPanel javaHeader = new JPanel(new BorderLayout());
        javaHeader.setOpaque(false);
        javaHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        javaHeader.setBorder(BorderFactory.createEmptyBorder(7, 0, 3, 0));
        JLabel javaTitle = new JLabel("Java source (editor annotations removed)");
        javaTitle.setForeground(new Color(75, 89, 108));
        javaTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        javaHeader.add(javaTitle, BorderLayout.WEST);
        JButton copySource = new JButton("Copy");
        copySource.setFocusable(false);
        copySource.setMargin(new Insets(2, 8, 2, 8));
        copySource.setToolTipText("Copy this block as Java source without editor annotations");
        copySource.addActionListener(e -> Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(javaCode), null));
        javaHeader.add(copySource, BorderLayout.EAST);
        details.add(javaHeader);
        JTextArea javaSource = new JTextArea(javaCode);
        javaSource.setToolTipText("Java source for this block with JScratch-only markers removed.");
        javaSource.setEditable(false);
        javaSource.setFocusable(true);
        javaSource.setLineWrap(true);
        javaSource.setWrapStyleWord(false);
        javaSource.setColumns(60);
        javaSource.setRows(Math.max(1, Math.min(6, javaCode.split("\\n", -1).length)));
        javaSource.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        javaSource.setForeground(new Color(44, 57, 76));
        javaSource.setBackground(new Color(247, 249, 252));
        javaSource.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));
        javaSource.setCaretPosition(0);
        JScrollPane sourceScroll = new JScrollPane(javaSource);
        sourceScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        sourceScroll.setPreferredSize(new Dimension(580, Math.min(124, Math.max(48, javaSource.getPreferredSize().height + 4))));
        sourceScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 227, 237)));
        details.add(sourceScroll);
        card.add(details, BorderLayout.CENTER);

        JPanel note = new JPanel(new BorderLayout(5, 0));

        note.setBackground(new Color(255, 255, 255));
        note.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(226, 190, 93)),
                BorderFactory.createEmptyBorder(4, 9, 5, 8)));
        JLabel pin = new JLabel("NOTE");
        pin.setForeground(new Color(145, 112, 38));
        pin.setFont(new Font("SansSerif", Font.BOLD, 9));
        JLabel noteText = new JLabel("<html>" + html(valueOr(block.comment, block.docs)) + "</html>");
        noteText.setForeground(new Color(80, 91, 108));
        noteText.setFont(new Font("SansSerif", Font.PLAIN, 11));
        note.add(pin, BorderLayout.WEST); note.add(noteText, BorderLayout.CENTER);
        note.setToolTipText("Pinned block note — its text is stored in this block's source annotation.");
        card.setName("scratch-block-" + block.id);
        note.setName("scratch-comment-" + block.id);
        JPanel stack = new JPanel(); stack.setOpaque(false); stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.add(card); stack.add(note);
        return stack;
    }

    /** Remove editor-only annotations while retaining Java strings and comments. */
    private String reconstructableJava(RenderedBlock block, String source) {
        return ScratchBlockAnnotator.readableJava(source.substring(block.bodyStart, block.bodyEnd));
    }

    private int lineNumber(String source, int offset) {
        int line = 1;
        for (int i = 0; i < offset && i < source.length(); i++) {
            if (source.charAt(i) == '\n') line++;
        }
        return line;
    }

    private String displayParameter(RenderedParameter parameter) {
        String value = parameter.value;
        if (parameter.type.equals("text") || parameter.type.equals("string")) {
            if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) return value.substring(1, value.length() - 1).replace("\\\"", "\"");
        }
        if (parameter.type.equals("character") && value.length() >= 3 && value.startsWith("'") && value.endsWith("'")) {
            return value.substring(1, value.length() - 1).replace("\\\\'", "'");
        }
        return value;
    }

    /** Update only the literal between its markers, retaining all annotations. */
    private void writeParameter(RenderedParameter parameter, String value) {
        String current = codeEditor.getText();
        // Re-find this parameter by id: edits to an earlier field shift every later source offset.
        String startMarker = "/* param start [id:\"" + parameter.id + "\"";
        int markerOffset = current.indexOf(startMarker);
        if (markerOffset < 0) return;
        int valueStart = current.indexOf("*/", markerOffset);
        if (valueStart < 0) return;
        valueStart += 2;
        String endMarker = "/* param end [id:\"" + parameter.id + "\"] */";
        int valueEnd = current.indexOf(endMarker, valueStart);
        if (valueEnd < 0) return;
        String replacement = value;
        if (parameter.type.equals("text") || parameter.type.equals("string")) {
            replacement = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        } else if (parameter.type.equals("character")) {
            replacement = "'" + value.replace("'", "\\'") + "'";
        }
        if (replacement.equals(current.substring(valueStart, valueEnd))) return;
        try {
            writingBlockParameter = true;
            codeEditor.getDocument().remove(valueStart, valueEnd - valueStart);
            codeEditor.getDocument().insertString(valueStart, replacement, null);
            lastCompiledText = codeEditor.getText();
            if (currentSprite != null) currentSprite.sourceCode = lastCompiledText;
            if (blockCanvas != null) scheduleBlockRefresh();
        } catch (BadLocationException ignored) { }
        finally { writingBlockParameter = false; }
    }

    private static String html(String text) {
        return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
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
            
            for (CompilationResult.CompilationError error : errors) {                    if (error.fileName.equals(currentSprite.name + ".java")
                            || error.fileName.equals(currentSprite.getQualifiedName().replace('.', '/') + ".java")) {
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
                    currentBlock.category = parts.length > 1 ? parts[1].trim() : "My Blocks";
                    if (parts.length > 2) {
                        for (String t : parts[2].split(",")) currentBlock.tags.add(t.trim());
                    }
                    if (parts.length > 3) currentBlock.docs = parts[3].replaceFirst("^docs=", "").trim();
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
        // Block templates can declare editable palette inputs with
        // ${param:name:type:default}; those values are substituted on drag.
        final String template = block.code;
        final Map<String, PaletteParameter> parameters = parsePaletteParameters(template);
        final JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 4));
        card.setOpaque(true);            card.setBackground(getCategoryColor(block.category));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getCategoryColor(block.category).darker()),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)));
        card.setMaximumSize(new Dimension(228, 100));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setToolTipText("<html><b>" + html(block.name) + "</b><br>" + html(valueOr(block.docs, "Scratch-style code block.")) + "</html>");
        JLabel label = new JLabel(block.name);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        card.add(label);
        for (PaletteParameter parameter : parameters.values()) {
            JTextField input = new JTextField(parameter.value, parameter.type.equals("text") ? 8 : 5);
            input.setToolTipText("Block input: " + parameter.name + " (" + parameter.type + ")");
            input.addActionListener(e -> parameter.value = input.getText());
            input.addFocusListener(new FocusAdapter() {
                @Override public void focusLost(FocusEvent e) { parameter.value = input.getText(); }
            });
            card.add(new JLabel(parameter.name + ":"));
            card.add(input);
        }
        card.setTransferHandler(new TransferHandler() {
            @Override public int getSourceActions(JComponent c) { return COPY; }
            @Override protected Transferable createTransferable(JComponent c) {
                BlockSnippet configured = new BlockSnippet();
                configured.name = block.name; configured.category = block.category;
                configured.tags = block.tags; configured.docs = block.docs;
                configured.code = resolvePaletteParameters(template, parameters);
                if (configured.code.contains("${id}")) configured.code = configured.code.replace("${id}", String.valueOf(project.blockIdCounter++));
                return new BlockTransferable(configured);
            }
        });
        card.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                card.getTransferHandler().exportAsDrag(card, e, TransferHandler.COPY);
            }
        });
        return card;
    }

    private static class PaletteParameter {
        String name, type, value;
        PaletteParameter(String name, String type, String value) { this.name = name; this.type = type; this.value = value; }
    }

    private Map<String, PaletteParameter> parsePaletteParameters(String template) {
        Map<String, PaletteParameter> parameters = new java.util.LinkedHashMap<>();
        Matcher matcher = Pattern.compile("\\$\\{param:([^:}]+):([^:}]+):([^}]+)}").matcher(template);
        while (matcher.find()) {
            String name = matcher.group(1).trim();
            parameters.put(name, new PaletteParameter(name, matcher.group(2).trim(), matcher.group(3).trim()));
        }
        return parameters;
    }

    private String resolvePaletteParameters(String template, Map<String, PaletteParameter> parameters) {
        Matcher matcher = Pattern.compile("\\$\\{param:([^:}]+):([^:}]+):([^}]+)}").matcher(template);
        StringBuffer resolved = new StringBuffer();
        while (matcher.find()) {
            PaletteParameter parameter = parameters.get(matcher.group(1).trim());
            String value = parameter == null ? matcher.group(3) : parameter.value;
            if (parameter != null && "text".equals(parameter.type)) value = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
            matcher.appendReplacement(resolved, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(resolved);
        return resolved.toString();
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
        String migrated = ScratchBlockAnnotator.annotate(sprite.sourceCode, sprite.name);
        if (!migrated.equals(sprite.sourceCode)) sprite.sourceCode = migrated;
        codeEditor.setText(sprite.sourceCode);
        refreshReadableJavaView();
        lastCompiledText = sprite.sourceCode;
        codeEditor.setCaretPosition(0);
        codeEditor.discardAllEdits();
        refreshSlots();
        rebuildBlockCanvas();
    }

    /** Reload the selected project source after package/type metadata changes. */
    public void reloadSpriteCode(SpriteData sprite) {
        currentSprite = null;
        loadSpriteCode(sprite);
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
                if (currentSprite != null) {
                    try { loader.loadClass(currentSprite.packageName + "." + expr); return true; } catch (Exception ignored) {}
                }
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
                try { return loader.loadClass(currentSprite.getQualifiedName()); } catch (Exception e) {}
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
            if (currentSprite != null) {
                try { return loader.loadClass(currentSprite.packageName + "." + expr); } catch (Exception ignored) {}
            }
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

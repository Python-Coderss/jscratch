package com.jscratch.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Adds Scratch-like comment annotations to Java without changing behavior.
 * Source indentation is normalized with a small lexer that ignores braces in
 * strings and comments; operation markers remain ordinary Java comments.
 */
public final class ScratchBlockAnnotator {
    private ScratchBlockAnnotator() { }

    private static final class Token {
        final String text;
        final int start, end;
        Token(String text, int start, int end) { this.text = text; this.start = start; this.end = end; }
    }

    private static final class Range {
        final int start, end;
        final String category, label, shape, docs, id;
        Range(int start, int end, String category, String label, String shape, String docs, String id) {
            this.start = start; this.end = end; this.category = category;
            this.label = label; this.shape = shape; this.docs = docs; this.id = id;
        }
    }

    private static final class Edit {
        final int offset, span, order;
        final String text;
        Edit(int offset, String text, int span, int order) {
            this.offset = offset; this.text = text; this.span = span; this.order = order;
        }
    }

    /** Normalize indentation while preserving strings, comments and blank lines. */
    public static String formatJava(String source) {
        if (source == null || source.trim().isEmpty()) return source;
        // Ensure markers adjacent to expressions remain standalone Java comments.
        source = spaceParameterMarkers(source);
        String[] lines = source.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);
        StringBuilder formatted = new StringBuilder(source.length() + lines.length * 4);
        int braceDepth = 0;
        boolean inBlockComment = false;
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String trimmed = lines[lineIndex].trim();
            if (!trimmed.isEmpty()) {
                int depthForLine = braceDepth;
                if (!inBlockComment) {
                    for (int i = 0; i < trimmed.length() && trimmed.charAt(i) == '}'; i++) {
                        depthForLine = Math.max(0, depthForLine - 1);
                    }
                }
                for (int i = 0; i < depthForLine; i++) formatted.append("    ");
                formatted.append(trimmed);
                braceDepth = updateBraceDepth(trimmed, braceDepth, inBlockComment);
                inBlockComment = endsInsideBlockComment(trimmed, inBlockComment);
            }
            if (lineIndex < lines.length - 1) formatted.append('\n');
        }
        return formatted.toString();
    }

    private static String spaceParameterMarkers(String source) {
        StringBuilder spaced = new StringBuilder(source.length() + 32);
        int cursor = 0;
        while (cursor < source.length()) {
            int markerStart = source.indexOf("/* param ", cursor);
            if (markerStart < 0) break;
            int markerEnd = source.indexOf("*/", markerStart);
            if (markerEnd < 0) break;
            spaced.append(source, cursor, markerStart);
            if (markerStart > 0 && !Character.isWhitespace(source.charAt(markerStart - 1))) {
                char before = source.charAt(markerStart - 1);
                if (before == '/') {
                    int j = spaced.length() - 1;
                    while (j >= 0 && Character.isWhitespace(spaced.charAt(j))) j--;
                    if (j >= 0 && spaced.charAt(j) == '/') spaced.insert(j + 1, ' ');
                }
            }
            spaced.append(source, markerStart, markerEnd + 2);
            if (markerEnd + 2 < source.length() && !Character.isWhitespace(source.charAt(markerEnd + 2))) {
                char after = source.charAt(markerEnd + 2);
                if (after == '/' && markerEnd + 3 < source.length() && source.charAt(markerEnd + 3) == '*') spaced.append(' ');
            }
            cursor = markerEnd + 2;
        }
        if (cursor == 0) return source;
        spaced.append(source.substring(cursor));
        return spaced.toString();
    }

    private static int updateBraceDepth(String line, int depth, boolean inBlockComment) {
        boolean inString = false, inCharacter = false, escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            char next = i + 1 < line.length() ? line.charAt(i + 1) : 0;
            if (inBlockComment) {
                if (ch == '*' && next == '/') { inBlockComment = false; i++; }
                continue;
            }
            if (inString || inCharacter) {
                if (escaped) escaped = false;
                else if (ch == '\\') escaped = true;
                else if (inString && ch == '"') inString = false;
                else if (inCharacter && ch == '\'') inCharacter = false;
                continue;
            }
            if (ch == '/' && next == '/') break;
            if (ch == '/' && next == '*') { inBlockComment = true; i++; continue; }
            if (ch == '"') inString = true;
            else if (ch == '\'') inCharacter = true;
            else if (ch == '{') depth++;
            else if (ch == '}') depth = Math.max(0, depth - 1);
        }
        return depth;
    }

    private static boolean endsInsideBlockComment(String line, boolean inBlockComment) {
        boolean inString = false, inCharacter = false, escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            char next = i + 1 < line.length() ? line.charAt(i + 1) : 0;
            if (inBlockComment) {
                if (ch == '*' && next == '/') { inBlockComment = false; i++; }
                continue;
            }
            if (inString || inCharacter) {
                if (escaped) escaped = false;
                else if (ch == '\\') escaped = true;
                else if (inString && ch == '"') inString = false;
                else if (inCharacter && ch == '\'') inCharacter = false;
                continue;
            }
            if (ch == '/' && next == '/') break;
            if (ch == '/' && next == '*') { inBlockComment = true; i++; }
            else if (ch == '"') inString = true;
            else if (ch == '\'') inCharacter = true;
        }
        return inBlockComment;
    }

    /** Return a formatted, annotated copy; preserve already-authored block markers. */
    public static String annotate(String source, String typeName) {
        if (source == null || source.trim().isEmpty()) return source;
        source = formatJava(source);
        if (source.contains("/* block start [")) return refreshLegacyMethodCallBlocks(source);
        List<Token> tokens = tokenize(source);
        if (tokens.isEmpty()) return source;
        final String prefix = typeName == null ? "Source" : typeName.replaceAll("[^A-Za-z0-9_]", "_");
        AtomicInteger ids = new AtomicInteger(1);
        List<Range> ranges = new ArrayList<Range>();
        List<int[]> ignoredHeaders = new ArrayList<int[]>();

        for (int i = 0; i < tokens.size(); i++) {
            String word = tokens.get(i).text;
            String label = null, docs = null;
            if ("if".equals(word)) {
                label = "if then"; docs = "Checks a Boolean condition and runs the body when it is true.";
            } else if ("for".equals(word)) {
                label = i + 1 < tokens.size() && ":".equals(tokens.get(i + 1).text) ? "for each" : "repeat / for";
                docs = "Repeats the nested operations for each loop turn.";
            } else if ("while".equals(word)) {
                label = "repeat while"; docs = "Repeats the body while its Boolean condition is true.";
            } else if ("do".equals(word)) {
                label = "repeat until"; docs = "Runs the body once, then repeats while the condition is false.";
            } else if ("switch".equals(word)) {
                label = "choose a case"; docs = "Selects and runs the case that matches the input value.";
            } else if ("try".equals(word)) {
                label = "try / catch"; docs = "Runs work safely and handles matching errors in a catch branch.";
            }
            if (label == null) continue;
            int open = findToken(tokens, i + 1, "(");
            int close = open < 0 ? -1 : matching(tokens, open, "(", ")");
            if (open >= 0 && close >= 0) ignoredHeaders.add(new int[]{open, close});
            int bodyToken = close >= 0 ? close + 1 : i + 1;
            if ("for".equals(word) && i + 1 < tokens.size() && ":".equals(tokens.get(i + 1).text)) bodyToken = i + 2;
            if ("try".equals(word) && bodyToken < tokens.size() && "(".equals(tokens.get(bodyToken).text)) {
                int resourceEnd = matching(tokens, bodyToken, "(", ")");
                if (resourceEnd >= 0) bodyToken = resourceEnd + 1;
            }
            if (bodyToken >= tokens.size()) continue;
            int bodyEndToken = statementEnd(tokens, bodyToken);
            if (bodyEndToken < bodyToken) continue;
            int endOffset = tokens.get(bodyEndToken).end;
            if ("if".equals(word) && bodyEndToken + 1 < tokens.size() && "else".equals(tokens.get(bodyEndToken + 1).text)) {
                int elseEnd = statementEnd(tokens, bodyEndToken + 2);
                if (elseEnd >= bodyEndToken + 2) endOffset = tokens.get(elseEnd).end;
                label = "if then else";
            }
            String id = prefix + "_block_" + ids.getAndIncrement();
            ranges.add(new Range(tokens.get(i).start, endOffset, "Control", label, "control", docs, id));
        }

        int braceDepth = 0, parenDepth = 0, bracketDepth = 0, segmentStart = -1;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String value = token.text;
            if ("{".equals(value)) { braceDepth++; segmentStart = i + 1; continue; }
            if ("}".equals(value)) { braceDepth = Math.max(0, braceDepth - 1); segmentStart = i + 1; continue; }
            if ("(".equals(value)) parenDepth++;
            else if (")".equals(value)) parenDepth = Math.max(0, parenDepth - 1);
            else if ("[".equals(value)) bracketDepth++;
            else if ("]".equals(value)) bracketDepth = Math.max(0, bracketDepth - 1);
            if (!";".equals(value)) continue;
            if (parenDepth == 0 && bracketDepth == 0 && braceDepth >= 2 && segmentStart >= 0 && segmentStart < i) {
                int from = segmentStart;
                while (from < i && isStatementPrefix(tokens.get(from).text)) from++;
                if (from < i && !insideIgnoredHeader(from, ignoredHeaders)) {
                    String first = tokens.get(from).text;
                    String category, blockLabel, blockDocs;
                    if (isDeclarationStart(first)) {
                        category = "Variables";
                        blockLabel = "set " + declarationName(tokens, from, i);
                        blockDocs = "Creates or updates a local value. Use it to keep a value for later operations.";
                    } else if ("return".equals(first)) {
                        category = "Control"; blockLabel = "return"; blockDocs = "Returns a value or ends the current method.";
                    } else if ("break".equals(first)) {
                        category = "Control"; blockLabel = "break loop"; blockDocs = "Stops the nearest loop or switch block.";
                    } else if ("continue".equals(first)) {
                        category = "Control"; blockLabel = "next loop turn"; blockDocs = "Skips to the next iteration of the nearest loop.";
                    } else if ("throw".equals(first)) {
                        category = "Control"; blockLabel = "throw error"; blockDocs = "Raises an error for a matching catch block.";
                    } else {
                        String call = calledMethod(tokens, from, i);
                        category = call == null ? "Operators" : categoryFor(call);
                        blockLabel = call == null ? "calculate / update value"
                                : ("My Blocks".equals(category) ? "call " + labelFor(call) : labelFor(call));
                        blockDocs = call == null ? "Evaluates this expression and applies its result."
                                : ("My Blocks".equals(category)
                                    ? "Calls the " + call + " method using its declared arguments."
                                    : "Runs the " + blockLabel + " action. Values in this block are editable inputs.");
                    }
                    int startOffset = tokens.get(from).start;
                    if (from > segmentStart) startOffset = tokens.get(segmentStart).start;
                    String id = prefix + "_block_" + ids.getAndIncrement();
                    ranges.add(new Range(startOffset, token.end, category, blockLabel, "command", blockDocs, id));
                }
            }
            segmentStart = i + 1;
        }

        if (ranges.isEmpty()) return source;
        List<Edit> edits = new ArrayList<Edit>();
        for (Range range : ranges) {
            String docs = clean(range.docs);
            String attrs = "id:\"" + range.id + "\" category:\"" + clean(range.category)
                    + "\" label:\"" + clean(range.label) + "\" shape:\"" + range.shape
                    + "\" docs:\"" + docs + "\"";
            String indentation = indentationAt(source, range.start);
            edits.add(new Edit(range.start, "/* block comment [block:\"" + range.id + "\" text:\""
                    + docs + "\"] */\n" + indentation + "/* block start [" + attrs + "] */\n" + indentation,
                    range.end - range.start, 0));
            edits.add(new Edit(range.end, "\n" + indentation + "/* block end [id:\"" + range.id + "\"] */",
                    range.end - range.start, 1));
        }
        for (Token token : tokens) {
            String type = literalType(token.text);
            if (type == null) continue;
            Range owner = null;
            for (Range range : ranges) {
                if (token.start < range.start || token.end > range.end) continue;
                if (owner == null || range.end - range.start < owner.end - owner.start) owner = range;
            }
            if (owner == null) continue;
            String id = prefix + "_value_" + ids.getAndIncrement();
            edits.add(new Edit(token.start, "/* param start [id:\"" + id + "\" type:\"" + type + "\"] */", token.end - token.start, 2));
            edits.add(new Edit(token.end, "/* param end [id:\"" + id + "\"] */", token.end - token.start, 3));
        }
        Collections.sort(edits, new Comparator<Edit>() {
            @Override public int compare(Edit left, Edit right) {
                if (left.offset != right.offset) return right.offset - left.offset;
                if (left.span != right.span) return right.span - left.span;
                return left.order - right.order;
            }
        });
        StringBuilder annotated = new StringBuilder(source);
        for (Edit edit : edits) annotated.insert(edit.offset, edit.text);
        return formatJava(annotated.toString());
    }

    /** Return Java source for the readable view, with all editor-only markers removed. */
    public static String readableJava(String source) {
        if (source == null || source.isEmpty()) return source;
        StringBuilder clean = new StringBuilder(source.length());
        for (int i = 0; i < source.length();) {
            char ch = source.charAt(i);
            if (ch == '"' || ch == '\'') {
                char quote = ch;
                int end = i + 1;
                while (end < source.length()) {
                    char current = source.charAt(end++);
                    if (current == '\\' && end < source.length()) end++;
                    else if (current == quote) break;
                }
                clean.append(source, i, end);
                i = end;
            } else if (ch == '/' && i + 1 < source.length() && source.charAt(i + 1) == '/') {
                int end = source.indexOf('\n', i + 2);
                if (end < 0) end = source.length();
                clean.append(source, i, end);
                i = end;
            } else if (ch == '/' && i + 1 < source.length() && source.charAt(i + 1) == '*') {
                int end = source.indexOf("*/", i + 2);
                if (end < 0) {
                    clean.append(source.substring(i));
                    break;
                }
                end += 2;
                String comment = source.substring(i + 2, end - 2).trim();
                if (!isEditorMarker(comment)) clean.append(source, i, end);
                else if (clean.length() > 0 && !Character.isWhitespace(clean.charAt(clean.length() - 1))) clean.append(' ');
                i = end;
            } else {
                clean.append(ch);
                i++;
            }
        }
        StringBuilder compact = new StringBuilder(clean.length());
        boolean blankLine = false;
        for (String line : clean.toString().replace("\r\n", "\n").replace('\r', '\n').split("\n", -1)) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                if (!blankLine && compact.length() > 0) compact.append('\n');
                blankLine = true;
            } else {
                compact.append(line.replaceAll("[ \\t]+$", "")).append('\n');
                blankLine = false;
            }
        }
        String readable = compact.toString().trim();
        return reindentJava(readable);
    }

    private static String reindentJava(String source) {
        if (source.isEmpty()) return source;
        String[] lines = source.split("\\n", -1);
        int minimumIndent = Integer.MAX_VALUE;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            int indent = 0;
            while (indent < line.length() && (line.charAt(indent) == ' ' || line.charAt(indent) == '\t')) indent++;
            minimumIndent = Math.min(minimumIndent, indent);
        }
        if (minimumIndent == Integer.MAX_VALUE || minimumIndent == 0) return source;
        StringBuilder dedented = new StringBuilder(source.length());
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            dedented.append(line.substring(Math.min(minimumIndent, leadingWhitespace(line)), line.length()));
            if (i < lines.length - 1) dedented.append('\n');
        }
        return formatJava(dedented.toString());
    }

    private static int leadingWhitespace(String line) {
        int indent = 0;
        while (indent < line.length() && (line.charAt(indent) == ' ' || line.charAt(indent) == '\t')) indent++;
        return indent;
    }

    private static boolean isEditorMarker(String comment) {
        return comment.startsWith("block comment [") || comment.startsWith("block start [")
                || comment.startsWith("block end [") || comment.startsWith("param start [")
                || comment.startsWith("param end [") || comment.startsWith("slot start [")
                || comment.startsWith("slot end [");
    }

    /** Update older generated call labels without touching authored block metadata. */
    private static String refreshLegacyMethodCallBlocks(String source) {
        List<Edit> edits = new ArrayList<Edit>();
        String startMarker = "/* block start [";
        int searchFrom = 0;
        while (true) {
            int start = source.indexOf(startMarker, searchFrom);
            if (start < 0) break;
            int attrsStart = start + startMarker.length();
            int attrsEnd = source.indexOf("] */", attrsStart);
            if (attrsEnd < 0) break;
            String attrs = source.substring(attrsStart, attrsEnd);
            String id = annotationValue(attrs, "id");
            String category = annotationValue(attrs, "category");
            String docs = annotationValue(attrs, "docs");
            searchFrom = attrsEnd + 4;
            if (id == null || !"My Blocks".equals(category) || docs == null
                    || !docs.startsWith("Runs the ") || !docs.endsWith(" action. Values in this block are editable inputs.")) continue;
            String endMarker = "/* block end [id:\"" + id + "\"] */";
            int bodyEnd = source.indexOf(endMarker, searchFrom);
            if (bodyEnd < 0) continue;
            List<Token> bodyTokens = tokenize(source.substring(searchFrom, bodyEnd));
            if (!isSimpleMethodCall(bodyTokens)) continue;
            String method = calledMethod(bodyTokens, 0, bodyTokens.size());
            if (method == null) continue;
            String refreshedDocs = "Calls the " + method + " method using its declared arguments.";
            if ("super".equals(method) || "this".equals(method)) continue;
            String refreshedAttrs = replaceAttribute(attrs, "label", "call " + labelFor(method));
            refreshedAttrs = replaceAttribute(refreshedAttrs, "docs", refreshedDocs);
            edits.add(new Edit(attrsStart, refreshedAttrs, attrs.length(), 0));

            int commentStart = source.lastIndexOf("/* block comment [block:\"" + id + "\"", start);
            if (commentStart >= 0) {
                int commentEnd = source.indexOf("*/", commentStart);
                int textOffset = source.indexOf("text:\"", commentStart);
                if (commentEnd >= 0 && commentEnd < start && textOffset >= 0 && textOffset < commentEnd) {
                    int textEnd = source.indexOf('"', textOffset + 6);
                    if (textEnd >= 0 && textEnd < commentEnd) {
                        String oldText = source.substring(textOffset, textEnd + 1);
                        String newText = "text:\"" + refreshedDocs + "\"";
                        edits.add(new Edit(textOffset, newText, oldText.length(), 0));
                    }
                }
            }
        }
        if (edits.isEmpty()) return source;
        Collections.sort(edits, new Comparator<Edit>() {
            @Override public int compare(Edit left, Edit right) { return right.offset - left.offset; }
        });
        StringBuilder updated = new StringBuilder(source);
        for (Edit edit : edits) updated.replace(edit.offset, edit.offset + edit.span, edit.text);
        return updated.toString();
    }

    private static boolean isSimpleMethodCall(List<Token> tokens) {
        if (tokens.size() < 3 || !";".equals(tokens.get(tokens.size() - 1).text)) return false;
        String first = tokens.get(0).text;
        if (!isIdentifier(first) || "if".equals(first) || "for".equals(first) || "while".equals(first)
                || "switch".equals(first) || "return".equals(first) || "throw".equals(first)
                || "new".equals(first) || "this".equals(first) || "super".equals(first)) return false;
        int depth = 0;
        for (int i = 0; i < tokens.size() - 1; i++) {
            String value = tokens.get(i).text;
            if ("(".equals(value)) depth++;
            else if (")".equals(value)) depth = Math.max(0, depth - 1);
            else if (depth == 0 && ("=".equals(value) || "+=".equals(value) || "-=".equals(value)
                    || "++".equals(value) || "--".equals(value) || "&&".equals(value) || "||".equals(value))) return false;
        }
        return calledMethod(tokens, 0, tokens.size()) != null;
    }

    private static String annotationValue(String attrs, String key) {
        String marker = key + ":\"";
        int start = attrs.indexOf(marker);
        if (start < 0) return null;
        start += marker.length();
        int end = attrs.indexOf('"', start);
        return end < 0 ? null : attrs.substring(start, end);
    }

    private static String replaceAttribute(String attrs, String key, String value) {
        String marker = key + ":\"";
        int start = attrs.indexOf(marker);
        if (start < 0) return attrs;
        start += marker.length();
        int end = attrs.indexOf('"', start);
        if (end < 0) return attrs;
        return attrs.substring(0, start) + value.replace("\\", "\\\\").replace("\"", "\\\"")
                + attrs.substring(end);
    }

    private static String indentationAt(String source, int offset) {
        int lineStart = source.lastIndexOf('\n', Math.max(0, offset - 1)) + 1;
        int cursor = lineStart;
        while (cursor < offset && (source.charAt(cursor) == ' ' || source.charAt(cursor) == '\t')) cursor++;
        return cursor == offset ? source.substring(lineStart, offset) : "";
    }

    private static List<Token> tokenize(String source) {
        List<Token> tokens = new ArrayList<Token>();
        for (int i = 0; i < source.length();) {
            char c = source.charAt(i);
            if (Character.isWhitespace(c)) { i++; continue; }
            if (c == '/' && i + 1 < source.length() && source.charAt(i + 1) == '/') {
                i += 2; while (i < source.length() && source.charAt(i) != '\n') i++; continue;
            }
            if (c == '/' && i + 1 < source.length() && source.charAt(i + 1) == '*') {
                int close = source.indexOf("*/", i + 2); i = close < 0 ? source.length() : close + 2; continue;
            }
            int start = i;
            if (c == '"' || c == '\'') {
                char quote = c; i++;
                while (i < source.length()) {
                    char next = source.charAt(i++);
                    if (next == '\\' && i < source.length()) i++;
                    else if (next == quote) break;
                }
                tokens.add(new Token(source.substring(start, i), start, i)); continue;
            }
            if (Character.isJavaIdentifierStart(c)) {
                i++;
                while (i < source.length() && Character.isJavaIdentifierPart(source.charAt(i))) i++;
            } else if (Character.isDigit(c)) {
                i++;
                while (i < source.length() && (Character.isJavaIdentifierPart(source.charAt(i)) || source.charAt(i) == '.')) i++;
            } else {
                i++;
                if (i < source.length() && isPair(c, source.charAt(i))) i++;
            }
            tokens.add(new Token(source.substring(start, i), start, i));
        }
        return tokens;
    }

    private static boolean isPair(char left, char right) {
        return (left == '=' && right == '=') || (left == '!' && right == '=')
                || (left == '<' && (right == '=' || right == '<'))
                || (left == '>' && (right == '=' || right == '>'))
                || (left == '&' && right == '&') || (left == '|' && right == '|')
                || (left == '+' && right == '+') || (left == '-' && right == '-')
                || (left == '+' && right == '=') || (left == '-' && right == '=')
                || (left == '*' && right == '=') || (left == '/' && right == '=');
    }

    private static int findToken(List<Token> tokens, int start, String wanted) {
        for (int i = start; i < tokens.size(); i++) {
            if (wanted.equals(tokens.get(i).text)) return i;
            if ("{".equals(tokens.get(i).text) || ";".equals(tokens.get(i).text)) return -1;
        }
        return -1;
    }

    private static int matching(List<Token> tokens, int openIndex, String open, String close) {
        int depth = 0;
        for (int i = openIndex; i < tokens.size(); i++) {
            if (open.equals(tokens.get(i).text)) depth++;
            else if (close.equals(tokens.get(i).text) && --depth == 0) return i;
        }
        return -1;
    }

    private static int statementEnd(List<Token> tokens, int start) {
        if (start < 0 || start >= tokens.size()) return -1;
        if ("{".equals(tokens.get(start).text)) {
            int depth = 0;
            for (int i = start; i < tokens.size(); i++) {
                if ("{".equals(tokens.get(i).text)) depth++;
                else if ("}".equals(tokens.get(i).text) && --depth == 0) return i;
            }
            return -1;
        }
        int parens = 0, brackets = 0;
        for (int i = start; i < tokens.size(); i++) {
            String text = tokens.get(i).text;
            if ("(".equals(text)) parens++;
            else if (")".equals(text)) parens--;
            else if ("[".equals(text)) brackets++;
            else if ("]".equals(text)) brackets--;
            if (";".equals(text) && parens <= 0 && brackets <= 0) return i;
            if ("{".equals(text)) return statementEnd(tokens, i);
            if ("}".equals(text) || "else".equals(text)) return i - 1;
        }
        return -1;
    }

    private static boolean insideIgnoredHeader(int index, List<int[]> headers) {
        for (int[] header : headers) if (index >= header[0] && index <= header[1]) return true;
        return false;
    }

    private static boolean isStatementPrefix(String value) {
        return "@".equals(value) || "public".equals(value) || "private".equals(value)
                || "protected".equals(value) || "final".equals(value) || "static".equals(value);
    }

    private static boolean isDeclarationStart(String value) {
        return "final".equals(value) || "var".equals(value) || "byte".equals(value) || "short".equals(value)
                || "int".equals(value) || "long".equals(value) || "float".equals(value) || "double".equals(value)
                || "boolean".equals(value) || "char".equals(value) || "String".equals(value)
                || (!value.isEmpty() && Character.isUpperCase(value.charAt(0)));
    }

    private static String declarationName(List<Token> tokens, int from, int end) {
        for (int i = from + 1; i < end; i++) if (isIdentifier(tokens.get(i).text)) return tokens.get(i).text;
        return "local value";
    }

    private static boolean isIdentifier(String value) {
        return !value.isEmpty() && Character.isJavaIdentifierStart(value.charAt(0));
    }

    private static String calledMethod(List<Token> tokens, int from, int end) {
        String method = null;
        int depth = 0, bestDepth = Integer.MAX_VALUE;
        for (int i = from; i + 1 < end; i++) {
            String candidate = tokens.get(i).text;
            if ("(".equals(candidate)) { depth++; continue; }
            if (")".equals(candidate)) { depth = Math.max(0, depth - 1); continue; }
            if (!"(".equals(tokens.get(i + 1).text) || !isIdentifier(candidate)) continue;
            if (i > from && "new".equals(tokens.get(i - 1).text)) continue;
            if ("if".equals(candidate) || "for".equals(candidate) || "while".equals(candidate)
                    || "switch".equals(candidate) || "catch".equals(candidate)
                    || "super".equals(candidate) || "this".equals(candidate)
                    || "new".equals(candidate)) continue;
            if (depth <= bestDepth) {
                method = candidate;
                bestDepth = depth;
            }
        }
        return method;
    }

    private static String labelFor(String method) {
        return method.replaceAll("([a-z0-9])([A-Z])", "$1 $2").toLowerCase();
    }

    private static String categoryFor(String method) {
        String name = method.toLowerCase();
        if (name.contains("move") || name.contains("goto") || name.contains("turn") || name.contains("walk") || name.contains("direction")) return "Motion";
        if (name.contains("costume") || name.contains("size") || name.equals("show") || name.equals("hide") || name.equals("say")) return "Looks";
        if (name.contains("sound") || name.contains("music") || name.contains("volume")) return "Sound";
        if (name.contains("key") || name.contains("click") || name.contains("receive") || name.contains("broadcast")) return "Events";
        if (name.contains("variable") || name.startsWith("set") || name.startsWith("change")) return "Variables";
        if (name.contains("touch") || name.contains("distance") || name.contains("mouse") || name.contains("collid")) return "Sensing";
        if (name.contains("wait") || name.contains("script") || name.contains("thread")) return "Control";
        return "My Blocks";
    }

    private static String literalType(String token) {
        if (token.length() >= 2 && token.charAt(0) == '"' && token.charAt(token.length() - 1) == '"') return "text";
        if (token.length() >= 3 && token.charAt(0) == '\'' && token.charAt(token.length() - 1) == '\'') return "character";
        if ("true".equals(token) || "false".equals(token)) return "boolean";
        if ("null".equals(token)) return "value";
        if (!token.isEmpty() && Character.isDigit(token.charAt(0))) return "number";
        return null;
    }

    private static String clean(String value) {
        return value == null ? "" : value.replace('"', '\'').replace('\r', ' ').replace('\n', ' ').replaceAll("\\s+", " ").trim();
    }
}

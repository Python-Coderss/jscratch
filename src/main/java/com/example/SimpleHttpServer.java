package com.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.UUID; // For unique filenames
import java.io.InputStream; // For reading request body

public class SimpleHttpServer {

    private static final String SCRIPTS_DIR_NAME = "scripts";
    private static final String WEBAPP_DIR_NAME = "webapp"; // New directory for web content
    private static final File SCRIPTS_DIR = new File(SCRIPTS_DIR_NAME);
    private static final File WEBAPP_DIR = new File(WEBAPP_DIR_NAME); // File object for webapp directory
    private static final Logger LOGGER = Logger.getLogger(SimpleHttpServer.class.getName());
    private static final JythonExecutor jythonExecutor = new JythonExecutor(); // Initialize JythonExecutor
    // Allow alphanumeric characters, underscore, hyphen, and dot.
    private static final Pattern ALLOWED_SCRIPT_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]+$");


    static {
        try {
            // Configure logger
            FileHandler fileHandler = new FileHandler("server.log", true); // Append to log, true for append
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.INFO);
            // Prevent logging to console by default, unless console handler is added explicitly
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            // Log to console if file logger fails
            LOGGER.log(Level.SEVERE, "Failed to initialize file logger. Logging to console.", e);
        }
    }

    public static void main(String[] args) throws IOException {
        // Check for scripts directory
        if (!SCRIPTS_DIR.exists() || !SCRIPTS_DIR.isDirectory()) {
            LOGGER.info("Scripts directory '" + SCRIPTS_DIR.getAbsolutePath() + "' not found, attempting to create it.");
            if (SCRIPTS_DIR.mkdirs()){ // Use mkdirs to create parent directories if necessary
                LOGGER.info("Scripts directory created at: " + SCRIPTS_DIR.getAbsolutePath());
            } else {
                LOGGER.warning("Failed to create scripts directory. Script execution might fail.");
                // Not exiting, as server might still be useful for static files or other endpoints
            }
        }

        // Check for webapp directory
        if (!WEBAPP_DIR.exists() || !WEBAPP_DIR.isDirectory()) {
            LOGGER.info("Webapp directory '" + WEBAPP_DIR.getAbsolutePath() + "' not found, attempting to create it.");
            if (WEBAPP_DIR.mkdirs()){ // Use mkdirs to create parent directories if necessary
                LOGGER.info("Webapp directory created at: " + WEBAPP_DIR.getAbsolutePath());
            } else {
                LOGGER.warning("Failed to create webapp directory. Static file serving might fail.");
            }
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        // Order of context registration matters for overlapping paths. Most specific first.
        server.createContext("/run/", new RunScriptHandler());
        server.createContext("/api/execute_program", new ExecuteProgramHandler()); // New handler
        server.createContext("/", new StaticFileHandler(WEBAPP_DIR_NAME)); // Static file handler for root

        server.setExecutor(null); // Creates a default executor
        server.start();
        LOGGER.info("Server started on port 8000. Scripts: " + SCRIPTS_DIR.getAbsolutePath() + ", Webapp: " + WEBAPP_DIR.getAbsolutePath());
    }

    // RootHandler is effectively replaced by StaticFileHandler serving index.html from webapp by default.
    // If a specific "Hello, World!" for / is still needed separate from static files,
    // it would require more complex logic in StaticFileHandler or a different path.

    static class RunScriptHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String requestPath = t.getRequestURI().getPath();
            // Basic check to ensure handler is not misused for other paths if context changes
            if (!requestPath.startsWith("/run/")) {
                sendResponse(t, 400, "Bad Request: Invalid path for RunScriptHandler.", requestPath, null);
                return;
            }
            LOGGER.info("RunScriptHandler received request for: " + requestPath + " from " + t.getRemoteAddress());

            String scriptName = requestPath.substring("/run/".length());

            if (scriptName.isEmpty()) {
                LOGGER.warning("Attempt to run script with empty name from " + t.getRemoteAddress() + " for path " + requestPath);
                sendResponse(t, 400, "Bad Request: Script name is missing.", requestPath, scriptName);
                return;
            }

            // Security: Validate script name characters
            if (!ALLOWED_SCRIPT_NAME_PATTERN.matcher(scriptName).matches()) {
                LOGGER.warning("Attempt to run script with invalid name (disallowed characters): " + scriptName + " from " + t.getRemoteAddress() + " for path " + requestPath);
                sendResponse(t, 400, "Bad Request: Script name contains invalid characters.", requestPath, scriptName);
                return;
            }

            // Security: Normalize path and check for directory traversal
            File scriptFile = new File(SCRIPTS_DIR, scriptName); // scriptName is already validated for not containing / or ..
            // Path normalization and ensuring it's within SCRIPTS_DIR
            // Since scriptName cannot contain '..' or '/', direct construction is safer.
            // However, canonical path check is best practice.
            try {
                String canonicalScriptsDirPath = SCRIPTS_DIR.getCanonicalPath();
                String canonicalScriptFilePath = scriptFile.getCanonicalPath();

                if (!canonicalScriptFilePath.startsWith(canonicalScriptsDirPath + File.separator) && !canonicalScriptFilePath.equals(canonicalScriptsDirPath)) {
                     // The check `!canonicalScriptFilePath.equals(canonicalScriptsDirPath)` is to prevent scriptFile being SCRIPTS_DIR itself.
                     // A script name should not be empty, so `scriptFile` will not be equal to `SCRIPTS_DIR`.
                     // The startsWith must ensure it's `canonicalScriptsDirPath` + `File.separator` unless scriptName is empty (which is checked).
                     // For robustness, if scriptName could be empty and resolve to SCRIPTS_DIR, that would be an issue.
                     // But empty scriptName is handled.
                    LOGGER.severe("Directory traversal attempt (post-validation check)! " +
                                  "Requested script path: " + canonicalScriptFilePath +
                                  ", Normalized script dir: " + canonicalScriptsDirPath +
                                  " from " + t.getRemoteAddress() + " for path " + requestPath);
                    sendResponse(t, 400, "Bad Request: Invalid script path (directory traversal attempt).", requestPath, scriptName);
                    return;
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error checking canonical path for script " + scriptName + " from " + t.getRemoteAddress() + " for path " + requestPath, e);
                sendResponse(t, 500, "Internal Server Error: Could not verify script path.", requestPath, scriptName);
                return;
            }


            if (!scriptFile.exists() || !scriptFile.isFile()) {
                LOGGER.warning("Script not found or not a file: " + scriptFile.getAbsolutePath() + " for request " + requestPath + " from " + t.getRemoteAddress());
                sendResponse(t, 404, "Not Found: Script '" + scriptName + "' not found.", requestPath, scriptName);
                return;
            }

            if (!scriptFile.canExecute()) {
                LOGGER.info("Script '" + scriptFile.getName() + "' is not executable for request " + requestPath + ". Attempting to set it executable.");
                if (!scriptFile.setExecutable(true)) {
                    LOGGER.severe("Failed to make script executable: " + scriptFile.getAbsolutePath() + " for request " + requestPath + " from " + t.getRemoteAddress());
                    sendResponse(t, 500, "Internal Server Error: Script '" + scriptName + "' is not executable and could not be made executable.", requestPath, scriptName);
                    return;
                }
                LOGGER.info("Script '" + scriptFile.getName() + "' set to executable for request " + requestPath);
            }

            try {
                ProcessBuilder pb = new ProcessBuilder(scriptFile.getAbsolutePath()); // Safe: path is treated as single arg
                pb.directory(SCRIPTS_DIR);

                LOGGER.info("Executing script: " + scriptFile.getAbsolutePath() + " for request " + requestPath + " from " + t.getRemoteAddress());
                Process process = pb.start();

                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                StringBuilder errorOutput = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorOutput.append(line).append("\n");
                    }
                }

                int exitCode = process.waitFor();
                String trimmedOutput = output.toString().trim();
                String trimmedErrorOutput = errorOutput.toString().trim();

                if (exitCode == 0) {
                    LOGGER.info("Script '" + scriptName + "' executed successfully (exit " + exitCode + ") for " + requestPath + ". Output: " + trimmedOutput);
                    sendResponse(t, 200, output.toString(), requestPath, scriptName);
                } else {
                    LOGGER.warning("Script '" + scriptName + "' execution failed (exit " + exitCode + ") for " + requestPath +
                                 ".\nStdOut:\n" + trimmedOutput + "\nStdErr:\n" + trimmedErrorOutput);
                    sendResponse(t, 500, "Script execution failed with exit code " + exitCode +
                                 ".\n---Standard Output---\n" + output.toString() +
                                 "\n---Error Output---\n" + errorOutput.toString(), requestPath, scriptName);
                }

            } catch (IOException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error executing script '" + scriptName + "' for request " + requestPath, e);
                sendResponse(t, 500, "Internal Server Error: " + e.getMessage(), requestPath, scriptName);
            }
        }
    }

    static class StaticFileHandler implements HttpHandler {
        private final String webappRoot;

        public StaticFileHandler(String webappRoot) {
            this.webappRoot = webappRoot;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            String requestPath = t.getRequestURI().getPath();
            LOGGER.info("StaticFileHandler received request for: " + requestPath + " from " + t.getRemoteAddress());

            // Sanitize and normalize path
            String requestedFile = requestPath;
            if (requestedFile.equals("/") || requestedFile.isEmpty()) {
                requestedFile = "/index.html";
            }

            // Basic security: prevent directory traversal.
            // Normalize the path and ensure it doesn't try to escape the webappRoot.
            Path filePath = Paths.get(this.webappRoot, requestedFile).normalize();
            File file = filePath.toFile();

            // Check if the normalized path is still within the webappRoot directory
            File webappRootDir = new File(this.webappRoot);
            if (!file.getCanonicalPath().startsWith(webappRootDir.getCanonicalPath())) {
                LOGGER.warning("Directory traversal attempt for static file: " + requestedFile + " (resolved to " + file.getCanonicalPath() + ") from " + t.getRemoteAddress());
                sendResponse(t, 403, "Forbidden: Access denied.", requestPath, requestedFile);
                return;
            }

            if (file.isDirectory()) {
                // If it's a directory, try to serve index.html from within it
                filePath = Paths.get(this.webappRoot, requestedFile, "index.html").normalize();
                file = filePath.toFile();
                 if (!file.getCanonicalPath().startsWith(webappRootDir.getCanonicalPath())) { // Re-check after appending index.html
                    LOGGER.warning("Directory traversal attempt for static file (dir index): " + requestedFile + " from " + t.getRemoteAddress());
                    sendResponse(t, 403, "Forbidden: Access denied.", requestPath, requestedFile);
                    return;
                }
            }

            if (file.exists() && file.isFile()) {
                String contentType = guessContentType(file.getName());
                t.getResponseHeaders().set("Content-Type", contentType);
                t.sendResponseHeaders(200, file.length());
                try (OutputStream os = t.getResponseBody();
                     java.nio.file.Files.copy(file.toPath(), os)) {
                    // Content is streamed
                }
                LOGGER.info("Served static file: " + file.getPath() + " as " + contentType + " for " + requestPath);
            } else {
                LOGGER.warning("Static file not found: " + file.getPath() + " for " + requestPath);
                sendResponse(t, 404, "Not Found: " + requestedFile, requestPath, requestedFile);
            }
        }

        private String guessContentType(String fileName) {
            String lcFileName = fileName.toLowerCase();
            if (lcFileName.endsWith(".html") || lcFileName.endsWith(".htm")) {
                return "text/html; charset=utf-8";
            } else if (lcFileName.endsWith(".css")) {
                return "text/css; charset=utf-8";
            } else if (lcFileName.endsWith(".js")) {
                return "application/javascript; charset=utf-8";
            } else if (lcFileName.endsWith(".png")) {
                return "image/png";
            } else if (lcFileName.endsWith(".jpg") || lcFileName.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (lcFileName.endsWith(".gif")) {
                return "image/gif";
            } else if (lcFileName.endsWith(".ico")) {
                return "image/x-icon";
            }
            // Fallback to URLConnection's guess or a default
            String guessed = URLConnection.guessContentTypeFromName(fileName);
            return (guessed != null) ? guessed : "application/octet-stream";
        }
    }

    static class ExecuteProgramHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String requestPath = t.getRequestURI().getPath();
            LOGGER.info("ExecuteProgramHandler received request for: " + requestPath + " from " + t.getRemoteAddress());

            if (!"POST".equals(t.getRequestMethod())) {
                LOGGER.warning("Invalid method for " + requestPath + ": " + t.getRequestMethod());
                sendResponse(t, 405, "Method Not Allowed. Only POST is supported.", requestPath, "ExecuteProgram");
                return;
            }

            // Read the JSON request body
            String requestBodyString;
            try (InputStream is = t.getRequestBody();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line); // Read as a single line, assuming compact JSON
                }
                requestBodyString = sb.toString();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error reading request body for " + requestPath, e);
                sendResponse(t, 500, "Internal Server Error: Could not read request body.", requestPath, "ExecuteProgram");
                return;
            }

            if (requestBodyString.trim().isEmpty()) {
                LOGGER.warning("Empty request body received for " + requestPath);
                sendResponse(t, 400, "Bad Request: Request body cannot be empty.", requestPath, "ExecuteProgram");
                return;
            }

            LOGGER.info("Received JSON for " + requestPath + ": " + requestBodyString);

            // Simple JSON Parsing (Fragile - assumes specific structure)
            String blockType = null;
            String pythonCode = null;
            String sayText = null;

            try {
                // Extract blockType: "type":"VALUE"
                int typeKeyIndex = requestBodyString.indexOf("\"type\":\"");
                if (typeKeyIndex == -1) throw new IllegalArgumentException("Missing 'type' field in JSON.");
                int typeValueStart = typeKeyIndex + "\"type\":\"".length();
                int typeValueEnd = requestBodyString.indexOf("\"", typeValueStart);
                if (typeValueEnd == -1) throw new IllegalArgumentException("Malformed 'type' field in JSON.");
                blockType = requestBodyString.substring(typeValueStart, typeValueEnd);

                if ("PYTHON_BLOCK".equals(blockType)) {
                    // Extract code: "inputs":{"CODE":"VALUE"}
                    int codeKeyIndex = requestBodyString.indexOf("\"CODE\":\"");
                    if (codeKeyIndex == -1) throw new IllegalArgumentException("Missing 'CODE' input for PYTHON_BLOCK.");
                    int codeValueStart = codeKeyIndex + "\"CODE\":\"".length();
                    // Find the closing quote for CODE, carefully handling escaped quotes
                    // This is where manual parsing gets very tricky. A simple approach:
                    int codeValueEnd = -1;
                    boolean escaped = false;
                    for(int i = codeValueStart; i < requestBodyString.length(); i++) {
                        if (requestBodyString.charAt(i) == '\\') {
                            escaped = !escaped;
                        } else if (requestBodyString.charAt(i) == '"' && !escaped) {
                            codeValueEnd = i;
                            break;
                        } else {
                            escaped = false;
                        }
                    }
                    if (codeValueEnd == -1) throw new IllegalArgumentException("Malformed 'CODE' input for PYTHON_BLOCK.");
                    pythonCode = requestBodyString.substring(codeValueStart, codeValueEnd)
                                     .replace("\\n", "\n")
                                     .replace("\\\"", "\"")
                                     .replace("\\\\", "\\")
                                     .replace("\\r", "\r")
                                     .replace("\\t", "\t"); // Basic unescaping
                } else if ("SAY_BLOCK".equals(blockType)) {
                    // Extract text: "inputs":{"TEXT":"VALUE"}
                    int textKeyIndex = requestBodyString.indexOf("\"TEXT\":\"");
                    if (textKeyIndex == -1) throw new IllegalArgumentException("Missing 'TEXT' input for SAY_BLOCK.");
                    int textValueStart = textKeyIndex + "\"TEXT\":\"".length();
                    int textValueEnd = requestBodyString.indexOf("\"", textValueStart); // Assumes no escaped quotes in say text for simplicity here
                    if (textValueEnd == -1) throw new IllegalArgumentException("Malformed 'TEXT' input for SAY_BLOCK.");
                    sayText = requestBodyString.substring(textValueStart, textValueEnd)
                                    .replace("\\n", "\n")
                                    .replace("\\\"", "\"")
                                    .replace("\\\\", "\\")
                                    .replace("\\r", "\r")
                                    .replace("\\t", "\t"); // Basic unescaping for say text too
                }

            } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
                LOGGER.log(Level.WARNING, "Error parsing JSON request body for " + requestPath + ": " + e.getMessage() + ". Body: " + requestBodyString, e);
                sendResponse(t, 400, "Bad Request: Malformed JSON or missing required fields. " + e.getMessage(), requestPath, "ExecuteProgram");
                return;
            }


            if ("PYTHON_BLOCK".equals(blockType)) {
                if (pythonCode == null || pythonCode.trim().isEmpty()) {
                     LOGGER.warning("Empty Python code received after parsing for " + requestPath);
                     sendResponse(t, 400, "Bad Request: Python code cannot be empty.", requestPath, "ExecuteProgramPython");
                     return;
                }
                LOGGER.info("Executing PYTHON_BLOCK via Jython for request: " + requestPath);
                try {
                    JythonExecutor.ExecutionResult result = jythonExecutor.executeScript(pythonCode);
                    String responseBody = result.toString();
                    if (result.hasError()) {
                        LOGGER.warning("Jython execution for " + requestPath + " (PYTHON_BLOCK) completed with errors/exceptions. Combined output:\n" + responseBody);
                    } else {
                        LOGGER.info("Jython execution for " + requestPath + " (PYTHON_BLOCK) completed successfully. Output:\n" + responseBody);
                    }
                    t.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
                    sendResponse(t, 200, responseBody, requestPath, "ExecuteProgramPython");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Unexpected Java error during Jython execution for " + requestPath, e);
                    sendResponse(t, 500, "Internal Server Error: Failed to execute Python script via Jython due to server-side Java error.", requestPath, "ExecuteProgramPython");
                }
            } else if ("SAY_BLOCK".equals(blockType)) {
                if (sayText == null) { // Should not happen if parsing logic is correct and TEXT is mandatory
                     LOGGER.warning("SAY_BLOCK text is null after parsing for " + requestPath);
                     sendResponse(t, 400, "Bad Request: SAY_BLOCK text is missing.", requestPath, "ExecuteProgramSay");
                     return;
                }
                LOGGER.info("Executing SAY_BLOCK for request: " + requestPath + ". Text: \"" + sayText + "\"");
                String responseBody = "[Output] SAY: " + sayText;
                t.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
                sendResponse(t, 200, responseBody, requestPath, "ExecuteProgramSay");
            } else {
                LOGGER.warning("Unknown block type received for " + requestPath + ": " + blockType);
                sendResponse(t, 400, "Bad Request: Unknown block type '" + blockType + "'.", requestPath, "ExecuteProgram");
            }
        }
    }

    // Common response sender utility - overloaded for scriptName context
    private static void sendResponse(HttpExchange t, int statusCode, String response, String requestPathForLog, String contextName) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        t.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8"); // Ensure content type for error messages too
        t.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = t.getResponseBody();
        os.write(responseBytes);
        os.close();
        String logContext = (contextName != null && !contextName.isEmpty()) ? "'" + contextName + "' in " : "";
        if (statusCode >= 400) {
            LOGGER.warning("Sent error response " + statusCode + " for " + logContext + requestPathForLog + ": " + response.lines().findFirst().orElse(""));
        }
    }
}

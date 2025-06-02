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
import java.util.UUID;
import java.io.InputStream;

// JSON library imports
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

// Additional imports for Sprite management
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList; // Used in setupDefaultState

public class SimpleHttpServer {

    private static final String SCRIPTS_DIR_NAME = "scripts";
    private static final String WEBAPP_DIR_NAME = "webapp"; // New directory for web content
    private static final File SCRIPTS_DIR = new File(SCRIPTS_DIR_NAME);
    private static final File WEBAPP_DIR = new File(WEBAPP_DIR_NAME); // File object for webapp directory
    private static final Logger LOGGER = Logger.getLogger(SimpleHttpServer.class.getName());
    private static final JythonExecutor jythonExecutor = new JythonExecutor(); // Initialize JythonExecutor
    private static final Map<String, Sprite> projectSprites = new ConcurrentHashMap<>(); // For storing sprites
    private static final Map<String, Object> projectGlobalVariables = new ConcurrentHashMap<>(); // For global variables
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
        setupDefaultState(); // Initialize with a default sprite

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
        LOGGER.info("Default sprites initialized: " + projectSprites.keySet());
    }

    private static void setupDefaultState() {
        // For now, costumes and sounds are metadata lists. Actual data is client-side.
        // Create a default costume metadata entry
        Map<String, String> defaultCostumeMeta = new HashMap<>();
        defaultCostumeMeta.put("id", "default_costume_id"); // Matches client-side placeholder
        defaultCostumeMeta.put("name", "default");
        // defaultCostumeMeta.put("fileName", "placeholder.png"); // If we had server-side assets

        List<Map<String, String>> defaultCostumesList = new ArrayList<>();
        defaultCostumesList.add(defaultCostumeMeta);

        // Create the default sprite
        Sprite defaultSprite = new Sprite(
            "sprite1",                  // id
            "Sprite1",                  // name
            0,                          // x
            0,                          // y
            "default_costume_id",       // currentCostumeId
            defaultCostumesList,        // costumes list
            new ArrayList<>()           // empty sounds list
        );
        projectSprites.put(defaultSprite.getId(), defaultSprite);
        LOGGER.info("Default sprite '" + defaultSprite.getName() + "' created with ID '" + defaultSprite.getId() + "'.");

        // Pre-populate example variables
        projectGlobalVariables.put("global_score", 0);
        projectGlobalVariables.put("global_message", "Hello Everyone!");

        if (defaultSprite != null) { // Should not be null here
            defaultSprite.setLocalVariable("my_sprite_var", 100);
            defaultSprite.setLocalVariable("sprite_greeting", "Hi from Sprite1");
            LOGGER.info("Pre-populated global and local variables for default sprite.");
        }
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

        // Helper method to resolve input values, which might be literals or variable reporters
        private Object resolveInputValue(JSONObject parentInputs, String inputKey, String targetSpriteId, StringBuilder aggregatedOutput) {
            Object rawValue = parentInputs.opt(inputKey);

            if (rawValue instanceof JSONObject) {
                JSONObject reporter = (JSONObject) rawValue;
                if ("VARIABLE".equals(reporter.optString("reporterType"))) {
                    String varName = reporter.optString("name");
                    String varScope = reporter.optString("scope", "global");
                    Object varValue = null;
                    boolean found = false;

                    if ("global".equals(varScope)) {
                        if (projectGlobalVariables.containsKey(varName)) {
                            varValue = projectGlobalVariables.get(varName);
                            found = true;
                        }
                    } else { // "local"
                        Sprite sprite = projectSprites.get(targetSpriteId);
                        if (sprite != null) {
                            // Check local directly, assuming localVariables map exists and is up-to-date
                            varValue = sprite.getLocalVariable(varName);
                            if (sprite.getAllLocalVariables().containsKey(varName)) { // Check if key exists even if value is null
                                found = true;
                            }
                        } else {
                            LOGGER.warning("resolveInputValue: Target sprite '" + targetSpriteId + "' not found for local variable '" + varName + "'.");
                            aggregatedOutput.append(String.format("  Error: Sprite '%s' not found for local variable '%s'.\n", targetSpriteId, varName));
                            return 0; // Default value for unresolved local variable from missing sprite
                        }
                    }

                    if (found) {
                        LOGGER.info(String.format("Resolved variable '%s' (scope: %s) to value: %s", varName, varScope, varValue));
                        return varValue; // Could be null if variable exists but has null value
                    } else {
                        LOGGER.warning(String.format("resolveInputValue: Variable '%s' (scope: %s) not found.", varName, varScope));
                        aggregatedOutput.append(String.format("  Error: Variable '%s' (scope: %s) not found.\n", varName, varScope));
                        return 0; // Default value for unfound variables (Scratch-like behavior)
                    }
                }
            }

            // If not a variable reporter, treat as literal and parse
            if (rawValue instanceof String) {
                String valueStr = (String) rawValue;
                if (valueStr.matches("-?\\d+(\\.\\d+)?")) {
                    try {
                        double doubleVal = Double.parseDouble(valueStr);
                        if (doubleVal == (long) doubleVal) return (long) doubleVal;
                        return doubleVal;
                    } catch (NumberFormatException nfe) { /* Fall through to return as string */ }
                } else if (valueStr.equalsIgnoreCase("true")) {
                    return true;
                } else if (valueStr.equalsIgnoreCase("false")) {
                    return false;
                }
                return valueStr; // Return as string if no other parsing fits
            } else if (rawValue instanceof Number || rawValue instanceof Boolean) {
                return rawValue; // Already parsed by JSONObject
            } else if (rawValue == null || JSONObject.NULL.equals(rawValue)) {
                return null; // Or a suitable default like "" or 0
            }

            // Default: return as string or a default value if rawValue is not a recognized type
            return (rawValue != null) ? rawValue.toString() : "";
        }


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

            LOGGER.info("Received request body for " + requestPath + ": " + requestBodyString);
            StringBuilder aggregatedOutput = new StringBuilder();

            try {
                JSONArray program = new JSONArray(requestBodyString); // Parse the whole body as an array

                for (int i = 0; i < program.length(); i++) {
                    JSONObject block = program.getJSONObject(i);
                    String blockType = block.optString("type", "UNKNOWN_BLOCK"); // Default to UNKNOWN_BLOCK if type is missing
                    JSONObject inputs = block.optJSONObject("inputs");
                    if (inputs == null) inputs = new JSONObject(); // Ensure inputs is never null for optString/optInt calls

                    // String blockId = block.optString("id", "no_id"); // For logging if needed
                    // LOGGER.info("Processing block " + (i + 1) + "/" + program.length() + " (ID: " + blockId + ", Type: " + blockType + ")");
                    aggregatedOutput.append("Block ").append(i + 1).append(" (").append(blockType).append("):\n");

                    switch (blockType) {
                        case "PYTHON_BLOCK": {
                            String pythonCode = inputs.optString("CODE", "");
                            if (pythonCode.trim().isEmpty()) {
                                aggregatedOutput.append("  Error: Python code was empty.\n");
                                LOGGER.warning("Empty Python code for PYTHON_BLOCK.");
                                continue;
                            }
                            LOGGER.info("Executing PYTHON_BLOCK via Jython.");
                            JythonExecutor.ExecutionResult result = jythonExecutor.executeScript(pythonCode);
                            String[] lines = result.toString().split("\n");
                            for (String line : lines) {
                                aggregatedOutput.append("  ").append(line).append("\n");
                            }
                            if (result.hasError()) {
                                LOGGER.warning("Jython execution for PYTHON_BLOCK had errors.");
                            }
                            break;
                        }
                        case "CHANGE_VARIABLE_BLOCK": {
                            String varNameChange = inputs.optString("VARIABLE_NAME", null);
                            String varScopeChange = inputs.optString("VARIABLE_SCOPE", "global");
                            // VALUE to change by can itself be a variable or a literal
                            // Pass aggregatedOutput to resolveInputValue so it can log errors there
                            Object resolvedValueToChangeBy = resolveInputValue(inputs, "VALUE", "sprite1", aggregatedOutput); // Assuming "sprite1" for now

                            if (varNameChange == null || varNameChange.trim().isEmpty()) {
                                aggregatedOutput.append("  Error: Variable name not provided for CHANGE_VARIABLE_BLOCK.\n");
                                LOGGER.warning("Variable name not provided for CHANGE_VARIABLE_BLOCK");
                                break;
                            }

                            double numValueToChangeBy;
                            if (resolvedValueToChangeBy instanceof Number) {
                                numValueToChangeBy = ((Number) resolvedValueToChangeBy).doubleValue();
                            } else {
                                try {
                                    numValueToChangeBy = Double.parseDouble(String.valueOf(resolvedValueToChangeBy));
                                } catch (NumberFormatException e) {
                                    aggregatedOutput.append(String.format("  Error: Value for CHANGE_VARIABLE_BLOCK ('%s') on variable '%s' is not a number.\n", String.valueOf(resolvedValueToChangeBy), varNameChange));
                                    LOGGER.warning(String.format("Non-numeric value '%s' used in CHANGE_VARIABLE for variable '%s'", String.valueOf(resolvedValueToChangeBy), varNameChange));
                                    break;
                                }
                            }

                            Map<String, Object> targetMapChange = null;
                            // Sprite targetSpriteChange = null; // Not strictly needed if only using targetMapChange
                            String logScopeDisplayChange = "Global"; // For logging

                            if (varScopeChange.equals("global")) {
                                targetMapChange = SimpleHttpServer.this.projectGlobalVariables;
                            } else {
                                String targetSpriteIdChange = "sprite1"; // Hardcoded for now
                                Sprite spriteForChange = SimpleHttpServer.this.projectSprites.get(targetSpriteIdChange);
                                if (spriteForChange != null) {
                                    targetMapChange = spriteForChange.getAllLocalVariables();
                                    logScopeDisplayChange = "Local for " + spriteForChange.getName();
                                } else {
                                    aggregatedOutput.append(String.format("  Error: Sprite '%s' not found for local variable '%s' in CHANGE block.\n", targetSpriteIdChange, varNameChange));
                                    LOGGER.warning("Sprite not found for CHANGE_VARIABLE (local): " + targetSpriteIdChange);
                                    break;
                                }
                            }

                            if (targetMapChange != null) {
                                Object currentValueObj = targetMapChange.get(varNameChange);
                                double currentNumericValue = 0;
                                if (currentValueObj instanceof Number) {
                                    currentNumericValue = ((Number) currentValueObj).doubleValue();
                                } else if (currentValueObj != null && !String.valueOf(currentValueObj).isEmpty()) { // check if not null and not empty string
                                    try {
                                        currentNumericValue = Double.parseDouble(String.valueOf(currentValueObj));
                                    } catch (NumberFormatException e) {
                                        // Variable exists but isn't a number. Scratch treats as 0 in 'change by'.
                                        LOGGER.warning(String.format("Variable '%s' (%s) current value ('%s') is not a number, treating as 0 for change op.", varNameChange, logScopeDisplayChange, currentValueObj));
                                    }
                                }
                                // If variable doesn't exist (currentValueObj is null), Scratch creates it and treats its value as 0 for 'change by'.

                                double newValue = currentNumericValue + numValueToChangeBy;
                                targetMapChange.put(varNameChange, newValue);

                                aggregatedOutput.append(String.format("  %s variable '%s' changed by %s, new value is %s.\n", logScopeDisplayChange, varNameChange, numValueToChangeBy, newValue));
                                LOGGER.info(String.format("Changed %s variable '%s' by %s to %s", logScopeDisplayChange, varNameChange, numValueToChangeBy, newValue));
                            }
                            break;
                        }
                        case "SET_VARIABLE_BLOCK": {
                            String varNameSet = inputs.optString("VARIABLE_NAME", null);
                            String varScopeSet = inputs.optString("VARIABLE_SCOPE", "global");
                            // Resolve the VALUE input, which might be a literal or a variable reporter
                            Object valueToSet = resolveInputValue(inputs, "VALUE", "sprite1", aggregatedOutput); // Assuming "sprite1" for now

                            if (varNameSet == null || varNameSet.trim().isEmpty()) {
                                aggregatedOutput.append("  Error: Variable name not provided for SET_VARIABLE_BLOCK.\n");
                                LOGGER.warning("Variable name missing in SET_VARIABLE_BLOCK.");
                                continue;
                            }

                            if (varScopeSet.equals("global")) {
                                projectGlobalVariables.put(varNameSet, valueToSet);
                                aggregatedOutput.append(String.format("  Global variable '%s' set to %s.\n", varNameSet, valueToSet));
                                LOGGER.info(String.format("Set global variable '%s' to %s", varNameSet, valueToSet));
                            } else { // "local"
                                String targetSpriteIdSet = "sprite1"; // Hardcoded
                                Sprite localSpriteSet = projectSprites.get(targetSpriteIdSet);
                                if (localSpriteSet != null) {
                                    localSpriteSet.setLocalVariable(varNameSet, valueToSet);
                                    aggregatedOutput.append(String.format("  Local variable '%s' for sprite '%s' set to %s.\n", varNameSet, localSpriteSet.getName(), valueToSet));
                                    LOGGER.info(String.format("Set local variable '%s' for sprite '%s' to %s", varNameSet, localSpriteSet.getName(), valueToSet));
                                } else {
                                    aggregatedOutput.append(String.format("  Error: Sprite '%s' not found for setting local variable '%s'.\n", targetSpriteIdSet, varNameSet));
                                    LOGGER.warning("Sprite not found for SET_VARIABLE_BLOCK (local): " + targetSpriteIdSet);
                                }
                            }
                            break;
                        }
                        case "SWITCH_COSTUME_BLOCK": {
                            String costumeId = inputs.optString("COSTUME_ID", null); // Assuming direct costume ID for now
                            String costumeNameForLog = inputs.optString("COSTUME_NAME", costumeId);
                            String targetSpriteId_Looks = "sprite1"; // Hardcoded
                            Sprite looksSprite = projectSprites.get(targetSpriteId_Looks);

                            if (costumeId == null || costumeId.isEmpty()) {
                                aggregatedOutput.append("  Error: No costume ID for SWITCH_COSTUME_BLOCK.\n");
                                LOGGER.warning("Costume ID missing in SWITCH_COSTUME_BLOCK.");
                                continue;
                            }
                            if (looksSprite != null) {
                                boolean costumeExists = looksSprite.getCostumes().stream().anyMatch(c -> costumeId.equals(c.get("id")));
                                if (costumeExists) {
                                    looksSprite.setCurrentCostumeId(costumeId);
                                    aggregatedOutput.append(String.format("  Sprite '%s' switched to costume '%s'.\n", looksSprite.getName(), costumeNameForLog));
                                    LOGGER.info("Executed SWITCH_COSTUME_BLOCK for " + looksSprite.getName() + " to " + costumeNameForLog);
                                } else {
                                    aggregatedOutput.append(String.format("  Error: Costume ID '%s' not found for sprite '%s'.\n", costumeId, looksSprite.getName()));
                                    LOGGER.warning("Costume ID " + costumeId + " not found for " + looksSprite.getName());
                                }
                            } else {
                                aggregatedOutput.append(String.format("  Error: Sprite '%s' not found for SWITCH_COSTUME_BLOCK.\n", targetSpriteId_Looks));
                                LOGGER.warning("Sprite not found for SWITCH_COSTUME_BLOCK: " + targetSpriteId_Looks);
                            }
                            break;
                        }
                        case "SAY_BLOCK": {
                            Object sayValueRaw = resolveInputValue(inputs, "TEXT", "sprite1", aggregatedOutput);
                            String textToSay = String.valueOf(sayValueRaw); // Convert resolved value to String
                            LOGGER.info("Executing SAY_BLOCK: " + textToSay);
                            aggregatedOutput.append("  [Output] SAY: ").append(textToSay).append("\n");
                            break;
                        }
                        case "LOOP_BLOCK": {
                            Object countRaw = resolveInputValue(inputs, "COUNT", "sprite1", aggregatedOutput);
                            int count = 0;
                            if (countRaw instanceof Number) {
                                count = ((Number) countRaw).intValue();
                            } else {
                                try { count = Integer.parseInt(String.valueOf(countRaw)); }
                                catch (NumberFormatException e) {
                                    aggregatedOutput.append("  Error: Loop count was not a valid number ('"+countRaw+"').\n");
                                    LOGGER.warning("Invalid loop count: " + countRaw);
                                    continue;
                                }
                            }
                            LOGGER.info("Encountered LOOP_BLOCK with count: " + count);
                            aggregatedOutput.append("  Loop ").append(count).append(" times (Note: execution of children not yet implemented).\n");
                            break;
                        }
                        case "GOTO_XY_BLOCK": {
                            Object xValRaw = resolveInputValue(inputs, "X", "sprite1", aggregatedOutput);
                            Object yValRaw = resolveInputValue(inputs, "Y", "sprite1", aggregatedOutput);
                            double xVal = 0.0, yVal = 0.0;

                            try {
                                xVal = (xValRaw instanceof Number) ? ((Number)xValRaw).doubleValue() : Double.parseDouble(String.valueOf(xValRaw));
                                yVal = (yValRaw instanceof Number) ? ((Number)yValRaw).doubleValue() : Double.parseDouble(String.valueOf(yValRaw));
                            } catch (NumberFormatException e) {
                                aggregatedOutput.append("  Error: Invalid coordinate for GOTO_XY_BLOCK (X: '"+xValRaw+"', Y: '"+yValRaw+"').\n");
                                LOGGER.warning("Invalid coordinates for GOTO_XY: X="+xValRaw+", Y="+yValRaw);
                                continue;
                            }

                            String targetSpriteId = "sprite1"; // Hardcoded
                            Sprite currentSprite = projectSprites.get(targetSpriteId);
                            if (currentSprite != null) {
                                currentSprite.setX(xVal);
                                currentSprite.setY(yVal);
                                aggregatedOutput.append(String.format("  Sprite '%s' moved to X: %.2f, Y: %.2f.\n", currentSprite.getName(), xVal, yVal));
                                LOGGER.info(String.format("Executed GOTO_XY_BLOCK for %s to X=%.2f, Y=%.2f", currentSprite.getName(), xVal, yVal));
                            } else {
                                aggregatedOutput.append(String.format("  Error: Sprite '%s' not found for GOTO_XY_BLOCK.\n", targetSpriteId));
                                LOGGER.warning("Sprite not found for GOTO_XY_BLOCK: " + targetSpriteId);
                            }
                            break;
                        }
                        default:
                            LOGGER.warning("Unknown block type encountered: " + blockType);
                            aggregatedOutput.append("  Error: Unknown block type '").append(blockType).append("'.\n");
                            break;
                    }
                    aggregatedOutput.append("\n"); // Add a blank line after each block's output section
                }
                sendResponse(t, 200, aggregatedOutput.toString(), requestPath, "ExecuteProgramArray");

            } catch (JSONException e) {
                LOGGER.log(Level.WARNING, "JSON parsing error for " + requestPath + ": " + e.getMessage() + ". Body: " + requestBodyString, e);
                sendResponse(t, 400, "Bad Request: Malformed JSON program structure. " + e.getMessage(), requestPath, "ExecuteProgram");
            } catch (Exception e) { // Catch-all for other unexpected errors during processing
                LOGGER.log(Level.SEVERE, "Unexpected error processing program for " + requestPath + ": " + e.getMessage() + ". Body: " + requestBodyString, e);
                sendResponse(t, 500, "Internal Server Error: Could not execute program due to an unexpected server error.", requestPath, "ExecuteProgram");
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

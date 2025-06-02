# Design and Architecture Notes

This document outlines the conceptual architecture for program execution in the Java Scratch-Python application, particularly focusing on how different block types (Python and "Standard Blocks") are intended to be handled.

## Program Execution Architecture

### 1. Overall Program Flow

The envisioned program execution process is as follows:

1.  **Frontend Program Representation:** The user arranges blocks in the "Script Assembly Area" on the frontend. This sequence of blocks is translated into a structured format, likely JSON.
2.  **API Request:** When the "Run Program" button is clicked, the frontend sends this JSON representation of the block sequence to a backend API endpoint, for example, `/api/execute_program` (which currently handles raw Python code but would be extended).
3.  **Backend Parsing:** The `ExecuteProgramHandler` (or a similar handler) on the Java backend receives the JSON payload. It parses this JSON to understand the sequence and types of blocks to be executed.
4.  **Orchestration:** A new backend component, let's call it `ProgramOrchestrator` or `BlockExecutor`, takes the parsed block sequence. This orchestrator iterates through the blocks one by one.
5.  **Block Execution:** Based on the `type` of each block, the orchestrator dispatches the execution to the appropriate mechanism:
    *   Python blocks are executed using the `JythonExecutor`.
    *   "Standard Blocks" are executed by calling corresponding Java methods.
6.  **State Management:** A `ProgramContext` object is maintained and passed through the execution, allowing blocks to share state (like variables or sprite properties in the future) and accumulate output.
7.  **Response Compilation:** After all blocks have been processed, or if an error halts execution, the orchestrator compiles a result (e.g., accumulated output, error messages) and sends it back to the frontend.

### 2. JSON Program Structure

The program constructed on the frontend would be serialized into a JSON array of block objects. Each block object would conform to a defined structure.

**Proposed Block Object Fields:**

*   `id`: (String, Optional) A unique identifier for the block instance, useful for debugging, state management, or advanced features like jumping between blocks (though not immediately planned).
*   `type`: (String, Required) Specifies the kind of block, e.g., `"PYTHON_BLOCK"`, `"SAY_BLOCK"`, `"MOVE_SPRITE_BLOCK"`, `"LOOP_BLOCK"`.
*   `inputs`: (Object, Optional) A key-value map where keys are input names defined for that block type (e.g., `MESSAGE` for a "SAY_BLOCK", `CODE` for a "PYTHON_BLOCK", `STEPS` for a "MOVE_SPRITE_BLOCK"). Values would be the user-provided data for these inputs.
*   `children`: (Array, Optional) An array of block objects representing nested blocks, primarily used by control flow blocks like loops or conditionals. Each element in `children` would be a complete block object itself.

**Example JSON Snippet:**

```json
[
  {
    "id": "block_001",
    "type": "SAY_BLOCK",
    "inputs": {
      "MESSAGE": "Hello, World!"
    }
  },
  {
    "id": "block_002",
    "type": "PYTHON_BLOCK",
    "inputs": {
      "CODE": "name = 'AI Assistant'\\nprint(f'My name is {name}')\\ncontext.setVariable('py_var', 123)"
    }
  },
  {
    "id": "block_003",
    "type": "SAY_BLOCK",
    "inputs": {
      "MESSAGE": "Python block executed. Check for py_var."
    }
  }
  // Example of a future control flow block:
  {
    "id": "block_004",
    "type": "LOOP_BLOCK",
    "inputs": {
      "COUNT": 3
    },
    "children": [] // Initially empty, for future nested blocks
  }
  // Example of a future control flow block with children:
  // {
  //   "id": "block_005",
  //   "type": "REPEAT_BLOCK",
  //   "inputs": {
  //     "TIMES": 3
  //   },
  //   "children": [
  //     {
  //       "id": "block_006",
  //       "type": "MOVE_SPRITE_BLOCK",
  //       "inputs": { "STEPS": 10 }
  //     }
  //   ]
  // }
]
```

### 2.5 Sprite Data Model (Conceptual)

The application now incorporates a basic concept of sprites, primarily managed on the client-side but with a corresponding Java class for server-side understanding if state were to be fully synchronized or persisted.

*   **Client-Side (`webapp/js/app.js`):**
    *   `projectSprites`: An array holding all sprite objects for the current session.
    *   `activeSpriteId`: The ID of the currently selected sprite, whose scripts and assets are being edited/viewed.
    *   **Sprite Object Structure (JavaScript):**
        ```javascript
        {
            id: String, // Unique ID (e.g., 'sprite_167...')
            name: String, // e.g., 'Sprite1'
            x: Number,    // X-coordinate on stage (0 is center)
            y: Number,    // Y-coordinate on stage (0 is center, positive is up)
            currentCostumeId: String, // ID of the currently active costume
            scripts: Array, // Array of block objects (as defined in JSON Program Structure)
            costumes: [   // Array of costume objects
                { id: String, name: String, dataURL: String /*Base64 encoded image*/ }
            ],
            sounds: [     // Array of sound objects
                { id: String, name: String, dataURL: String /*Base64 encoded audio*/ }
            ]
        }
        ```
    *   A default "Sprite1" is created on initialization with a placeholder costume and an empty script list.

*   **Server-Side (`Sprite.java`):**
    *   The `com.example.Sprite` Java class mirrors this structure (`id`, `name`, `x`, `y`, `currentCostumeId`, `costumes` (List<Map<String, String>>), `sounds` (List<Map<String, String>>)).
    *   The server initializes a default "sprite1" in its `projectSprites` map. Currently, block actions like "Go to X,Y" and "Switch to costume" on the backend operate on this hardcoded "sprite1".

*   **Variable System Data (Conceptual):**
    *   **Client-Side (`webapp/js/app.js`):**
        *   `projectGlobalVariables`: Array of global variable objects.
        *   `sprite.variables`: Array of local variable objects within each sprite in `projectSprites`.
        *   **Variable Object Structure (JavaScript):**
            ```javascript
            {
                id: String, // Unique ID (e.g., 'var_167...')
                name: String, // User-defined variable name
                value: Any,   // Current value of the variable (Number, String, Boolean)
                isMonitored: Boolean, // True if stage monitor is visible
                // Scope is implicit: global if in projectGlobalVariables, local if in sprite.variables
            }
            ```
    *   **Server-Side (`SimpleHttpServer.java` & `Sprite.java`):**
        *   `SimpleHttpServer.projectGlobalVariables`: `Map<String, Object>` for global variables (name -> value).
        *   `Sprite.localVariables`: `Map<String, Object>` for local variables within each `Sprite` instance (name -> value).
        *   Note: The `isMonitored` flag is purely a client-side concern for UI display and is not stored or used by the server. The `id` is also primarily for client-side management (e.g., linking checkbox to variable object, key for React-like rendering). Server-side variables are identified by name and scope.


### 3. Execution of Block Types

The `ProgramOrchestrator` (currently part of `ExecuteProgramHandler` in `SimpleHttpServer.java`) uses the `type` field of each block from the input JSON array to determine how to execute it:

*   **Python Blocks (`PYTHON_BLOCK`):**
    *   Extracts Python code from `inputs.CODE`.
    *   Executed via `JythonExecutor.executeScript(pythonCode)`.
    *   `stdout`, `stderr`, and exceptions are captured and appended to the aggregated output.
    *   Future: The Jython environment will need access to the `ProgramContext` for the target sprite.

*   **Standard Blocks:**
    *   These are handled by specific Java logic in the backend.
    *   **`SAY_BLOCK`:**
        *   Extracts `inputs.TEXT`.
        *   Server appends a message like `[Output] SAY: <text>` to the aggregated output.
    *   **`GOTO_XY_BLOCK` (Motion):**
        *   Frontend: Optimistically updates the `activeSprite.x` and `activeSprite.y` values and calls `renderStage()` for immediate visual feedback.
        *   Backend: Extracts `inputs.X` and `inputs.Y`. Updates the server-side state of the (currently hardcoded) target sprite (e.g., "sprite1"). Appends a confirmation message to aggregated output.
    *   **`SWITCH_COSTUME_BLOCK` (Looks):**
        *   Frontend: Optimistically updates `activeSprite.currentCostumeId` and calls `renderStage()`.
        *   Backend: Extracts `inputs.COSTUME_ID`. Validates if the costume ID exists for the (currently hardcoded) target sprite. If yes, updates the sprite's `currentCostumeId` on the server. Appends a confirmation or error message to aggregated output.
    *   **`SET_VARIABLE_BLOCK`:**
        *   **JSON Structure:** `{ type: 'SET_VARIABLE_BLOCK', inputs: { VARIABLE_ID: 'var_id_...', VARIABLE_NAME: 'varName', VARIABLE_SCOPE: 'global'/'local', VALUE: 'someValue' / { reporterType: 'VARIABLE', ... } } }`
        *   Frontend: Optimistically updates the variable's value in `projectGlobalVariables` or the active sprite's `variables` array. Calls `renderVariablePalette()` and `renderStageMonitors()`.
        *   Backend: `ExecuteProgramHandler` uses `resolveInputValue` to determine the actual `VALUE` (which could be a literal or resolved from another variable reporter). Updates `projectGlobalVariables` (Map<String, Object>) or the target sprite's `localVariables` map.
    *   **`CHANGE_VARIABLE_BLOCK`:**
        *   **JSON Structure:** Similar to `SET_VARIABLE_BLOCK`.
        *   Frontend: Similar optimistic updates, ensuring numeric conversion for the change operation.
        *   Backend: `ExecuteProgramHandler` resolves `VALUE`. Retrieves current variable value (defaults to 0 if non-numeric or non-existent), adds the numeric `VALUE`, and updates the map.
    *   This mapping to Java methods or specific logic paths *is* the "compilation to Java" in this context.

*   **Control Flow Blocks (`LOOP_BLOCK` - Placeholder Execution):**
    *   Extracts `inputs.COUNT` (potentially using `resolveInputValue`).
    *   Currently, the backend only acknowledges the loop and its count in the aggregated output (e.g., "Loop X times (execution of children not yet implemented)").
    *   Future: The Java handler for a loop block would need to recursively process its `children` array of blocks. This involves:
        *   Evaluating `inputs.COUNT`.
        *   Iterating that many times.
        *   In each iteration, recursively calling the main block processing logic for the `children` array.

*   **Control Flow Blocks (General Future - e.g., `IF_BLOCK`):**
    *   These would require more complex logic in their Java handler methods.
    *   For example, a `handleRepeatBlock(inputs, children, programContext)` method would:
        *   Evaluate the `inputs.TIMES` expression.
        *   Loop that many times.
        *   In each iteration, it would recursively call the main `ProgramOrchestrator`'s execution loop for the blocks listed in the `children` array of the repeat block.
    *   Conditional blocks would evaluate a condition from their inputs and then selectively execute a list of child blocks.

### 4. State Management

A crucial component for making the blocks interact and for the program to have memory is a `ProgramContext` (or `ExecutionContext`) object, especially for multi-sprite scenarios.

*   **Shared State:** This Java object would be instantiated at the beginning of a program's execution (or per sprite script execution).
*   **Target Sprite Context:** For true multi-sprite support, the `ProgramContext` on the backend (when processing a script) will need to include the `spriteId` of the sprite whose script is currently executing. Block handlers like "Go to X,Y" or "Switch Costume" would then operate on this target sprite rather than a hardcoded one. The `projectSprites` map on the server holds the state for all sprites.
*   **Passed to Blocks:** The context (or relevant parts of it, like the target sprite object) would be passed as an argument to each Java method handling a standard block, and also made available to Jython scripts.
*   **Contents:**
    *   **Variables:** A map or similar structure to store user-defined variables (potentially per-sprite or global).
    *   **Output Stream/Buffer:** A way to accumulate output from blocks like "Say" or `print()` statements in Python. This buffer forms part of the final result sent to the client.
    *   **Sprite/Stage State:** The `projectSprites` map effectively holds the state for all sprites (position, current costume, etc.). Standard blocks modify this state directly for the target sprite.
    *   **Global Properties:** Potentially other global program properties.
*   **Jython Interaction:**
    *   The `ProgramContext` Java object (or the specific target `Sprite` object) can be exposed to the Jython environment using `PythonInterpreter.set("context", javaContextObject)` or `interpreter.set("sprite", currentSpriteObject)`.
    *   Python code can then interact with this Java object's public methods:
        ```python
        # Example Python code in a block:
        # print(f"Sprite X: {sprite.getX()}")
        # sprite.setX(sprite.getX() + 10)
        # context.setGlobalVariable('score', 100)
        ```
    *   **Variable Reporter as Input:** If a block input (e.g., `VALUE` for `SET_VARIABLE_BLOCK`) is a variable reporter, the frontend structures it in the JSON as an object:
        ```json
        "inputs": {
            "VALUE": { "reporterType": "VARIABLE", "id": "var_id_...", "name": "sourceVarName", "scope": "global" }
        }
        ```
        The backend's `ExecuteProgramHandler.resolveInputValue(inputs, "VALUE", ...)` method detects this structure, retrieves the named variable's current value from the appropriate server-side map (`projectGlobalVariables` or a sprite's `localVariables`), and returns it for use by the block's logic.

### 5. Sandboxing Considerations

#### Introduction to Risks

Executing user-provided code, whether Python via Jython or dynamically generated/loaded Java code (a more distant future possibility), inherently introduces security risks to the server environment. These risks include, but are not limited to:

*   **Arbitrary File System Access:** Reading sensitive files (e.g., server configuration, other users' data) or writing/deleting files, potentially corrupting the system or other applications.
*   **Network Connections:** Initiating outbound network connections to arbitrary hosts, which could be used for data exfiltration, participating in DDoS attacks, or probing internal networks.
*   **Excessive Resource Consumption:** Code designed to consume excessive CPU time (e.g., infinite loops, computationally intensive tasks) or memory, leading to denial of service for other users or the server itself.
*   **Access to Sensitive Server Internals:** Interacting with or manipulating server components, application data, or the underlying Java Virtual Machine in unintended ways.
*   **Execution of Arbitrary Commands:** If the execution environment allows calling out to shell commands.

#### Jython Sandboxing Strategies

Mitigating these risks when using Jython requires careful configuration and potentially leveraging both Java and Jython-specific mechanisms.

*   **Java Permissions Model:**
    *   The most robust approach within Java is to use its security model. This involves running the `PythonInterpreter` instance under a restricted `AccessControlContext` associated with a `ProtectionDomain` that has a limited set of `java.security.Permission`s.
    *   A custom `Policy` file can define these permissions, granting only what is absolutely necessary (e.g., no `FilePermission` for arbitrary paths, no `SocketPermission` for arbitrary hosts/ports).
    *   This is a powerful but complex mechanism to set up correctly. The granularity of permissions can be very fine (e.g., read-only access to specific directories).

*   **Jython's Internal Mechanisms:**
    *   **`org.python.core.Options.importSite`:** Setting this to `false` (`PythonInterpreter.getSystemState().setOption("importSite", false)`) can prevent the `site` module from being imported, which can reduce the number of available built-in modules and site-specific customizations that might offer ways to escape a sandbox.
    *   **Custom `PySystemState`:** A `PythonInterpreter` can be initialized with a custom `PySystemState`. This object could be configured with:
        *   Limited built-in modules: Only exposing a "safe" subset of Python's standard library.
        *   Restricted import paths (`sys.path`): Limiting where Jython can look for modules.
    *   **Overriding Builtins:** It's theoretically possible to replace or wrap built-in Python functions (like `open()`) with safer Java implementations that perform checks before delegating.

*   **Restricting Java Class Access:**
    *   A key risk with Jython is its ability to import and use arbitrary Java classes present in the server's classpath.
    *   **`org.python.core.Options.respectJavaAccessibility`:** Setting this to `false` (default is `true`) means Jython will *not* respect Java's `public`, `protected`, `private` modifiers, potentially allowing access to non-public members of classes. Keeping it `true` is generally safer.
    *   **Package/Class Import Filtering:** One might try to restrict access to specific Java packages or classes. Jython's `PySystemState` allows providing a custom `ImportDirector` which could potentially filter imports.
    *   **Custom ClassLoader:** Running the interpreter with a `ClassLoader` that restricts visibility to only "safe" Java classes is a more advanced technique.
    *   **Bytecode Manipulation/Analysis:** Tools like ASM could theoretically inspect generated Jython bytecode before execution to disallow certain operations, but this is highly complex.

*   **Resource Limits (CPU/Memory/Time):**
    *   Enforcing strict resource limits (especially CPU time and heap memory) on a per-script basis from within Java/Jython is notoriously difficult.
    *   **Execution Time:** A separate watchdog thread in Java can monitor the execution time of `interpreter.exec()` and attempt to interrupt it (e.g., via `Thread.interrupt()` or by trying to call a Jython-specific stop/interrupt method if one exists, though direct interruption of Jython execution is not straightforward). This often relies on the Jython script being cooperative or checking for interruption signals.
    *   **Memory:** Limiting heap usage for a specific script within a shared JVM is very hard. OS-level containerization (e.g., Docker) or running scripts in separate processes (as the previous CPython implementation did) are more effective for memory isolation.
    *   These are challenging areas, and robust solutions often involve OS-level controls or specialized JVMs/agents, which are likely beyond the scope of this project for now.

#### Java Sandboxing Strategies (for future "compiled" or dynamically loaded standard blocks)

If, in the distant future, "standard blocks" were to involve dynamically loading or generating Java code beyond the current "safe mapping" model:

*   **Current "Safe Mapping" Model:** It's important to reiterate that the current design for standard blocks—where each block type maps to a predefined, trusted Java method in our own codebase—is inherently the safest approach. The "sandbox" is the careful, security-conscious design of these Java methods, ensuring they only perform expected actions and sanitize inputs.

*   **Dynamic Java Code (Advanced Future Scenario - Generally Not Recommended Without Extreme Caution):**
    *   **`SecurityManager`:** While historically used for this, `SecurityManager` has been deprecated for removal in Java (since Java 17). It is no longer a viable long-term solution for new development.
    *   **Custom `ClassLoader`s and `ProtectionDomain`s:** This is the more modern, albeit complex, approach. Untrusted Java code (e.g., compiled from a user's block definition or loaded from a plugin) would be loaded by a custom `ClassLoader` into a specific `ProtectionDomain` associated with a restricted set_of `Permission`s. This is how Java EE application servers or plugin systems often manage isolation.
    *   **Java Platform Module System (JPMS):** JPMS is about strong encapsulation and reliable configuration of modules, defining what parts of a module are accessible to others. While it can restrict access to internal APIs, it's not primarily designed as a sandbox for executing untrusted, arbitrary code *within* a module with fine-grained permissions like file or network access. It's more about modularity of trusted code.
    *   **Third-Party Sandboxing Libraries:** If dynamic Java code execution were ever seriously considered, evaluating dedicated Java sandboxing libraries (e.g., `java-sandbox`, `jailed-java`, or others that might emerge) would be necessary. Their maturity, maintenance status, and suitability for the specific use case would require thorough investigation.

#### Conclusion

Robustly sandboxing user-provided code is a complex and critical security challenge. The considerations above outline several potential avenues, each with its own trade-offs in terms of complexity and effectiveness.

For the current stage of this project:
*   The "safe mapping" model for standard blocks (like "Say") is secure as long as the Java methods themselves are implemented carefully.
*   For Jython execution, initial steps might involve disabling `importSite`, carefully managing the `PySystemState` (if feasible to restrict modules/builtins), and ensuring `respectJavaAccessibility` is not loosened. Applying Java's `SecurityManager` or custom `AccessControlContext` with a `Policy` would be a more significant step towards stronger sandboxing but requires substantial effort.
*   Resource limiting (CPU/time/memory) remains a difficult problem without OS-level intervention or more complex architectural changes (like per-user isolated execution environments).

Implementing a truly secure and comprehensive sandbox is a major undertaking that would require dedicated research, development, and rigorous testing, likely beyond initial project goals but essential for any public-facing deployment.

### 6. Output Handling

The primary way users see results is through the "Stage" area.

*   **Accumulated Output:** The `ProgramContext` would likely have a `StringBuilder` or similar to accumulate all textual output generated by "Say" blocks or `print()` statements (from Jython).
*   **Final Result:** At the end of program execution (or if an error occurs), the content of this output buffer, along with any error messages, would be formatted and sent back as the HTTP response to the client.
*   **Structured Output (Future):** For more complex interactions (e.g., sprite changes, graphical output), the server might send back a more structured JSON response that the frontend can interpret to update the stage visually, rather than just plain text. For now, plain text is the focus.

This conceptual framework aims to provide a flexible way to execute a mix of predefined "standard" blocks (as Java methods) and custom Python code blocks (via Jython), all interacting through a shared context.

## Frontend Architecture Notes

### Client-Side Asset Handling (Costumes & Sounds)

Currently, costume (image) and sound (audio) assets are managed entirely on the client-side and associated with the active sprite:

*   **File Selection:** Users select local files using the `<input type="file">` element within the "Costumes" or "Sounds" tab.
*   **`FileReader` API:** The JavaScript `FileReader` API reads the selected file's content as a Data URL.
*   **In-Memory Storage (per Sprite):**
    *   The generated Data URLs, along with metadata (filename, generated ID), are stored in arrays (`costumes` or `sounds`) within the respective client-side sprite object in the `projectSprites` array.
*   **Display/Playback:**
    *   **Costumes:** Image thumbnails are displayed by creating `<img>` elements with their `src` set to the Data URL. Clicking a thumbnail updates the active sprite's `currentCostumeId` and triggers a re-render of the stage.
    *   **Sounds:** Sounds are listed with their names. A "Play" button for each sound uses the Data URL to create an `Audio` object and play it.
*   **No Server Interaction for Assets:** There is no server-side upload, storage, or processing of these asset files. They exist only in the browser's memory for the current session and are lost on page refresh.

This approach is simple for initial development but has limitations (no persistence, assets not tied to saved projects, potential memory issues with very large files/many assets). Future enhancements would involve server-side asset management.

### Stage Rendering (Client-Side)

The visual stage is rendered on the client-side by the `renderStage()` JavaScript function:

*   **Clearing Stage:** It first clears any existing sprite visuals from the `#stage-area` div.
*   **Iterating Sprites:** It loops through the `projectSprites` array.
*   **Sprite Visual Div:** For each sprite, a `div` with class `.sprite-visual` is created.
*   **Costume Display:**
    *   It finds the sprite's `currentCostume` based on `currentCostumeId` from its `costumes` array. If not found or invalid, it defaults to the first available costume.
    *   If the costume has a `dataURL`, an `<img>` element is created, its `src` is set, and it's appended to the sprite's div.
    *   If no valid costume/dataURL, a placeholder colored div is rendered for the sprite.
*   **Positioning:**
    *   The sprite's `(x, y)` coordinates (where (0,0) is stage center, Y positive is up) are converted to CSS `top` and `left` properties.
    *   This conversion accounts for the stage dimensions and the size of the sprite's visual (currently determined by `img.onload` to get image dimensions, or default for placeholder). The CSS positions the *center* of the sprite visual at the calculated `(left, top)` coordinates.
    *   `#stage-area` has `position: relative; overflow: hidden;` to act as the container for absolutely positioned sprites.
*   **Optimistic Updates:** Functions like `setActiveSprite`, changes to sprite properties via blocks (`GOTO_XY_BLOCK`, `SWITCH_COSTUME_BLOCK`, `SET_VARIABLE_BLOCK`, `CHANGE_VARIABLE_BLOCK` in their client-side logic), and costume selection in the "Costumes" tab trigger `renderStage()` and/or `renderStageMonitors()` to provide immediate visual feedback.

### Stage Variable Monitors (Client-Side)

*   **Data Model:** The `isMonitored` boolean flag is added to each variable object in the client-side `projectGlobalVariables` and `sprite.variables` arrays.
*   **Palette Checkboxes:** In `renderVariablePalette()`, each variable reporter is now created alongside a checkbox. The checkbox's state reflects `isMonitored`. Changing the checkbox updates the variable's `isMonitored` flag and calls `renderStageMonitors()`.
*   **`renderStageMonitors()` Function:**
    *   This function clears the `#stage-variable-monitors-container` div.
    *   It iterates through `projectGlobalVariables` and the active sprite's `variables`.
    *   For each variable where `isMonitored` is true, it creates a `div.variable-monitor` element.
    *   The monitor div is styled with CSS and displays the variable's name and current value.
    *   Monitors are appended to `#stage-variable-monitors-container`.
*   **Optimistic Value Updates:** When variable values are changed by "Set Variable" or "Change Variable" blocks, the client-side optimistic update logic within the `runProgramButton` listener calls `renderStageMonitors()` after updating the variable's value in the JavaScript model. This ensures that visible stage monitors reflect the new value immediately.

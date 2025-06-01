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

### 3. Execution of Block Types

The `ProgramOrchestrator` would use the `type` field of each block to determine how to execute it:

*   **Python Blocks (`PYTHON_BLOCK`):**
    *   The Python code provided in `inputs.CODE` would be extracted.
    *   This code would be passed to the `JythonExecutor.executeScript(pythonCode)` method.
    *   The `stdout`, `stderr`, and any exceptions from Jython execution would be captured.
    *   The Jython environment would be provided access to the `ProgramContext` (see State Management).

*   **Standard Blocks (e.g., `SAY_BLOCK`, `MOVE_SPRITE_BLOCK`):**
    *   These represent predefined actions within the application that are implemented directly in Java.
    *   Each standard block `type` (e.g., "SAY_BLOCK") would map to a specific Java method within the backend (e.g., in the `ProgramOrchestrator` or a dedicated `StandardBlockHandlers` class). For example, `handleSayBlock(inputs, programContext)`.
    *   The `inputs` object for the block would be passed to this Java method.
    *   The Java method would perform the action (e.g., append a message to an output buffer in the `ProgramContext`, modify sprite state in the context).
    *   This direct mapping and execution of Java methods based on block type is how these blocks are "compiled to Java" in this server-side execution model (it's an interpretation rather than bytecode generation).

*   **Control Flow Blocks (Future - e.g., `REPEAT_BLOCK`, `IF_BLOCK`):**
    *   These blocks would require more complex logic in their corresponding Java handler methods.
    *   For example, a `handleRepeatBlock(inputs, children, programContext)` method would:
        *   Evaluate the `inputs.TIMES` expression.
        *   Loop that many times.
        *   In each iteration, it would recursively call the main `ProgramOrchestrator`'s execution loop for the blocks listed in the `children` array of the repeat block.
    *   Conditional blocks would evaluate a condition from their inputs and then selectively execute a list of child blocks.

### 4. State Management

A crucial component for making the blocks interact and for the program to have memory is a `ProgramContext` (or `ExecutionContext`) object.

*   **Shared State:** This Java object would be instantiated at the beginning of a program's execution.
*   **Passed to Blocks:** It would be passed as an argument to each Java method handling a standard block, and also made available to Jython scripts.
*   **Contents:**
    *   **Variables:** A map or similar structure to store user-defined variables created by blocks (e.g., a "Set Variable" block or variables created in a Python script).
    *   **Output Stream/Buffer:** A way to accumulate output from blocks like "Say" or `print()` statements in Python. This buffer would form part of the final result sent to the client.
    *   **Sprite/Stage State (Future):** For a true Scratch-like environment, this context would hold information about sprites (position, costume, visibility, etc.) and the stage. Standard blocks would interact with this state.
    *   **Global Properties:** Potentially other global program properties.
*   **Jython Interaction:**
    *   The `ProgramContext` Java object can be exposed to the Jython environment using `PythonInterpreter.set("context", javaContextObject)`.
    *   Python code can then interact with this Java object's public methods to get/set variables or trigger other Java-defined actions:
        ```python
        # Example Python code in a block:
        print(f"Value from Java context: {context.getVariable('some_var')}")
        context.setVariable('new_py_var', 42)
        context.triggerSomeJavaAction('hello from python')
        ```

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

Currently, costume (image) and sound (audio) assets are handled entirely on the client-side:

*   **File Selection:** Users select local files using the `<input type="file">` element.
*   **`FileReader` API:** The JavaScript `FileReader` API is used to read the selected file's content.
    *   For images (costumes), files are read as Data URLs (`reader.readAsDataURL(file)`).
    *   For audio (sounds), files are also read as Data URLs.
*   **Local Storage (In-Memory):**
    *   The generated Data URLs, along with metadata like filename and a generated ID, are stored in JavaScript arrays within the `app.js` scope (e.g., `currentSpriteCostumes`, `currentSpriteSounds`).
*   **Display/Playback:**
    *   **Costumes:** Image thumbnails are displayed by creating `<img>` elements and setting their `src` attribute to the Data URL.
    *   **Sounds:** Sounds are played by creating an `Audio` object (`new Audio(dataURL)`) and calling its `play()` method.
*   **No Server Interaction:** There is no server-side upload, storage, or processing of these assets. They exist only in the browser's memory for the current session. Refreshing the page will clear any uploaded assets.

This approach is simple for initial development but has limitations (no persistence, assets not tied to saved projects, potential memory issues with very large files/many assets). Future enhancements would involve server-side asset management.

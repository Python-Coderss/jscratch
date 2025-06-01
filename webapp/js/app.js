document.addEventListener('DOMContentLoaded', () => {
    // Element References
    const blockPalette = document.getElementById('block-palette');
    // Using the more specific div for dropping/showing assembled blocks
    const scriptAssemblyDisplay = document.getElementById('assembled-script-display');
    const stageArea = document.getElementById('stage-area');
    const runProgramButton = document.getElementById('runProgramButton');
    const outputDisplay = document.getElementById('output-display');

    let currentProgram = null; // Holds the current program structure {type: 'TYPE', ...data}

    // --- "Python Code Block" in Palette ---
    const pythonBlockInPalette = document.createElement('div');
    pythonBlockInPalette.textContent = 'Python Code Block';
    pythonBlockInPalette.id = 'pythonBlockInPalette'; // Keep ID for potential specific targeting
    pythonBlockInPalette.classList.add('palette-block', 'palette-python-block'); // Generic and specific class
    if (blockPalette) {
        blockPalette.appendChild(pythonBlockInPalette);
    } else {
        console.error("Block palette element not found for Python block!");
    }

    // --- "Say Block" in Palette ---
    const sayBlockInPalette = document.createElement('div');
    sayBlockInPalette.textContent = 'Say Block';
    sayBlockInPalette.id = 'sayBlockInPalette'; // Keep ID for potential specific targeting
    sayBlockInPalette.classList.add('palette-block', 'palette-say-block'); // Generic and specific class
    if (blockPalette) {
        blockPalette.appendChild(sayBlockInPalette);
    } else {
        console.error("Block palette element not found for Say block!");
    }


    // --- Modal for Python Code Input ---
    function openPythonCodeModal(existingCode = '') {
        // Check if modal already exists
        if (document.getElementById('pythonCodeModalOverlay')) {
            return;
        }

        // Modal Overlay
        const modalOverlay = document.createElement('div');
        modalOverlay.id = 'pythonCodeModalOverlay';
        modalOverlay.classList.add('modal-overlay');

        // Modal Content
        const modalContent = document.createElement('div');
        modalContent.classList.add('modal-content');

        const title = document.createElement('h3');
        title.textContent = 'Edit Python Code';
        modalContent.appendChild(title);

        const textarea = document.createElement('textarea');
        textarea.id = 'pythonCodeTextarea';
        textarea.value = existingCode;
        modalContent.appendChild(textarea);

        const saveButton = document.createElement('button');
        saveButton.id = 'savePythonCodeButton';
        saveButton.textContent = 'Save Python';
        saveButton.classList.add('modal-button', 'save');

        const cancelButton = document.createElement('button');
        cancelButton.id = 'cancelPythonCodeButton';
        cancelButton.textContent = 'Cancel';
        cancelButton.classList.add('modal-button', 'cancel');

        const buttonContainer = document.createElement('div');
        buttonContainer.classList.add('modal-button-container');
        buttonContainer.appendChild(saveButton);
        buttonContainer.appendChild(cancelButton);
        modalContent.appendChild(buttonContainer);

        modalOverlay.appendChild(modalContent);
        document.body.appendChild(modalOverlay);

        // Event Listeners for Modal Buttons
        saveButton.addEventListener('click', () => {
            const code = textarea.value;
            currentProgram = { type: 'PYTHON_BLOCK', code: code };
            if (scriptAssemblyDisplay) {
                scriptAssemblyDisplay.innerHTML = `<div class="assembled-block assembled-python-block">Python Block (Code Saved)<pre>${escapeHtml(code)}</pre></div>`;
            }
            closeModal();
            console.log("Python program saved:", currentProgram);
        });

        cancelButton.addEventListener('click', () => {
            closeModal();
        });

        // Close modal if overlay is clicked
        modalOverlay.addEventListener('click', (event) => {
            if (event.target === modalOverlay) {
                closeModal();
            }
        });
    }

    function closeModal() {
        const modalOverlay = document.getElementById('pythonCodeModalOverlay');
        if (modalOverlay) {
            document.body.removeChild(modalOverlay);
        }
    }

    function escapeHtml(unsafe) {
        if (unsafe === null || unsafe === undefined) {
            return "";
        }
        return unsafe
             .replace(/&/g, "&amp;")
             .replace(/</g, "&lt;")
             .replace(/>/g, "&gt;")
             .replace(/"/g, "&quot;")
             .replace(/'/g, "&#039;");
     }

    // --- Event Listener for Palette's Python Block ---
    if (pythonBlockInPalette) {
        pythonBlockInPalette.addEventListener('click', () => {
            const currentCode = (currentProgram && currentProgram.type === 'PYTHON_BLOCK') ? currentProgram.code : '';
            openPythonCodeModal(currentCode);
        });
    }

    // --- Event Listener for Palette's Say Block ---
    if (sayBlockInPalette) {
        sayBlockInPalette.addEventListener('click', () => {
            const currentText = (currentProgram && currentProgram.type === 'SAY_BLOCK') ? currentProgram.text : 'Hello!';
            const textToSay = prompt("Enter text for the Say block:", currentText);
            if (textToSay !== null) { // User didn't click cancel
                currentProgram = { type: 'SAY_BLOCK', text: textToSay };
                if (scriptAssemblyDisplay) {
                    scriptAssemblyDisplay.innerHTML = `<div class="assembled-block assembled-say-block">Say: "${escapeHtml(textToSay)}"</div>`;
                }
                console.log("Say program saved:", currentProgram);
            }
        });
    }


    // --- "Run Program" Button Listener ---
    if (runProgramButton) {
        runProgramButton.addEventListener('click', () => {
            console.log("Run Program button clicked.");

            if (!outputDisplay) {
                console.error("Output display element not found!");
                alert("Critical error: Output display area is missing.");
                return;
            }

            if (!currentProgram) {
                console.log("No program to run.");
                outputDisplay.textContent = "No program to run. Add a block from the palette.";
                 if (scriptAssemblyDisplay) {
                    scriptAssemblyDisplay.innerHTML = '<p><em>No program assembled. Add a block.</em></p>';
                }
                return;
            }

            outputDisplay.textContent = 'Executing...';
            let payload = {};
            if (currentProgram.type === 'PYTHON_BLOCK') {
                payload = { type: 'PYTHON_BLOCK', inputs: { CODE: currentProgram.code } };
                console.log("Sending Python Program to server:", payload);
            } else if (currentProgram.type === 'SAY_BLOCK') {
                payload = { type: 'SAY_BLOCK', inputs: { TEXT: currentProgram.text } };
                console.log("Sending Say Program to server:", payload);
            } else {
                outputDisplay.textContent = 'Unknown program type.';
                console.error("Unknown program type in currentProgram:", currentProgram);
                return;
            }

            fetch('/api/execute_program', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8' // Changed to application/json
                },
                body: JSON.stringify(payload) // Send JSON string
            })
            .then(response => {
                return response.text().then(text => { // Get text content regardless of ok status for more info
                    if (!response.ok) {
                        console.error('Server Error Response:', text);
                        throw new Error(`Server error: ${response.status} ${response.statusText}. \n${text}`);
                    }
                    return text;
                });
            })
            .then(data => {
                outputDisplay.textContent = data;
                console.log("Server Response:", data);
            })
            .catch(error => {
                console.error('Execution Or Network Error:', error);
                outputDisplay.textContent = 'Error during execution: \n' + error.message;
            });
        });
    } else {
        console.error("Run Program button element not found!");
    }

    // Initial state for script assembly area
    if (scriptAssemblyDisplay) {
        scriptAssemblyDisplay.innerHTML = '<p><em>Drag blocks here or click palette items to add.</em></p>';
    }

    console.log("JavaScript app.js loaded and initialized.");
});

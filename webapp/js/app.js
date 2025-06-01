document.addEventListener('DOMContentLoaded', () => {
    // Element References
    const blockPalette = document.getElementById('block-palette');
    // Using the more specific div for dropping/showing assembled blocks
    const scriptAssemblyDisplay = document.getElementById('assembled-script-display');
    const stageArea = document.getElementById('stage-area');
    const stageVariableMonitorsContainer = document.getElementById('stage-variable-monitors-container'); // New DOM ref
    const runProgramButton = document.getElementById('runProgramButton');
    const outputDisplay = document.getElementById('output-display');
    const spriteListUL = document.getElementById('sprite-list'); // New reference

    // Global state for sprites and active elements
    let projectSprites = [];
    let activeSpriteId = null;
    let currentProgram = []; // This will now reference the active sprite's scripts
    let projectGlobalVariables = []; // Array of {id, name, value}

    // DOM Element References for Variable UI (ensure these are available after HTML update)
    const makeVariableButton = document.getElementById('makeVariableButton');
    const variableModal = document.getElementById('variableModal');
    const variableNameInput = document.getElementById('variableNameInput');
    const confirmVariableButton = document.getElementById('confirmVariableButton');
    const cancelVariableButton = document.getElementById('cancelVariableButton');
    const variablePaletteBlocksContainer = document.getElementById('variable-palette-blocks');
    // const scopeGlobalRadio = document.getElementById('scopeGlobal'); // Can get by name
    // const scopeLocalRadio = document.getElementById('scopeLocal');


    // --- Tab Switching Logic ---
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabPanels = document.querySelectorAll('.tab-panel');

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const targetPanelId = button.getAttribute('data-tab');

            tabButtons.forEach(btn => {
                btn.classList.remove('active');
            });
            button.classList.add('active');

            tabPanels.forEach(panel => {
                if (panel.id === targetPanelId) {
                    panel.classList.add('active');
                    panel.style.display = 'block';
                    if (panel.id === 'scripts-panel') {
                        const container = panel.querySelector('.container');
                        if (container) container.style.display = 'flex';
                    }
                } else {
                    panel.classList.remove('active');
                    panel.style.display = 'none';
                    if (panel.id === 'scripts-panel') { // Ensure non-active scripts-panel's container is hidden
                        const container = panel.querySelector('.container');
                        if (container) container.style.display = 'none';
                    }
                }
            });
        });
    });

    // Ensure correct initial display based on 'active' class set in HTML
    tabPanels.forEach(panel => {
        if (!panel.classList.contains('active')) {
            panel.style.display = 'none';
            if (panel.id === 'scripts-panel') { // Ensure non-active scripts-panel's container is hidden
                const container = panel.querySelector('.container');
                if (container) container.style.display = 'none';
            }
        } else {
            panel.style.display = 'block'; // Ensure active panel is displayed
            if (panel.id === 'scripts-panel') {
                const container = panel.querySelector('.container');
                if (container) container.style.display = 'flex';
            }
        }
    });


    // --- Draggable Blocks in Palette & Associated Logic for SCRIPT PANEL ---
    // This whole section should only run if the scripts panel elements are present
    if (blockPalette && scriptAssemblyDisplay && runProgramButton && outputDisplay) {

        function makePaletteBlockDraggable(blockElement, blockType) {
            blockElement.setAttribute('draggable', 'true');
            blockElement.addEventListener('dragstart', (event) => {
                event.dataTransfer.setData('text/plain', blockType);
                event.dataTransfer.effectAllowed = 'copy';
                console.log(`Drag started for: ${blockType}`);
            });
        }

        // --- "Python Code Block" in Palette ---
        const pythonBlockInPalette = document.createElement('div');
        pythonBlockInPalette.textContent = 'Python Code Block';
        pythonBlockInPalette.classList.add('palette-block', 'palette-python-block');
        makePaletteBlockDraggable(pythonBlockInPalette, 'PYTHON_BLOCK');
        blockPalette.appendChild(pythonBlockInPalette);

        // --- "Say Block" in Palette ---
        const sayBlockInPalette = document.createElement('div');
        sayBlockInPalette.textContent = 'Say Block';
        sayBlockInPalette.classList.add('palette-block', 'palette-say-block');
        makePaletteBlockDraggable(sayBlockInPalette, 'SAY_BLOCK');
        blockPalette.appendChild(sayBlockInPalette);

        // --- "Loop Block" in Palette ---
        const loopBlockInPalette = document.createElement('div');
        loopBlockInPalette.textContent = 'Loop Block';
        loopBlockInPalette.classList.add('palette-block', 'palette-loop-block');
        makePaletteBlockDraggable(loopBlockInPalette, 'LOOP_BLOCK');
        blockPalette.appendChild(loopBlockInPalette);

        // --- "Go to X, Y" Block in Palette ---
        const gotoXYBlockInPalette = document.createElement('div');
        gotoXYBlockInPalette.className = 'palette-block palette-motion-block';
        gotoXYBlockInPalette.textContent = 'Go to X: Y:';
        makePaletteBlockDraggable(gotoXYBlockInPalette, 'GOTO_XY_BLOCK');
        blockPalette.appendChild(gotoXYBlockInPalette);

        // --- "Switch to costume" Block in Palette ---
        const switchCostumeBlockInPalette = document.createElement('div');
        switchCostumeBlockInPalette.className = 'palette-block palette-looks-block';
        switchCostumeBlockInPalette.textContent = 'Switch to costume:';
        makePaletteBlockDraggable(switchCostumeBlockInPalette, 'SWITCH_COSTUME_BLOCK');
        blockPalette.appendChild(switchCostumeBlockInPalette);

        // --- "Set Variable" Block in Palette (within #variables-category) ---
        const setVariableBlockInPalette = document.createElement('div');
        setVariableBlockInPalette.className = 'palette-block palette-variable-control-block';
        setVariableBlockInPalette.textContent = 'Set [variable] to [value]';
        makePaletteBlockDraggable(setVariableBlockInPalette, 'SET_VARIABLE_BLOCK');

        const varCategory = document.getElementById('variables-category'); // Assuming this is already in HTML
        if (varCategory) {
            const varPaletteBlocksDiv = document.getElementById('variable-palette-blocks');
            varCategory.insertBefore(setVariableBlockInPalette, varPaletteBlocksDiv);
        } else {
            blockPalette.appendChild(setVariableBlockInPalette);
        }

        // --- "Change Variable by" Block in Palette (within #variables-category) ---
        const changeVariableBlockInPalette = document.createElement('div');
        changeVariableBlockInPalette.className = 'palette-block palette-variable-control-block';
        changeVariableBlockInPalette.textContent = 'Change [variable] by [value]';
        makePaletteBlockDraggable(changeVariableBlockInPalette, 'CHANGE_VARIABLE_BLOCK');
        if (varCategory) {
            const varPaletteBlocksDiv = document.getElementById('variable-palette-blocks');
            // Insert after Set Variable Block or before reporters, depending on desired order
            varCategory.insertBefore(changeVariableBlockInPalette, varPaletteBlocksDiv);
        } else {
            blockPalette.appendChild(changeVariableBlockInPalette);
        }


        // --- Modal for Python Code Input (Modified to be a Promise) ---
    function openPythonCodeModal(existingCode = '') {
        return new Promise((resolve, reject) => {
            // Check if modal already exists
            if (document.getElementById('pythonCodeModalOverlay')) {
                reject(new Error("Modal already open.")); // Or focus existing modal
                return;
            }

            // Modal Overlay (rest of the modal creation code is similar)
            const modalOverlay = document.createElement('div');
            modalOverlay.id = 'pythonCodeModalOverlay';
            modalOverlay.classList.add('modal-overlay');

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

            const closeModalAndCleanup = (value) => {
                document.body.removeChild(modalOverlay);
                // Remove specific event listeners to avoid memory leaks if any were added to document/window
            };

            saveButton.addEventListener('click', () => {
                closeModalAndCleanup();
                resolve(textarea.value); // Resolve promise with the code
            });

            cancelButton.addEventListener('click', () => {
                closeModalAndCleanup();
                reject(new Error("Modal cancelled by user.")); // Reject promise
            });

            modalOverlay.addEventListener('click', (event) => {
                if (event.target === modalOverlay) {
                    closeModalAndCleanup();
                    reject(new Error("Modal cancelled by clicking overlay.")); // Reject promise
                }
            });
        });
    }

    // No global closeModal function needed anymore if tied to promise

    function escapeHtml(unsafe) {
        if (unsafe === null || unsafe === undefined) {
            return "";
        }
        // Corrected: Removed duplicated modal creation logic
        return unsafe
             .replace(/&/g, "&amp;")
             .replace(/</g, "&lt;")
             .replace(/>/g, "&gt;")
             .replace(/"/g, "&quot;")
             .replace(/'/g, "&#039;");
     }

    // --- Drop Zone Functionality ---
    if (scriptAssemblyDisplay) {
        scriptAssemblyDisplay.addEventListener('dragover', (event) => {
            event.preventDefault(); // Allow drop
            event.dataTransfer.dropEffect = 'copy';
            scriptAssemblyDisplay.classList.add('drag-over');
        });

        scriptAssemblyDisplay.addEventListener('dragenter', (event) => {
            event.preventDefault(); // Might be redundant if dragover handles it
            scriptAssemblyDisplay.classList.add('drag-over');
        });

        scriptAssemblyDisplay.addEventListener('dragleave', (event) => {
            // Be careful if dragging over child elements
            if (event.target === scriptAssemblyDisplay) {
                 scriptAssemblyDisplay.classList.remove('drag-over');
            }
        });

        scriptAssemblyDisplay.addEventListener('drop', (event) => {
            event.preventDefault();
            scriptAssemblyDisplay.classList.remove('drag-over');
            const blockType = event.dataTransfer.getData('text/plain');
            console.log(`Dropped blockType: ${blockType}`);
            addBlockToScript(blockType);
        });
    }

    // --- Add Block to Script Logic ---
    async function addBlockToScript(blockType) {
        let blockData = null;
        const blockId = 'block_' + Date.now() + '_' + Math.random().toString(36).substring(2, 7);

        if (blockType === 'PYTHON_BLOCK') {
            try {
                // For a new block, existingCode is empty. If editing, we'd pass current code.
                const code = await openPythonCodeModal('');
                blockData = { type: 'PYTHON_BLOCK', inputs: { CODE: code }, id: blockId };
            } catch (error) {
                console.log("Python block addition cancelled or modal error:", error.message);
                return; // Don't add if modal was cancelled
            }
        } else if (blockType === 'SAY_BLOCK') {
            const textToSay = prompt("Enter text for the Say block:", "Hello, world!");
            if (textToSay !== null) { // User didn't click cancel
                blockData = { type: 'SAY_BLOCK', inputs: { TEXT: textToSay }, id: blockId };
            } else {
                return; // Don't add if prompt was cancelled
            }
        } else if (blockType === 'LOOP_BLOCK') {
            const countInput = prompt("Enter loop count (e.g., 3):", "3");
            if (countInput !== null) { // User didn't cancel
                const count = parseInt(countInput);
                if (!isNaN(count) && count > 0) {
                    blockData = { type: 'LOOP_BLOCK', inputs: { COUNT: count }, id: blockId, children: [] }; // Initialize children
                } else {
                    alert("Invalid loop count. Please enter a positive number.");
                    return; // Don't add if input is invalid
                }
            } else {
                return; // Don't add if prompt was cancelled
            }
        } else if (blockType === 'GOTO_XY_BLOCK') {
            const xValStr = prompt('Enter X coordinate for Go To X,Y block:', '0');
            if (xValStr === null) return;
            const yValStr = prompt('Enter Y coordinate for Go To X,Y block:', '0');
            if (yValStr === null) return;

            const xVal = parseFloat(xValStr);
            const yVal = parseFloat(yValStr);

            if (isNaN(xVal) || isNaN(yVal)) {
                alert('Invalid coordinates. Please enter numbers.');
                return;
            } else {
                blockData = { type: 'GOTO_XY_BLOCK', inputs: { X: xVal, Y: yVal }, id: blockId };
            }
        } else if (blockType === 'SWITCH_COSTUME_BLOCK') {
            const activeSprite = getActiveSprite();
            if (!activeSprite || !activeSprite.costumes || activeSprite.costumes.length === 0) {
                alert('No costumes available for the active sprite. Add costumes in the "Costumes" tab.');
                return; // Do not add block if no costumes to choose from
            }
            let promptMsg = "Enter costume name or ID to switch to for sprite '" + activeSprite.name + "'. Available costumes:\n";
            activeSprite.costumes.forEach(c => {
                promptMsg += `- ${c.name} (ID: ${c.id})\n`;
            });
            const costumeIdentifier = prompt(promptMsg, activeSprite.costumes[0].name); // Default to first costume name

            if (costumeIdentifier) { // User entered something
                const foundCostume = activeSprite.costumes.find(c => c.id === costumeIdentifier || c.name.toLowerCase() === costumeIdentifier.toLowerCase());
                if (foundCostume) {
                    blockData = { type: 'SWITCH_COSTUME_BLOCK', inputs: { COSTUME_ID: foundCostume.id, COSTUME_NAME: foundCostume.name }, id: blockId };
                } else {
                    alert('Costume not found: ' + costumeIdentifier);
                    return; // Do not add block if costume not found
                }
            } else {
                return; // User cancelled prompt
            }
        } else if (blockType === 'SET_VARIABLE_BLOCK') {
            const activeSprite = getActiveSprite();
            let availableVars = projectGlobalVariables.map(v => ({ ...v, display: `Global: ${v.name}`, scope: 'global' }));
            if (activeSprite && activeSprite.variables) {
                activeSprite.variables.forEach(v => availableVars.push({ ...v, display: `Local (${activeSprite.name}): ${v.name}`, scope: 'local' }));
            }

            if (availableVars.length === 0) {
                alert("No variables created yet. Please 'Make a Variable' first.");
                return;
            }

            let promptMsg = "Select variable to set (type full display name or ID):\n";
            availableVars.forEach(v => { promptMsg += `- ${v.display} (ID: ${v.id})\n`; });
            const varIdentifier = prompt(promptMsg);
            if (!varIdentifier) return; // User cancelled

            // Prioritize matching ID, then display name, then just name (less specific)
            const chosenVar = availableVars.find(v => v.id === varIdentifier) ||
                              availableVars.find(v => v.display === varIdentifier) ||
                              availableVars.find(v => v.name === varIdentifier);


            if (!chosenVar) {
                alert("Variable not found. Please type the exact name or ID as listed.");
                return;
            }

            const valueToSet = prompt(`Enter value to set for "${chosenVar.name}":`, chosenVar.value !== undefined ? String(chosenVar.value) : '0');
            if (valueToSet === null) return; // User cancelled

            blockData = {
                type: 'SET_VARIABLE_BLOCK',
                inputs: {
                    VARIABLE_ID: chosenVar.id,
                    VARIABLE_NAME: chosenVar.name,
                    VARIABLE_SCOPE: chosenVar.scope,
                    VALUE: valueToSet
                },
                id: blockId
            };
        } else if (blockType === 'CHANGE_VARIABLE_BLOCK') {
            const activeSprite = getActiveSprite();
            let availableVars_Change = projectGlobalVariables.map(v => ({ ...v, display: `Global: ${v.name}`, scope: 'global' }));
            if (activeSprite && activeSprite.variables) {
                activeSprite.variables.forEach(v => availableVars_Change.push({ ...v, display: `Local (${activeSprite.name}): ${v.name}`, scope: 'local' }));
            }

            if (availableVars_Change.length === 0) {
                alert("No variables created yet. Please 'Make a Variable' first.");
                return;
            }

            let promptMsg_Change = "Select variable to change (type full display name or ID):\n";
            availableVars_Change.forEach(v => { promptMsg_Change += `- ${v.display} (ID: ${v.id})\n`; });
            const varIdentifier_Change = prompt(promptMsg_Change);
            if (!varIdentifier_Change) return;

            const chosenVar_Change = availableVars_Change.find(v => v.id === varIdentifier_Change || v.display === varIdentifier_Change || v.name === varIdentifier_Change);
            if (!chosenVar_Change) {
                alert("Variable not found. Please type the exact name or ID as listed.");
                return;
            }

            const valueToChangeBy = prompt(`Enter value to change "${chosenVar_Change.name}" by:`, '1');
            if (valueToChangeBy === null) return;

            blockData = {
                type: 'CHANGE_VARIABLE_BLOCK',
                inputs: {
                    VARIABLE_ID: chosenVar_Change.id,
                    VARIABLE_NAME: chosenVar_Change.name,
                    VARIABLE_SCOPE: chosenVar_Change.scope,
                    VALUE: valueToChangeBy
                },
                id: blockId
            };
        } else if (blockType === 'CHANGE_VARIABLE_BLOCK') {
            const activeSprite = getActiveSprite();
            let availableVars_Change = projectGlobalVariables.map(v => ({ ...v, display: `Global: ${v.name}`, scope: 'global' }));
            if (activeSprite && activeSprite.variables) {
                activeSprite.variables.forEach(v => availableVars_Change.push({ ...v, display: `Local (${activeSprite.name}): ${v.name}`, scope: 'local' }));
            }

            if (availableVars_Change.length === 0) {
                alert("No variables created yet. Please 'Make a Variable' first.");
                return;
            }

            let promptMsg_Change = "Select variable to change (type full display name or ID):\n";
            availableVars_Change.forEach(v => { promptMsg_Change += `- ${v.display} (ID: ${v.id})\n`; });
            const varIdentifier_Change = prompt(promptMsg_Change);
            if (!varIdentifier_Change) return;

            const chosenVar_Change = availableVars_Change.find(v => v.id === varIdentifier_Change || v.display === varIdentifier_Change || v.name === varIdentifier_Change);
            if (!chosenVar_Change) {
                alert("Variable not found. Please type the exact name or ID as listed.");
                return;
            }

            const valueToChangeBy = prompt(`Enter value to change "${chosenVar_Change.name}" by:`, '1');
            if (valueToChangeBy === null) return;

            blockData = {
                type: 'CHANGE_VARIABLE_BLOCK',
                inputs: {
                    VARIABLE_ID: chosenVar_Change.id,
                    VARIABLE_NAME: chosenVar_Change.name,
                    VARIABLE_SCOPE: chosenVar_Change.scope, // Will be 'global' or 'local' from chosenVar_Change
                    VALUE: valueToChangeBy
                },
                id: blockId
            };
        } else {
            console.error(`Unknown block type dropped: ${blockType}`);
            return;
        }

        if (blockData) {
            const activeSprite = getActiveSprite();
            if (!activeSprite) {
                alert("No active sprite to add blocks to!");
                return;
            }
            if (!activeSprite.scripts) { // Ensure scripts array exists
                activeSprite.scripts = [];
            }
            activeSprite.scripts.push(blockData); // Add to active sprite's scripts
            currentProgram = activeSprite.scripts; // Ensure currentProgram reference is up-to-date
            renderProgram();
            console.log("Current program for active sprite:", currentProgram);
        }
    }

    // --- Render Program in Assembly Area (uses global currentProgram which points to active sprite's scripts) ---
    function renderProgram() {
        if (!scriptAssemblyDisplay) return;
        scriptAssemblyDisplay.innerHTML = ''; // Clear previous content

        if (!currentProgram || currentProgram.length === 0) {
            scriptAssemblyDisplay.innerHTML = '<p><em>Drag blocks here to build your program for the active sprite.</em></p>';
            return;
        }

        currentProgram.forEach(block => { // currentProgram is activeSprite.scripts
            const blockDiv = document.createElement('div');
            blockDiv.classList.add('assembled-block');
            blockDiv.setAttribute('data-block-id', block.id);

            if (block.type === 'PYTHON_BLOCK') {
                blockDiv.classList.add('assembled-python-block');
                blockDiv.innerHTML = `Python Block (ID: ${block.id.substring(0,10)})<pre>${escapeHtml(block.inputs.CODE)}</pre>`;
            } else if (block.type === 'SAY_BLOCK') {
                blockDiv.classList.add('assembled-say-block');
                blockDiv.textContent = `Say: "${escapeHtml(block.inputs.TEXT)}" (ID: ${block.id.substring(0,10)})`;
            } else if (block.type === 'LOOP_BLOCK') {
                blockDiv.classList.add('assembled-loop-block');
                // Ensure COUNT is treated as a string for escapeHtml if it's a number
                blockDiv.innerHTML = `Loop ${escapeHtml(String(block.inputs.COUNT))} times (ID: ${block.id.substring(0,10)})
                                     <div class="nested-block-area" data-parent-block-id="${block.id}">
                                         ${renderNestedBlocks(block.children || [])}
                                     </div>`;
            } else if (block.type === 'GOTO_XY_BLOCK') {
                blockDiv.classList.add('assembled-motion-block');
                blockDiv.textContent = `Go to X: ${escapeHtml(String(block.inputs.X))} Y: ${escapeHtml(String(block.inputs.Y))} (ID: ${block.id.substring(0,10)})`;
            } else if (block.type === 'SWITCH_COSTUME_BLOCK') {
                blockDiv.classList.add('assembled-looks-block');
                blockDiv.textContent = `Switch to costume: ${escapeHtml(block.inputs.COSTUME_NAME || block.inputs.COSTUME_ID)} (ID: ${block.id.substring(0,10)})`;
            } else if (block.type === 'SET_VARIABLE_BLOCK') {
                blockDiv.classList.add('assembled-variable-control-block');
                blockDiv.textContent = `Set [${escapeHtml(block.inputs.VARIABLE_NAME)}] (${escapeHtml(block.inputs.VARIABLE_SCOPE)}) to [${escapeHtml(block.inputs.VALUE)}] (ID: ${block.id.substring(0,10)})`;
            } else if (block.type === 'CHANGE_VARIABLE_BLOCK') {
                blockDiv.classList.add('assembled-variable-control-block');
                // For VALUE, it might be a variable reporter object if we implement that drop later.
                // For now, it's a string from prompt.
                const valueDisplay = (typeof block.inputs.VALUE === 'object' && block.inputs.VALUE.reporterType === 'VARIABLE')
                                   ? escapeHtml(block.inputs.VALUE.name)
                                   : escapeHtml(String(block.inputs.VALUE));
                blockDiv.textContent = `Change [${escapeHtml(block.inputs.VARIABLE_NAME)}] (${escapeHtml(block.inputs.VARIABLE_SCOPE)}) by [${valueDisplay}] (ID: ${block.id.substring(0,10)})`;
            } else {
                blockDiv.textContent = `Unknown Block (ID: ${block.id.substring(0,10)})`;
            }
            scriptAssemblyDisplay.appendChild(blockDiv);
        });
    }

    function renderNestedBlocks(children) {
        if (!children || children.length === 0) return '<p class="empty-nested-placeholder">Drop blocks here</p>';
        // This is a simplified rendering for nested blocks.
        // In a full implementation, this would recursively call parts of renderProgram or a similar function.
        return children.map(child => {
            let content = '';
            if (child.type === 'PYTHON_BLOCK') {
                content = `Python Block (ID: ${child.id.substring(0,10)})<pre>${escapeHtml(child.inputs.CODE)}</pre>`;
            } else if (child.type === 'SAY_BLOCK') {
                content = `Say: "${escapeHtml(child.inputs.TEXT)}" (ID: ${child.id.substring(0,10)})`;
            } else if (child.type === 'GOTO_XY_BLOCK') {
                content = `Go to X: ${escapeHtml(String(child.inputs.X))} Y: ${escapeHtml(String(child.inputs.Y))} (ID: ${child.id.substring(0,10)})`;
            } else if (child.type === 'SWITCH_COSTUME_BLOCK') {
                content = `Switch to costume: ${escapeHtml(child.inputs.COSTUME_NAME || child.inputs.COSTUME_ID)} (ID: ${child.id.substring(0,10)})`;
            } else if (child.type === 'SET_VARIABLE_BLOCK') { // Added SET_VARIABLE_BLOCK to nested rendering
                content = `Set [${escapeHtml(child.inputs.VARIABLE_NAME)}] to [${escapeHtml(child.inputs.VALUE)}]`;
            } else if (child.type === 'CHANGE_VARIABLE_BLOCK') {
                const valueDisplayChild = (typeof child.inputs.VALUE === 'object' && child.inputs.VALUE.reporterType === 'VARIABLE')
                                   ? escapeHtml(child.inputs.VALUE.name)
                                   : escapeHtml(String(child.inputs.VALUE));
                content = `Change [${escapeHtml(child.inputs.VARIABLE_NAME)}] by [${valueDisplayChild}]`;
            } else {
                content = `Unknown Block (ID: ${child.id.substring(0,10)})`;
            }
            // Apply appropriate classes for styling nested blocks similarly to top-level ones
            const blockClasses = `assembled-block ${
                child.type === 'PYTHON_BLOCK' ? 'assembled-python-block' :
                child.type === 'SAY_BLOCK' ? 'assembled-say-block' :
                child.type === 'LOOP_BLOCK' ? 'assembled-loop-block' :
                child.type === 'GOTO_XY_BLOCK' ? 'assembled-motion-block' :
                child.type === 'SWITCH_COSTUME_BLOCK' ? 'assembled-looks-block' :
                child.type === 'SET_VARIABLE_BLOCK' ? 'assembled-variable-control-block' :
                child.type === 'CHANGE_VARIABLE_BLOCK' ? 'assembled-variable-control-block' : '' // Use same style
            }`;
            return `<div class="${blockClasses}" data-block-id="${child.id}">${content}</div>`;
        }).join('');
    }


    // Initial render in case there's a program loaded (not applicable now, but for future)
    renderProgram();


    // --- "Run Program" Button Listener (Modified to send array) ---
    if (runProgramButton) {
        runProgramButton.addEventListener('click', () => {
            console.log("Run Program button clicked.");

            if (!outputDisplay) {
                console.error("Output display element not found!");
                alert("Critical error: Output display area is missing.");
                return;
            }

            const activeSprite = getActiveSprite();
            if (!activeSprite || !currentProgram || currentProgram.length === 0) { // currentProgram is activeSprite.scripts
                console.log("No program to run for the active sprite.");
                outputDisplay.textContent = "No program to run. Drag blocks to the assembly area for the active sprite.";
                return;
            }

            // Client-side optimistic updates for visual feedback
            // This iterates through the *current* sprite's script before sending to backend
            currentProgram.forEach(block => {
                if (block.type === 'GOTO_XY_BLOCK') {
                    if (activeSprite) {
                        activeSprite.x = block.inputs.X;
                        activeSprite.y = block.inputs.Y;
                        console.log(`Optimistic update (GOTO_XY): Sprite ${activeSprite.name} to X:${activeSprite.x}, Y:${activeSprite.y}`);
                    }
                } else if (block.type === 'SWITCH_COSTUME_BLOCK') {
                    if (activeSprite) {
                        activeSprite.currentCostumeId = block.inputs.COSTUME_ID;
                        console.log(`Optimistic update (SWITCH_COSTUME): Sprite ${activeSprite.name} to costume ID:${activeSprite.currentCostumeId}`);
                    }
                } else if (block.type === 'SET_VARIABLE_BLOCK') {
                    let targetVarArray;
                    if (block.inputs.VARIABLE_SCOPE === 'global') {
                        targetVarArray = projectGlobalVariables;
                    } else if (activeSprite && activeSprite.id === getActiveSprite()?.id) {
                        targetVarArray = activeSprite.variables;
                    } else {
                        console.warn("Optimistic update: Target sprite for local variable not active or found.");
                        continue; // Skip this block's optimistic update
                    }

                    if (targetVarArray) {
                        const variable = targetVarArray.find(v => v.id === block.inputs.VARIABLE_ID);
                        if (variable) {
                            let parsedValue = block.inputs.VALUE;
                            if (parsedValue !== null && parsedValue !== "" && !isNaN(Number(parsedValue))) {
                                parsedValue = Number(parsedValue);
                            } else if (typeof parsedValue === 'string' && parsedValue.toLowerCase() === 'true') {
                                parsedValue = true;
                            } else if (typeof parsedValue === 'string' && parsedValue.toLowerCase() === 'false') {
                                parsedValue = false;
                            }
                            variable.value = parsedValue;
                            console.log(`Optimistic update: Variable ${variable.name} (${block.inputs.VARIABLE_SCOPE}) set to ${variable.value}`);
                            // Future: if (isVariableMonitorVisible(variable.id)) renderStageMonitors();
                            renderVariablePalette();
                        }
                    }
                } else if (block.type === 'CHANGE_VARIABLE_BLOCK') {
                    let targetVarArray_Change;
                    if (block.inputs.VARIABLE_SCOPE === 'global') {
                        targetVarArray_Change = projectGlobalVariables;
                    } else if (activeSprite && activeSprite.id === getActiveSprite()?.id) {
                        targetVarArray_Change = activeSprite.variables;
                    } else {
                        console.warn("Optimistic update: Target sprite for CHANGE_VARIABLE local var not active/found.");
                        continue;
                    }

                    if (targetVarArray_Change) {
                        const variable_Change = targetVarArray_Change.find(v => v.id === block.inputs.VARIABLE_ID);
                        if (variable_Change) {
                            const currentValue = parseFloat(variable_Change.value) || 0;

                            let changeByValue = block.inputs.VALUE; // This is a string from prompt initially
                            // If VALUE were a reporter object, this optimistic update would need to resolve it.
                            // For this step, we assume VALUE from prompt is a direct number or coercible to one.
                            if (typeof changeByValue === 'object' && changeByValue.reporterType === 'VARIABLE') {
                                 console.warn("Optimistic update for CHANGE_VARIABLE with reporter VALUE not fully implemented for direct value resolution. Treating as 0.");
                                 changeByValue = 0; // Placeholder until full client-side var resolution for inputs
                            } else {
                               changeByValue = parseFloat(changeByValue) || 0;
                            }

                            variable_Change.value = currentValue + changeByValue;
                            console.log(`Optimistic update: Variable ${variable_Change.name} (${block.inputs.VARIABLE_SCOPE}) changed by ${changeByValue} to ${variable_Change.value}`);
                            renderVariablePalette();
                        }
                    }
                } else if (block.type === 'CHANGE_VARIABLE_BLOCK') {
                    let targetVarArray_Change;
                    if (block.inputs.VARIABLE_SCOPE === 'global') {
                        targetVarArray_Change = projectGlobalVariables;
                    } else if (activeSprite && activeSprite.id === getActiveSprite()?.id) { // Ensure change is for the active sprite
                        targetVarArray_Change = activeSprite.variables;
                    } else {
                        console.warn("Optimistic update: Target sprite for CHANGE_VARIABLE local var not active/found.");
                        continue;
                    }

                    if (targetVarArray_Change) {
                        const variable_Change = targetVarArray_Change.find(v => v.id === block.inputs.VARIABLE_ID);
                        if (variable_Change) {
                            const currentValue = parseFloat(variable_Change.value) || 0;

                            let changeByInput = block.inputs.VALUE; // This is a string from prompt
                            let numValueToChangeBy = 0;

                            // Check if changeByInput is a variable reporter object (future enhancement for drag-drop)
                            // For now, the prompt gives a string, which could be a direct number or a variable name
                            // This optimistic update assumes direct numeric input or simple variable name resolution.
                            // A more robust solution would parse block.inputs.VALUE like the backend.
                            if (typeof changeByInput === 'object' && changeByInput !== null && changeByInput.reporterType === 'VARIABLE') {
                                let sourceVarValue;
                                const sourceVarIsGlobal = changeByInput.scope === 'global';
                                const sourceVarArray = sourceVarIsGlobal ? projectGlobalVariables : (activeSprite ? activeSprite.variables : []);

                                const sourceVar = sourceVarArray.find(v => v.id === changeByInput.id); // Assuming VALUE object has id, scope, name
                                if (sourceVar) {
                                    sourceVarValue = sourceVar.value;
                                } else {
                                     console.warn("Optimistic CHANGE_VARIABLE: source variable reporter not found", changeByInput);
                                     sourceVarValue = "0"; // Default to 0 if source var not found
                                }
                                numValueToChangeBy = parseFloat(sourceVarValue) || 0;
                            } else {
                               // If not a reporter object, treat as a direct numeric value (or NaN if not parseable)
                               numValueToChangeBy = parseFloat(changeByInput) || 0;
                            }
                            variable_Change.value = currentValue + numValueToChangeBy;
                            console.log(`Optimistic update: Variable ${variable_Change.name} (${block.inputs.VARIABLE_SCOPE}) changed by ${numValueToChangeBy} to ${variable_Change.value}`);
                            renderVariablePalette(); // Re-render variable palette with updated values
                        } else {
                            console.warn("Optimistic CHANGE_VARIABLE: target variable not found by ID", block.inputs.VARIABLE_ID);
                        }
                    }
                }
            });
            renderStage(); // Re-render stage immediately with optimistic updates
            renderStageMonitors(); // Update monitor values after optimistic changes

            outputDisplay.textContent = 'Executing...';

            const payload = currentProgram; // Reverted from programToRun used in temporary test
            console.log("Sending program to server:", payload);

            fetch('/api/execute_program', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                },
                body: JSON.stringify(payload)
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

    } // This was the end of the big "if (blockPalette && ..." block for script panel elements.
      // Note: Variable UI elements are part of blockPalette, so their listeners should be inside this if block too.
      // For simplicity, the variable-related event listeners will be added after this conditional block,
      // but ideally, they should also be guarded if their HTML elements are inside #scripts-panel.
      // Let's assume #variables-category and its contents are always present if #block-palette is.


    // --- Sprite Management Logic ---
    function createPlaceholderDataURL(width, height, color, text) {
        const canvas = document.createElement('canvas');
        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext('2d');
        ctx.fillStyle = color;
        ctx.fillRect(0, 0, width, height);
        ctx.fillStyle = 'black'; // Text color
        ctx.font = Math.min(width, height) / 2.5 + 'px Arial'; // Adjusted font size
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(text, width / 2, height / 2);
        return canvas.toDataURL();
    }

    function initializeDefaultSprite() {
        const defaultSpriteData = {
            id: 'sprite_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5),
            name: 'Sprite1',
            x: 0, y: 0,
            currentCostumeId: 'default_costume_id_sprite1',
            costumes: [{
                id: 'default_costume_id_sprite1',
                name: 'default',
                dataURL: createPlaceholderDataURL(80, 80, '#007bff', 'S1')
            }],
            sounds: [],
            scripts: [],
            variables: [ // Example of a predefined local var with monitor status
                // { id: 'local_var_sprite1_1', name: 'my_local', value: 0, isMonitored: false }
            ]
        };
        projectSprites.push(defaultSpriteData);
        setActiveSprite(defaultSpriteData.id);
    }

    function renderSpriteList() {
        if (!spriteListUL) return;
        spriteListUL.innerHTML = '';
        projectSprites.forEach(sprite => {
            const li = document.createElement('li');
            li.textContent = sprite.name;
            li.dataset.spriteId = sprite.id;
            if (sprite.id === activeSpriteId) {
                li.classList.add('active-sprite');
            }
            li.addEventListener('click', () => setActiveSprite(sprite.id));
            spriteListUL.appendChild(li);
        });
    }

    function setActiveSprite(spriteId) {
        activeSpriteId = spriteId;
        const activeSprite = getActiveSprite();
        if (!activeSprite) {
            console.error("Active sprite not found:", spriteId);
            currentProgram = []; // Reset currentProgram if active sprite is not found
        } else {
            console.log("Setting active sprite:", activeSprite.name);
            currentProgram = activeSprite.scripts; // Point currentProgram to the active sprite's scripts
        }

        renderProgram(); // Re-render blocks for the active sprite
        loadAssetsForActiveSprite();
        renderSpriteList(); // Re-render list to show active selection
        // Future: renderStage();
        renderVariablePalette(); // Also re-render variable palette as local vars change with sprite
        renderStageMonitors(); // Update monitors when sprite changes
    }

    function getActiveSprite() {
        if (!activeSpriteId) return null;
        return projectSprites.find(s => s.id === activeSpriteId);
    }

    // --- Stage Rendering ---
    function renderStage() {
        if (!stageArea || !projectSprites) {
            // console.warn("Stage area or projectSprites not available for rendering.");
            return;
        }

        stageArea.innerHTML = ''; // Clear previous sprites

        const stageWidth = stageArea.offsetWidth;
        const stageHeight = stageArea.offsetHeight;

        // Coordinate System Note:
        // Sprite X, Y are Scratch-like: (0,0) is stage center.
        // Positive X is right, Positive Y is up.
        // CSS top, left are from top-left corner of parent (#stage-area).
        // Positive top is down, Positive left is right.

        projectSprites.forEach(sprite => {
            const spriteDiv = document.createElement('div');
            spriteDiv.className = 'sprite-visual';
            spriteDiv.dataset.spriteId = sprite.id;

            let currentCostume = null;
            if (sprite.costumes && sprite.costumes.length > 0) {
                if (sprite.currentCostumeId) {
                    currentCostume = sprite.costumes.find(c => c.id === sprite.currentCostumeId);
                }
                if (!currentCostume) { // Fallback to first costume if currentCostumeId is invalid or not set
                    currentCostume = sprite.costumes[0];
                    sprite.currentCostumeId = currentCostume.id; // Correct it
                }
            }

            let spriteContentWidth = 30; // Default for placeholder
            let spriteContentHeight = 30; // Default for placeholder

            if (currentCostume && currentCostume.dataURL) {
                const img = document.createElement('img');
                img.src = currentCostume.dataURL;
                img.alt = currentCostume.name || 'Sprite Costume';

                img.onload = () => {
                    // Use naturalWidth/Height for original image dimensions if not otherwise specified
                    // For simplicity here, we'll let CSS/browser determine display size if not explicit
                    // Or, one might store width/height on costume object if scaling is intended
                    spriteContentWidth = img.naturalWidth > 0 ? img.naturalWidth : 30;
                    spriteContentHeight = img.naturalHeight > 0 ? img.naturalHeight : 30;

                    // Cap size for display if images are huge, or implement scaling properties on sprite
                    // For now, just use their loaded size.
                    // spriteDiv.style.width = spriteContentWidth + 'px';
                    // spriteDiv.style.height = spriteContentHeight + 'px';

                    const cssXCenter = sprite.x + stageWidth / 2;
                    const cssYCenter = -sprite.y + stageHeight / 2;

                    spriteDiv.style.left = (cssXCenter - spriteContentWidth / 2) + 'px';
                    spriteDiv.style.top = (cssYCenter - spriteContentHeight / 2) + 'px';
                };
                img.onerror = () => { // Handle broken image links
                    spriteDiv.style.backgroundColor = 'grey'; // Placeholder for broken image
                    spriteDiv.style.width = spriteContentWidth + 'px';
                    spriteDiv.style.height = spriteContentHeight + 'px';
                    const cssXCenter = sprite.x + stageWidth / 2;
                    const cssYCenter = -sprite.y + stageHeight / 2;
                    spriteDiv.style.left = (cssXCenter - spriteContentWidth / 2) + 'px';
                    spriteDiv.style.top = (cssYCenter - spriteContentHeight / 2) + 'px';
                };
                spriteDiv.appendChild(img);
            } else {
                // Placeholder visual if no costume or dataURL
                spriteDiv.style.backgroundColor = 'rgba(255, 0, 0, 0.7)'; // Semi-transparent red
                spriteDiv.style.width = spriteContentWidth + 'px';
                spriteDiv.style.height = spriteContentHeight + 'px';
                spriteDiv.style.border = '1px solid darkred';
                spriteDiv.style.display = 'flex'; // For centering text if any
                spriteDiv.style.alignItems = 'center';
                spriteDiv.style.justifyContent = 'center';
                spriteDiv.textContent = sprite.name.substring(0,1); // Show first letter

                const cssXCenter = sprite.x + stageWidth / 2;
                const cssYCenter = -sprite.y + stageHeight / 2;
                spriteDiv.style.left = (cssXCenter - spriteContentWidth / 2) + 'px';
                spriteDiv.style.top = (cssYCenter - spriteContentHeight / 2) + 'px';
            }
            stageArea.appendChild(spriteDiv);
        });
    }


    function loadAssetsForActiveSprite() {
        const activeSprite = getActiveSprite();

        const costumeThumbList = document.getElementById('costume-thumbnail-list');
        const soundL = document.getElementById('sound-list');

        if (costumeThumbList) {
            costumeThumbList.innerHTML = ''; // Clear previous
            if (activeSprite && activeSprite.costumes) {
                activeSprite.costumes.forEach(costume => {
                    const img = document.createElement('img');
                    img.src = costume.dataURL;
                    img.title = costume.name;
                    img.alt = costume.name;
                    img.dataset.costumeId = costume.id;
                    // Add click listener to thumbnails to set activeSprite.currentCostumeId
                    img.addEventListener('click', () => {
                        if(activeSprite) {
                            activeSprite.currentCostumeId = costume.id;
                            console.log(`Set active costume for ${activeSprite.name} to ${costume.name}`);
                            renderStage(); // Re-render stage to show new costume
                        }
                    });
                    if (activeSprite.currentCostumeId === costume.id) {
                        img.classList.add('selected-costume'); // Style the currently active costume
                    }
                    costumeThumbList.appendChild(img);
                });
            }
        }

        if (soundL) {
            soundL.innerHTML = ''; // Clear previous
            if (activeSprite && activeSprite.sounds) {
                activeSprite.sounds.forEach(sound => {
                    const soundItemDiv = document.createElement('div');
                    soundItemDiv.className = 'sound-item';
                    const soundNameSpan = document.createElement('span');
                    soundNameSpan.textContent = sound.name;
                    const playButton = document.createElement('button');
                    playButton.textContent = 'Play';
                    playButton.dataset.soundId = sound.id; // Store sound.id for the find
                    playButton.addEventListener('click', () => {
                        const spriteForSound = getActiveSprite(); // get current active sprite
                        if(spriteForSound && spriteForSound.sounds){
                             const soundToPlay = spriteForSound.sounds.find(s => s.id === playButton.dataset.soundId);
                             if (soundToPlay && soundToPlay.dataURL) {
                                new Audio(soundToPlay.dataURL).play().catch(e=>console.error("Error playing sound",e));
                             }
                        }
                    });
                    soundItemDiv.appendChild(soundNameSpan);
                    soundItemDiv.appendChild(playButton);
                    soundL.appendChild(soundItemDiv);
                });
            }
        }
    }

    // --- Modified Costume Upload Logic ---
    const costumeUploader = document.getElementById('costumeUploader');
    if (costumeUploader) {
        costumeUploader.addEventListener('change', (event) => {
            const activeSprite = getActiveSprite();
            if (!activeSprite) {
                alert("Please select a sprite first!");
                costumeUploader.value = ''; // Reset file input
                return;
            }

            activeSprite.costumes = []; // Clear existing costumes for the active sprite

            const files = event.target.files;
            if (files.length === 0) { // No files selected or cleared
                loadAssetsForActiveSprite(); // Re-render to show empty list
                return;
            }

            let filesProcessed = 0;
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                if (!file.type.startsWith('image/')) {
                    filesProcessed++;
                    if (filesProcessed === files.length) loadAssetsForActiveSprite();
                    continue;
                }
                const reader = new FileReader();
                reader.onload = (e) => {
                    const costumeData = {
                        name: file.name,
                        dataURL: e.target.result,
                        id: 'costume_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5)
                    };
                    activeSprite.costumes.push(costumeData);
                    filesProcessed++;
                    if (filesProcessed === files.length) {
                        if(activeSprite.costumes.length > 0 && !activeSprite.currentCostumeId) {
                            activeSprite.currentCostumeId = activeSprite.costumes[0].id;
                        }
                        loadAssetsForActiveSprite();
                        console.log("Costumes loaded for sprite '"+activeSprite.name+"':", activeSprite.costumes);
                    }
                };
                reader.onerror = () => {
                    console.error("Error reading file:", file.name);
                    filesProcessed++;
                    if (filesProcessed === files.length) {
                        loadAssetsForActiveSprite(); // Still call to refresh UI
                        renderStage(); // Re-render stage if costume list change might affect it
                    }
                };
                reader.readAsDataURL(file);
            }
            costumeUploader.value = ''; // Reset file input after processing
        });
    }

    // --- Modified Sound Upload Logic ---
    const soundUploader = document.getElementById('soundUploader');
    if (soundUploader) {
        soundUploader.addEventListener('change', (event) => {
            const activeSprite = getActiveSprite();
            if (!activeSprite) {
                alert("Please select a sprite first!");
                soundUploader.value = ''; // Reset file input
                return;
            }

            activeSprite.sounds = []; // Clear existing sounds for the active sprite

            const files = event.target.files;
            if (files.length === 0) {
                loadAssetsForActiveSprite();
                return;
            }

            let filesProcessed = 0;
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                if (!file.type.startsWith('audio/')) {
                     filesProcessed++;
                    if (filesProcessed === files.length) loadAssetsForActiveSprite();
                    continue;
                }
                const reader = new FileReader();
                reader.onload = (e) => {
                    const soundData = {
                        name: file.name,
                        dataURL: e.target.result,
                        id: 'sound_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5)
                    };
                    activeSprite.sounds.push(soundData);
                    filesProcessed++;
                    if (filesProcessed === files.length) {
                        loadAssetsForActiveSprite();
                        console.log("Sounds loaded for sprite '"+activeSprite.name+"':", activeSprite.sounds);
                    }
                };
                reader.onerror = () => {
                    console.error("Error reading sound file:", file.name);
                    filesProcessed++;
                    if (filesProcessed === files.length) loadAssetsForActiveSprite();
                };
                reader.readAsDataURL(file);
            }
            soundUploader.value = ''; // Reset file input
        });
    }

    // Initialize
    initializeDefaultSprite();

    // --- Variable Management UI Logic ---
    // All variable related functions and listeners are now properly inside DOMContentLoaded
    function renderVariablePalette() {
        if (!variablePaletteBlocksContainer) {
            // console.warn("Variable palette container not found, skipping render.");
            return;
        }
        variablePaletteBlocksContainer.innerHTML = ''; // Clear existing

        const activeSprite = getActiveSprite();

        projectGlobalVariables.forEach(gVar => {
            const blockDiv = createDraggableVariableReporter(gVar, 'global');
            variablePaletteBlocksContainer.appendChild(blockDiv);
        });

        if (activeSprite && activeSprite.variables) {
            activeSprite.variables.forEach(lVar => {
                const blockDiv = createDraggableVariableReporter(lVar, 'local', activeSprite.name);
                variablePaletteBlocksContainer.appendChild(blockDiv);
            });
        }
    }

    function createDraggableVariableReporter(varData, scope, spriteName = null) {
        const itemContainer = document.createElement('div');
        itemContainer.className = 'variable-palette-item';

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.checked = varData.isMonitored || false; // Default to false if undefined
        checkbox.title = 'Show variable monitor on stage';
        checkbox.dataset.variableId = varData.id;
        checkbox.dataset.variableScope = scope; // Store scope to help find the variable
        checkbox.addEventListener('change', (event) => {
            const varId = event.target.dataset.variableId;
            const varScope = event.target.dataset.variableScope;
            let targetVar = null;

            if (varScope === 'global') {
                targetVar = projectGlobalVariables.find(v => v.id === varId);
            } else { // 'local'
                const activeSprite = getActiveSprite();
                if (activeSprite && activeSprite.variables) {
                    targetVar = activeSprite.variables.find(v => v.id === varId);
                }
            }

            if (targetVar) {
                targetVar.isMonitored = event.target.checked;
                renderStageMonitors();
            } else {
                console.warn("Could not find variable for monitor checkbox:", varId, varScope);
            }
        });

        const blockDiv = document.createElement('div');
        blockDiv.className = 'variable-reporter-block palette-block'; // Existing class for styling the reporter part
        blockDiv.textContent = spriteName ? `${spriteName}:${varData.name}` : varData.name;
        blockDiv.draggable = true;
        blockDiv.dataset.variableId = varData.id;
        blockDiv.dataset.variableName = varData.name;
        blockDiv.dataset.variableScope = scope;
        // We are already stringifying varData.value when setting this dataset attribute.
        // And when retrieving, it will be a string. So, direct use or JSON.parse if it was an object.
        blockDiv.dataset.variableValue = (typeof varData.value === 'object') ? JSON.stringify(varData.value) : String(varData.value);


        blockDiv.addEventListener('dragstart', (event) => {
            const dragData = {
                reporterType: 'VARIABLE',
                type: 'VARIABLE_REPORTER',
                id: varData.id,
                name: varData.name,
                scope: scope,
                // No need to transfer 'value' for drag-drop of reporter itself,
                // it's for creating the block with current value if needed by block logic
            };
            event.dataTransfer.setData('application/json', JSON.stringify(dragData));
            event.dataTransfer.effectAllowed = 'copy';
            console.log(`Drag started for variable: ${varData.name}`);
        });

        itemContainer.appendChild(checkbox);
        itemContainer.appendChild(blockDiv);
        return itemContainer; // Return the container
    }

    function renderStageMonitors() {
        if (!stageVariableMonitorsContainer) {
            console.warn("Stage variable monitors container not found.");
            return;
        }
        stageVariableMonitorsContainer.innerHTML = ''; // Clear existing monitors

        const activeSprite = getActiveSprite();

        // Display global variables that are monitored
        projectGlobalVariables.forEach(gVar => {
            if (gVar.isMonitored) {
                const monitorDiv = document.createElement('div');
                monitorDiv.className = 'variable-monitor';
                // Using escapeHtml to be safe, though variable names and values are typically benign
                monitorDiv.innerHTML = `<span class="variable-monitor-name">${escapeHtml(gVar.name)}</span>
                                      <span class="variable-monitor-value">${escapeHtml(String(gVar.value))}</span>`;
                stageVariableMonitorsContainer.appendChild(monitorDiv);
            }
        });

        // Display local variables of the active sprite that are monitored
        if (activeSprite && activeSprite.variables) {
            activeSprite.variables.forEach(lVar => {
                if (lVar.isMonitored) {
                    const monitorDiv = document.createElement('div');
                    monitorDiv.className = 'variable-monitor';
                    // Optionally prefix local var names on stage for clarity, e.g., `${activeSprite.name}:${lVar.name}`
                    // For now, just the variable name as Scratch does.
                    monitorDiv.innerHTML = `<span class="variable-monitor-name">${escapeHtml(lVar.name)}</span>
                                          <span class="variable-monitor-value">${escapeHtml(String(lVar.value))}</span>`;
                    stageVariableMonitorsContainer.appendChild(monitorDiv);
                }
            });
        }
    }

    if (makeVariableButton && variableModal && variableNameInput && confirmVariableButton && cancelVariableButton) {
        makeVariableButton.addEventListener('click', () => {
            variableNameInput.value = '';
            variableNameInput.setCustomValidity("");
            document.querySelector('input[name="variableScope"][value="global"]').checked = true;

            const activeSprite = getActiveSprite();
            const localScopeRadio = document.getElementById('scopeLocal');
            const localScopeLabel = document.querySelector('label[for="scopeLocal"]');

            if (localScopeRadio) {
                 localScopeRadio.disabled = !activeSprite;
                 if (localScopeLabel) localScopeLabel.style.color = activeSprite ? '' : '#aaa';
                 if (!activeSprite) {
                    document.querySelector('input[name="variableScope"][value="global"]').checked = true;
                 }
            }
            variableModal.style.display = 'flex';
            variableNameInput.focus();
        });

        cancelVariableButton.addEventListener('click', () => {
            variableModal.style.display = 'none';
        });

        variableModal.addEventListener('click', (e) => {
            if (e.target === variableModal) {
                 variableModal.style.display = 'none';
            }
        });
        const varModalContent = variableModal.querySelector('.modal-content');
        if(varModalContent) varModalContent.addEventListener('click', (e) => e.stopPropagation());

        confirmVariableButton.addEventListener('click', () => {
            const varName = variableNameInput.value.trim();
            const scope = document.querySelector('input[name="variableScope"]:checked').value;
            const activeSprite = getActiveSprite();

            if (!variableNameInput.checkValidity() || !varName) {
                variableNameInput.setCustomValidity('Invalid variable name. Use letters, numbers, or underscores. Cannot start with a number.');
                alert('Invalid variable name. It must start with a letter or underscore, and contain only letters, numbers, or underscores.');
                variableNameInput.reportValidity();
                variableNameInput.focus();
                return;
            }
            variableNameInput.setCustomValidity("");

            let nameExists = false;
            if (scope === 'global') {
                nameExists = projectGlobalVariables.some(v => v.name === varName);
            } else if (activeSprite && activeSprite.variables) {
                nameExists = activeSprite.variables.some(v => v.name === varName);
            } else if (scope === 'local' && !activeSprite) {
                 alert("Cannot create local variable: No active sprite selected.");
                 return;
            }

            if (nameExists) {
                alert(`A variable named "${varName}" already exists in this scope.`);
                return;
            }

            const newVar = {
                id: 'var_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5),
                name: varName,
                value: 0,
                isMonitored: false // Add isMonitored flag
            };

            if (scope === 'global') {
                projectGlobalVariables.push(newVar);
                console.log("Global variable created:", newVar);
            } else {
                if (!activeSprite.variables) activeSprite.variables = [];
                activeSprite.variables.push(newVar);
                console.log("Local variable created for sprite '"+activeSprite.name+"':", newVar);
            }

            renderVariablePalette();
            renderStageMonitors(); // Update monitors if a new variable was created (though it defaults to not monitored)
            variableModal.style.display = 'none';
        });
    } else {
        // Only log this warning if the script panel is the one that's supposed to be active
        // This can be refined based on which tab is active on load.
        if (document.getElementById('scripts-panel').classList.contains('active')) {
             console.warn("Variable UI elements (#makeVariableButton etc.) not fully found. Variable creation might not work.");
        }
    }

    renderVariablePalette(); // Initial render after default sprite and its vars are set up.
    renderStageMonitors(); // Initial render of stage monitors
    console.log("JavaScript app.js loaded and initialized. Default sprite created and stage rendered.");
});

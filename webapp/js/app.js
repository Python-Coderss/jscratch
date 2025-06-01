document.addEventListener('DOMContentLoaded', () => {
    // Element References
    const blockPalette = document.getElementById('block-palette');
    // Using the more specific div for dropping/showing assembled blocks
    const scriptAssemblyDisplay = document.getElementById('assembled-script-display');
    const stageArea = document.getElementById('stage-area');
    const runProgramButton = document.getElementById('runProgramButton');
    const outputDisplay = document.getElementById('output-display');

    let currentProgram = []; // Array to hold the sequence of blocks in the program
    let currentSpriteCostumes = []; // Array to hold costume data objects

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


    // --- Draggable Blocks in Palette & Associated Logic ---
    if (blockPalette && scriptAssemblyDisplay && runProgramButton && outputDisplay) {
        // ^ Ensure all main script panel elements are present before setting up this part of UI

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
        } else {
            console.error(`Unknown block type dropped: ${blockType}`);
            return;
        }

        if (blockData) {
            // For simplicity, new blocks are always added to the main program list.
            // If a drop target was within a loop block, logic would be more complex here.
            currentProgram.push(blockData);
            renderProgram();
            console.log("Current program after adding block:", currentProgram);
        }
    }

    // --- Render Program in Assembly Area ---
    function renderProgram() {
        if (!scriptAssemblyDisplay) return;
        scriptAssemblyDisplay.innerHTML = ''; // Clear previous content

        if (currentProgram.length === 0) {
            scriptAssemblyDisplay.innerHTML = '<p><em>Drag blocks here to build your program.</em></p>';
            return;
        }

        currentProgram.forEach(block => {
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
            } else { // Could add LOOP_BLOCK here too if allowing nested loops visually from start
                content = `Unknown Block (ID: ${child.id.substring(0,10)})`;
            }
            // Apply appropriate classes for styling nested blocks similarly to top-level ones
            const blockClasses = `assembled-block ${child.type === 'PYTHON_BLOCK' ? 'assembled-python-block' : child.type === 'SAY_BLOCK' ? 'assembled-say-block' : ''}`;
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

            if (currentProgram.length === 0) {
                console.log("No program to run.");
                outputDisplay.textContent = "No program to run. Drag blocks to the assembly area.";
                return;
            }

            outputDisplay.textContent = 'Executing...';

            // For now, backend expects a single block object, not an array.
            // We will send the whole array, but mention that backend needs update.
            // Or, for temporary compatibility, send only the first block:
            // const payload = currentProgram.length > 0 ? currentProgram[0] : {};
            // Let's send the whole array as per the subtask instructions,
            // acknowledging the backend isn't ready for it yet.
            const payload = currentProgram;
            console.log("Sending program to server (backend currently expects single object, will misinterpret array):", payload);

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

    // Initial state for script assembly area
    if (scriptAssemblyDisplay) { // This check should be fine as scriptAssemblyDisplay is global
        renderProgram(); // Ensures the initial placeholder is shown if currentProgram is empty
    }


    // --- Costume Upload Logic ---
    const costumeUploader = document.getElementById('costumeUploader');
    const costumeThumbnailList = document.getElementById('costume-thumbnail-list');

    if (costumeUploader && costumeThumbnailList) {
        costumeUploader.addEventListener('change', (event) => {
            costumeThumbnailList.innerHTML = ''; // Clear previous thumbnails
            currentSpriteCostumes = []; // Reset current costumes array

            const files = event.target.files;
            if (files.length === 0) return;

            let filesProcessed = 0;
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                if (!file.type.startsWith('image/')){
                    filesProcessed++;
                    if (filesProcessed === files.length) console.log("All files processed (some might be skipped).");
                    continue;
                }

                const reader = new FileReader();
                reader.onload = (e) => {
                    const costumeData = {
                        name: file.name,
                        dataURL: e.target.result,
                        id: 'costume_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5)
                    };
                    currentSpriteCostumes.push(costumeData);

                    const img = document.createElement('img');
                    img.src = costumeData.dataURL;
                    img.title = costumeData.name;
                    img.alt = costumeData.name;
                    img.dataset.costumeId = costumeData.id;
                    costumeThumbnailList.appendChild(img);

                    filesProcessed++;
                    if (filesProcessed === files.length) {
                         console.log("All files processed. Costumes loaded:", currentSpriteCostumes);
                    }
                };
                reader.onerror = () => {
                    console.error("Error reading file:", file.name);
                    filesProcessed++;
                    if (filesProcessed === files.length) console.log("All files processed (some might have errors).");
                };
                reader.readAsDataURL(file);
            }
        });
    } else {
        // This might log if script is parsed before costumes tab is shown or if elements are missing.
        // console.warn("Costume uploader or thumbnail list not found on this page/tab.");
    }

    // --- Sound Upload Logic ---
    const soundUploader = document.getElementById('soundUploader');
    const soundList = document.getElementById('sound-list');
    let currentSpriteSounds = []; // Global array for sounds

    if (soundUploader && soundList) {
        soundUploader.addEventListener('change', (event) => {
            soundList.innerHTML = ''; // Clear previous sound items
            currentSpriteSounds = []; // Reset current sounds array

            const files = event.target.files;
            if (files.length === 0) return;

            let filesProcessed = 0;
            for (let i = 0; i < files.length; i++) {
                const file = files[i];
                if (!file.type.startsWith('audio/')) {
                    filesProcessed++;
                    if (filesProcessed === files.length) console.log("All sound files processed (some might be skipped).");
                    continue;
                }

                const reader = new FileReader();
                reader.onload = (e) => {
                    const soundData = {
                        name: file.name,
                        dataURL: e.target.result, // Store data URL for playback
                        id: 'sound_' + Date.now() + '_' + Math.random().toString(36).substr(2, 5)
                    };
                    currentSpriteSounds.push(soundData);

                    const soundItemDiv = document.createElement('div');
                    soundItemDiv.className = 'sound-item';

                    const soundNameSpan = document.createElement('span');
                    soundNameSpan.textContent = soundData.name;

                    const playButton = document.createElement('button');
                    playButton.textContent = 'Play';
                    playButton.dataset.soundId = soundData.id;

                    playButton.addEventListener('click', () => {
                        const soundToPlay = currentSpriteSounds.find(s => s.id === playButton.dataset.soundId);
                        if (soundToPlay && soundToPlay.dataURL) {
                            const audio = new Audio(soundToPlay.dataURL);
                            audio.play().catch(playError => {
                                console.error("Error playing sound:", soundData.name, playError);
                                alert("Error playing sound: " + playError.message);
                            });
                        }
                    });

                    soundItemDiv.appendChild(soundNameSpan);
                    soundItemDiv.appendChild(playButton);
                    soundList.appendChild(soundItemDiv);

                    filesProcessed++;
                    if (filesProcessed === files.length) {
                        console.log("All sound files processed. Sounds loaded:", currentSpriteSounds);
                    }
                };
                reader.onerror = () => {
                    console.error("Error reading sound file:", file.name);
                    filesProcessed++;
                    if (filesProcessed === files.length) console.log("All sound files processed (some might have errors).");
                };
                reader.readAsDataURL(file); // Read file as Data URL
            }
        });
    } else {
        // console.warn("Sound uploader or sound list not found on this page/tab.");
    }

    console.log("JavaScript app.js loaded and initialized. Current active tab should be visible.");
});

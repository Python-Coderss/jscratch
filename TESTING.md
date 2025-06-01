# Manual Test Cases

## Introduction

This document outlines manual testing procedures for the Java Scratch-like Web Application. These tests are designed to verify the functionality of the user interface and its interaction with the backend server.

## Prerequisites for Testing

*   **Server Running:** The Java HTTP server (`SimpleHttpServer.java`) must be compiled and running. This requires:
    *   Java JDK (8 or higher).
    *   Jython Standalone JAR (`jython-standalone-X.Y.Z.jar`) in the `lib/` directory.
    *   Org.JSON JAR (`json-YYYYMMDD.jar`) in the `lib/` directory.
    *   Refer to `README.md` for detailed build and run instructions.
*   **Web Browser:** A modern web browser (e.g., Chrome, Firefox, Edge, Safari).
*   **Sample Files:**
    *   At least 2-3 sample image files (e.g., `.png`, `.jpg`) for costume upload testing.
    *   At least 2-3 sample audio files (e.g., `.mp3`, `.wav`) for sound upload testing.
    *   One non-image file (e.g., `.txt`) for negative testing of costume upload.
    *   One non-audio file (e.g., `.txt`) for negative testing of sound upload.

## Test Case Format

For each test case:
*   **TC ID:** Unique Test Case Identifier.
*   **Description:** What is being tested.
*   **Steps to Reproduce:** Detailed steps to perform the test.
*   **Expected Result:** The anticipated outcome.
*   **Actual Result:** (To be filled by the tester).
*   **Pass/Fail:** (To be filled by the tester).
*   **Notes:** Any additional comments.

---

## Test Cases

### Tab Navigation

| TC ID | Description                                  | Steps to Reproduce                                                                                                | Expected Result                                                                                                | Actual Result | Pass/Fail | Notes |
|-------|----------------------------------------------|-------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------|---------------|-----------|-------|
| TN-001| Verify default tab is "Scripts"            | 1. Open the application in a web browser (`http://localhost:8000/`).                                               | The "Scripts" tab is active, and the Scripts panel (Block Palette, Script Assembly, Stage) is visible.         |               |           |       |
| TN-002| Click "Costumes" tab                         | 1. From the default view, click the "Costumes" tab button.                                                        | The "Costumes" tab becomes active. The Costumes panel (uploader, thumbnail list) is visible. Scripts panel is hidden. |               |           |       |
| TN-003| Click "Sounds" tab                           | 1. From the default view or Costumes tab, click the "Sounds" tab button.                                          | The "Sounds" tab becomes active. The Sounds panel (uploader, sound list) is visible. Other panels are hidden.    |               |           |       |
| TN-004| Click "Scripts" tab again                    | 1. Navigate to "Costumes" or "Sounds" tab. <br> 2. Click the "Scripts" tab button.                                 | The "Scripts" tab becomes active. The Scripts panel is visible. Other panels are hidden.                       |               |           |       |

### Block Palette & Drag-and-Drop

| TC ID | Description                                  | Steps to Reproduce                                                                                               | Expected Result                                                                                                     | Actual Result | Pass/Fail | Notes |
|-------|----------------------------------------------|------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|---------------|-----------|-------|
| DD-001| Drag "Python Block" to assembly area       | 1. Ensure "Scripts" tab is active. <br> 2. Drag the "Python Code Block" from the palette to the "Script Assembly Area". | A modal dialog appears, prompting for Python code input. The block is added to the internal program array.          |               |           |       |
| DD-002| Drag "Say Block" to assembly area          | 1. Ensure "Scripts" tab is active. <br> 2. Drag the "Say Block" from the palette to the "Script Assembly Area".        | A prompt dialog appears, asking for the text to say. The block is added to the internal program array.             |               |           |       |
| DD-003| Drag "Loop Block" to assembly area         | 1. Ensure "Scripts" tab is active. <br> 2. Drag the "Loop Block" from the palette to the "Script Assembly Area".       | A prompt dialog appears, asking for the loop count. The block is added to the internal program array.              |               |           |       |
| DD-004| Verify blocks stack vertically in order    | 1. Add a "Say Block" (enter "First"). <br> 2. Add a "Python Block" (enter `print('Second')`). <br> 3. Add another "Say Block" (enter "Third"). | The blocks appear in the "Script Assembly Area" in the order they were added: Say "First", then Python, then Say "Third". |               |           |       |
| DD-005| Drag over feedback for assembly area       | 1. Drag any block over the "Script Assembly Area".                                                                 | The "Script Assembly Area" shows visual feedback (e.g., a dashed border or background color change).                 |               |           |       |

### Python Block Functionality

| TC ID | Description                               | Steps to Reproduce                                                                                                                               | Expected Result                                                                                                    | Actual Result | Pass/Fail | Notes |
|-------|-------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|---------------|-----------|-------|
| PY-001| Add Python block with valid code          | 1. Add a "Python Code Block". <br> 2. In the modal, enter `print('Test Python Output')`. <br> 3. Click "Save Python".                               | The block appears in the assembly area, showing "Python Block (Code Saved)" and a snippet of the code.             |               |           |       |
| PY-002| Run program with valid Python block       | 1. Perform PY-001. <br> 2. Click "Run Program".                                                                                                  | The "Stage Output" area displays: `Block 1 (PYTHON_BLOCK):
  Test Python Output` (or similar, based on Jython output). |               |           |       |
| PY-003| Run Python block with syntax error        | 1. Add a "Python Code Block". <br> 2. Enter `print 'Syntax Error` (invalid Python 3 syntax). <br> 3. Click "Save Python". <br> 4. Click "Run Program". | The "Stage Output" area displays an error message from Jython indicating a syntax error.                           |               |           |       |
| PY-004| Python block with multiple print statements | 1. Add Python block: `print('Line 1')\nprint('Line 2')`. Save. <br> 2. Run.                                                                     | Stage Output shows: `Block X (PYTHON_BLOCK):
  Line 1
  Line 2`                                               |               |           |       |
| PY-005| Cancel Python block modal                 | 1. Drag Python block. Modal appears. <br> 2. Click "Cancel".                                                                                   | Modal closes. No new block is added to the assembly area. `currentProgram` array length remains unchanged.        |               |           |       |


### Say Block Functionality

| TC ID | Description                               | Steps to Reproduce                                                                                                                              | Expected Result                                                                                                  | Actual Result | Pass/Fail | Notes |
|-------|-------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|---------------|-----------|-------|
| SAY-001| Add Say block with text                   | 1. Add a "Say Block". <br> 2. When prompted, enter "Hello, Test!". <br> 3. Click "OK".                                                           | The block appears in the assembly area, showing `Say: "Hello, Test!"`.                                        |               |           |       |
| SAY-002| Run program with Say block                | 1. Perform SAY-001. <br> 2. Click "Run Program".                                                                                                 | The "Stage Output" area displays: `Block 1 (SAY_BLOCK):
  [Output] SAY: Hello, Test!`                            |               |           |       |
| SAY-003| Cancel Say block prompt                   | 1. Drag Say block. Prompt appears. <br> 2. Click "Cancel" or press Esc.                                                                         | Prompt closes. No new block is added. `currentProgram` array length remains unchanged.                           |               |           |       |

### Loop Block Functionality (Placeholder)

| TC ID | Description                               | Steps to Reproduce                                                                                                                               | Expected Result                                                                                                 | Actual Result | Pass/Fail | Notes |
|-------|-------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|---------------|-----------|-------|
| LP-001| Add Loop block with valid count           | 1. Add a "Loop Block". <br> 2. When prompted, enter "5". <br> 3. Click "OK".                                                                       | The block appears in the assembly area, showing "Loop 5 times" and a nested area placeholder.                   |               |           |       |
| LP-002| Run program with Loop block               | 1. Perform LP-001. <br> 2. Click "Run Program".                                                                                                  | Stage Output displays: `Block 1 (LOOP_BLOCK):
  Loop 5 times (Note: execution of children not yet implemented).` |               |           |       |
| LP-003| Add Loop block with invalid count         | 1. Add a "Loop Block". <br> 2. Enter "abc" or "-1" or "0". <br> 3. Click "OK".                                                                    | An alert "Invalid loop count..." appears. No block is added. `currentProgram` array length remains unchanged. |               |           |       |
| LP-004| Cancel Loop block prompt                  | 1. Drag Loop block. Prompt appears. <br> 2. Click "Cancel".                                                                                      | Prompt closes. No new block is added. `currentProgram` array length remains unchanged.                          |               |           |       |

### Costume Panel

| TC ID | Description                               | Steps to Reproduce                                                                                                                                  | Expected Result                                                                                                    | Actual Result | Pass/Fail | Notes |
|-------|-------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|---------------|-----------|-------|
| COS-001| Navigate to Costumes tab & upload 1 image | 1. Click "Costumes" tab. <br> 2. Click "Choose Files" / "Browse". <br> 3. Select one valid image file (e.g., PNG, JPG).                               | One thumbnail of the selected image appears in the "costume-thumbnail-list" area. `currentSpriteCostumes` has 1 item. |               |           |       |
| COS-002| Upload multiple images                    | 1. Click "Costumes" tab. <br> 2. Click "Choose Files". <br> 3. Select 2-3 valid image files.                                                        | Thumbnails for all selected valid images appear. `currentSpriteCostumes` has corresponding number of items.       |               |           |       |
| COS-003| Upload non-image file                     | 1. Click "Costumes" tab. <br> 2. Click "Choose Files". <br> 3. Select a non-image file (e.g., .txt).                                                | No thumbnail for the non-image file appears. If other valid images were selected, they should still appear.        |               |           |       |
| COS-004| Upload mix of image and non-image files   | 1. Click "Costumes" tab. <br> 2. Click "Choose Files". <br> 3. Select one image file and one .txt file.                                              | Only the thumbnail for the image file appears.                                                                     |               |           |       |
| COS-005| Re-upload clears previous costumes        | 1. Perform COS-001. <br> 2. Click "Choose Files" again. <br> 3. Select a *different* single image file.                                              | Only the thumbnail for the newly selected image appears. `currentSpriteCostumes` has 1 item (the new one).        |               |           |       |

### Sounds Panel

| TC ID | Description                               | Steps to Reproduce                                                                                                                                 | Expected Result                                                                                                   | Actual Result | Pass/Fail | Notes |
|-------|-------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------|---------------|-----------|-------|
| SND-001| Navigate to Sounds tab & upload 1 audio   | 1. Click "Sounds" tab. <br> 2. Click "Choose Files". <br> 3. Select one valid audio file (e.g., MP3, WAV).                                          | The sound's name and a "Play" button appear in the "sound-list". `currentSpriteSounds` has 1 item.                |               |           |       |
| SND-002| Upload multiple audio files               | 1. Click "Sounds" tab. <br> 2. Click "Choose Files". <br> 3. Select 2-3 valid audio files.                                                       | Each sound name and a "Play" button appear. `currentSpriteSounds` has corresponding number of items.              |               |           |       |
| SND-003| Upload non-audio file                     | 1. Click "Sounds" tab. <br> 2. Click "Choose Files". <br> 3. Select a non-audio file (e.g., .txt).                                                 | No entry for the non-audio file appears. If other valid audio files were selected, they should still appear.      |               |           |       |
| SND-004| Play uploaded sound                       | 1. Perform SND-001. <br> 2. Click the "Play" button next to the uploaded sound's name.                                                            | The sound plays through the computer's speakers/headphones.                                                       |               |           |       |
| SND-005| Re-upload clears previous sounds          | 1. Perform SND-001. <br> 2. Click "Choose Files" again. <br> 3. Select a *different* single audio file.                                             | Only the entry for the newly selected sound appears. `currentSpriteSounds` has 1 item (the new one).             |               |           |       |

### Multi-Block Program Execution

| TC ID | Description                                  | Steps to Reproduce                                                                                                                                                              | Expected Result                                                                                                                                                                  | Actual Result | Pass/Fail | Notes |
|-------|----------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|-----------|-------|
| MB-001| Sequence: Say, Python, Say                   | 1. Add Say block ("Start Program"). <br> 2. Add Python block (`print('Processing...')\nprint(1+2)`). <br> 3. Add Say block ("End Program"). <br> 4. Click "Run Program".        | Stage Output shows aggregated results in order: <br> `Block 1 (SAY_BLOCK):
  [Output] SAY: Start Program
<br>Block 2 (PYTHON_BLOCK):
  Processing...
  3
<br>Block 3 (SAY_BLOCK):
  [Output] SAY: End Program
` |               |           |       |
| MB-002| Program with Loop block (placeholder)        | 1. Add Say block ("Before Loop"). <br> 2. Add Loop block (count 2). <br> 3. Add Say block ("After Loop"). <br> 4. Click "Run Program".                                            | Stage Output shows: <br> `Block 1 (SAY_BLOCK):
  [Output] SAY: Before Loop
<br>Block 2 (LOOP_BLOCK):
  Loop 2 times (Note: execution of children not yet implemented).
<br>Block 3 (SAY_BLOCK):
  [Output] SAY: After Loop
` |               |           |       |
| MB-003| Empty program execution                      | 1. Ensure "Script Assembly Area" is empty (refresh page if needed). <br> 2. Click "Run Program".                                                                                   | Stage Output shows: "No program to run. Drag blocks to the assembly area."                                                                                                       |               |           |       |

---

# Scratch-style blocks in JScratch

JScratch keeps Java as its editable, compilable source of truth. The Java tab shows readable Java with JScratch-only block, parameter, and slot markers hidden; Java Source keeps those editable annotations visible, and Blocks interprets them as Scratch-like, category-coloured command/reporter/control blocks. The readable tab has Java syntax highlighting and folding. Editing an inline parameter changes only its marked Java value. Block comments are Java comments, so neither the compiler nor exported game needs a custom runtime.

## Package-aware project sources

- Source files under `jscratch_ws/src` are discovered recursively and shown beneath nested, collapsible package folders. A source file leaf represents one fully qualified Java type.
- Every package directory segment must match the `package` declaration. A file in `game/ui/Hud.java` must say `package game.ui;`. A file directly in the source root must have no package declaration.
- Mismatches are listed in the editor console and stop compilation rather than silently compiling under a different identity. For manually-added editor sources, package declarations must match their selected tree package too.
- The Add Sprite prompt accepts a simple class name (defaults to `generated`) or a fully qualified class name such as `game.ui.HudSprite`.
- Compiler file paths, runner entry-point selection and exported-game entry-point metadata retain fully qualified names. Older exports and project JSON continue to default to `generated.Main` / `generated`.
- Build cleanup is limited to stale `.java` files inside package directories represented by project sources; other package trees and non-Java files are preserved.

## Source annotations

A block is bounded by matching start/end comments. The optional preceding comment is the equivalent of Scratch's pinned comment card:

```java
/* block comment [block:"move_1" text:"Move the sprite by the requested number of steps."] */
/* block start [id:"move_1" category:"Motion" label:"move steps" shape:"command" docs:"Move the sprite by a number of steps."] */
move(/* param start [id:"move_1_steps" type:"number"] */10/* param end [id:"move_1_steps"] */);
/* block end [id:"move_1"] */
```

- `id` is unique in one source file and must match between start/end markers.
- `category` sets the Scratch palette colour (Motion, Looks, Sound, Events, Control, Sensing, Operators, Variables, My Blocks, plus Structure/Helpers categories).
- `label` is the visible block text. Keep it concise and action-oriented.
- `shape` accepts `command`, `control`, `reporter`, or `boolean`; the editor styles control and value blocks distinctly.
- `docs` is the hover help. `block comment` text is the yellow, pinned comment attached to the block. They can be similar, but docs should explain what the user can do with the block.
- Parameter values are the exact source text between matching markers. Types `number`, `text`/`string`, `boolean`, `character`, and `value` get inline inputs. Text and character values are quoted/escaped when written back. The editor resolves a marker id at the time of each edit, so earlier field edits do not leave stale offsets.
- Nested block ranges are shown as nested stacks, especially useful for control structures. Editing a parameter in either view updates the same Java document.

`ScratchBlockAnnotator` runs a conservative, idempotent first-open migration for unannotated source. Its small lexer skips comments and string contents; it adds visible control-structure and statement blocks, local declarations, and editable literal markers. It also refreshes legacy generic My Blocks call labels into explicit `call methodName` blocks while leaving authored labels/docs intact. The migration is a UI convenience rather than a Java parser: review unusual constructs and refine their annotations in source when needed.

## Palette definitions (`blocks.txt`)

The text resource retains its legacy snippet format and accepts documentation and editable palette values:

```text
# Move Steps : Motion : inInstanceMethod : docs=Move this sprite by a number of steps.
move(${param:steps:number:10});
###
```

`docs=` is shown in the palette tooltip. `${param:name:type:default}` prompts for a value when dragging a snippet; text is quoted/escaped, while number/boolean/value insert as Java expressions. `${id}` remains available for unique snippet slots. The Structure palette includes named instance, parameterized, and static method definitions. My Blocks includes method-call statements (including calls with arguments and static calls); enter the exact Java method name and valid argument expressions. Existing slot tags (`inInstanceMethod`, `inStaticMethod`, `inClassNotMethod`, `literal`, `anywhere`, and `allowEmpty`) continue to control valid drop locations.

Prefer general Scratch primitives rather than one-off blocks for each game's rules. Put game-specific behavior in My Blocks/custom Java methods. The Variables palette has local number, text, boolean and update templates: locals are appropriate for values used only within the current method/script stack, avoid repeated calculation and allocation (helpful in CheerpJ), and should not be mistaken for long-lived Scratch globals.

## DnDiablo gameplay lifecycle

`GameState` is the central screen/pause switch. The character-creation screen owns boot; the game shows the dungeon only after `CharacterCreation.finish`. During gameplay, H toggles a controls overlay and pauses world updates. The controls card lists movement, melee, spell, inventory, selection, and mute keys.

When HP reaches zero, `EnemySprite.landStrike` broadcasts `playerDied`; CharacterCreation pauses the run and displays the death card. “Make a New Character” resets inventory, player stats, enemies, loot, summons, floor/victory state, and returns to the title/character flow. The old Sprite1 loop is stopped before a replacement driver is created.

`EnemyManager.startSpawnGrace(10_000)` suppresses regular mobs for ten seconds after the run begins and after each floor change; it does not make the player invulnerable. Floor 1 remains the fresh-run destination. Ghouls share pursuit/attack direction logic with other enemies but add 180 degrees after each facing update because their supplied art faces backward.

## Extending the annotation system

Keep marker syntax synchronized between this guide, `ScratchBlockAnnotator` and `CodeWorkspace`. For a new block field, document its type and write-back rules; preserve comments as inert Java so compile/export remains ordinary Java. If adding a source path feature, update recursive discovery, package/path validation, qualified compilation paths, project tree selection and exported entry point together.

## Context

The app currently has no explanatory text for users. The UI shows a board, pieces list, date picker, and solve button, but nothing explains the puzzle goal or controls. Users must discover interactions (drag-and-drop, click-to-remove, rotate with spacebar, double-click restore) by trial and error.

The existing layout places the board on the left and a sidebar (date picker + pieces list) on the right inside a `workspace-card` flex container, with a button group below.

## Goals / Non-Goals

**Goals:**
- Provide a concise, scannable instructions panel that covers the puzzle goal and all controls
- Make instructions collapsible so they don't take up space after the first read
- Persist the collapsed/expanded state across page loads using `localStorage`

**Non-Goals:**
- Interactive tutorial or guided walkthrough
- Tooltips on individual controls
- Internationalisation / multi-language support
- Backend changes

## Decisions

### 1. Standalone collapsible component
**Decision**: Create an `Instructions` component that renders as a collapsible panel with a header toggle.

**Rationale**: Keeps the instructions self-contained and reusable. A collapsible panel respects screen real estate while remaining discoverable.

**Alternative considered**: A modal/dialog — rejected because it interrupts flow and requires explicit dismissal.

### 2. Placement above the workspace card
**Decision**: Place the instructions panel between the header and the workspace card.

**Rationale**: Visible on first load without scrolling. Doesn't interfere with the board or pieces layout. When collapsed, it's just a single-line header.

### 3. Collapsed state persisted in localStorage
**Decision**: Use `localStorage` key `puzzleInstructionsCollapsed` to remember the user's preference.

**Rationale**: Returning users shouldn't have to re-collapse instructions every visit. `localStorage` is the simplest persistence mechanism with no backend required.

### 4. Default to expanded on first visit
**Decision**: Instructions start expanded when no `localStorage` value exists.

**Rationale**: First-time users need to see the instructions. Returning users who have collapsed them will see them collapsed.

## Risks / Trade-offs

- **[Screen space on small viewports]** → The panel is collapsible, and the content is concise. Acceptable trade-off for discoverability.
- **[Instructions become stale]** → If controls change, instructions need manual updates. Mitigated by keeping the text short and generic.

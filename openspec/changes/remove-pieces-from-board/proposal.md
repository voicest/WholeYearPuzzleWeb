## Why

Currently, the only way to remove a manually-placed piece from the board is to double-click it in the pieces list sidebar — a hidden, undiscoverable interaction. There is no way to interact with placed pieces directly on the board, and no way to clear all placements at once. This makes the manual puzzle-solving experience frustrating, especially when experimenting with different layouts.

## What Changes

- Add the ability to click on a placed piece directly on the board to remove it and return it to the available pieces list
- Add a "Clear Board" button to remove all manually-placed pieces at once
- Add visual affordance (cursor change, hover highlight) on placed board pieces to indicate they are interactive/removable

## Capabilities

### New Capabilities
- `board-piece-removal`: Click-to-remove interaction on placed pieces within the board grid, including hover affordance and state cleanup
- `clear-board`: A "Clear Board" button that removes all manually-placed pieces in one action, resetting the board to its pre-solve state

### Modified Capabilities

## Impact

- **Frontend only** — no backend/API changes required
- **App.js** — new handler for board-cell click removal; new clear-all handler; state updates to `solution` and `pieces[].used`
- **Board.js** — click handler on filled cells; hover styling for placed pieces; cursor affordance
- **Board.css** — hover/interactive styles for removable cells
- **PiecesList.js / App.css** — "Clear Board" button placement and styling
- No breaking changes — existing double-click restore in PiecesList remains functional

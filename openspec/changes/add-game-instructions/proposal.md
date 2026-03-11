## Why

New users have no way to understand how to play the puzzle or interact with the app. There are no instructions, tooltips, or help text explaining the goal (tile all cells except the selected date), the controls (drag-and-drop, click-to-remove, rotate, solve), or the puzzle rules. This makes the app unintuitive for first-time visitors.

## What Changes

- Add a concise "How to Play" instructions panel to the UI that explains the puzzle goal, available controls, and interaction patterns
- The instructions should be collapsible so they don't dominate screen space for returning users

## Capabilities

### New Capabilities
- `game-instructions-panel`: A collapsible instructions panel in the UI that explains the puzzle goal, controls, and interaction patterns

### Modified Capabilities

## Impact

- **Frontend only** — no backend/API changes required
- **New component**: `Instructions.js` + `Instructions.css`
- **App.js** — integrate the instructions component into the layout
- **App.css** — minor layout adjustments if needed

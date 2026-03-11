## ADDED Requirements

### Requirement: Instructions panel displays puzzle goal and controls
The system SHALL display an instructions panel that explains the puzzle goal ("cover all cells except the selected date using the 9 puzzle pieces") and lists all available controls (drag-and-drop placement, click-to-remove, spacebar rotate, Clear Board, Solve).

#### Scenario: First visit shows expanded instructions
- **WHEN** a user visits the app for the first time (no localStorage value)
- **THEN** the instructions panel SHALL be displayed in an expanded state showing the full instructions text

#### Scenario: Instructions content covers all interactions
- **WHEN** the instructions panel is expanded
- **THEN** it SHALL display the puzzle goal and at minimum: how to place pieces (drag), how to remove pieces (click on board), how to rotate pieces (spacebar), and how to auto-solve (Solve button)

### Requirement: Instructions panel is collapsible
The system SHALL allow the user to toggle the instructions panel between expanded and collapsed states by clicking the panel header.

#### Scenario: Collapse instructions
- **WHEN** the user clicks the instructions panel header while it is expanded
- **THEN** the instructions body SHALL be hidden and only the header remains visible

#### Scenario: Expand instructions
- **WHEN** the user clicks the instructions panel header while it is collapsed
- **THEN** the instructions body SHALL be shown with the full instructions text

### Requirement: Collapsed state persists across page loads
The system SHALL persist the instructions panel collapsed/expanded state in localStorage so the user's preference survives page reloads.

#### Scenario: User collapses and reloads
- **WHEN** the user collapses the instructions panel and reloads the page
- **THEN** the instructions panel SHALL remain collapsed

#### Scenario: User expands and reloads
- **WHEN** the user expands the instructions panel and reloads the page
- **THEN** the instructions panel SHALL remain expanded

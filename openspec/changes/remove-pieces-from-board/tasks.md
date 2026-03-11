## 1. Board Piece Removal — Click Handler

- [x] 1.1 Add `handleBoardCellClick(row, col)` function in `App.js` that looks up the clicked cell in the `solution` array and calls `handlePieceRestore(pieceId)` if a piece is found
- [x] 1.2 Pass `onCellClick` handler from `App.js` to `Board.js` as a prop
- [x] 1.3 Attach `onClick` handler to filled board cells in `Board.js`, calling `onCellClick(row, col)`

## 2. Board Piece Removal — Hover Affordance

- [x] 2.1 Add CSS hover styles in `Board.css` for placed-piece cells: brightness filter and pointer cursor
- [x] 2.2 Add a CSS class (e.g., `placed-piece`) to board cells that contain a placed piece in `Board.js`

## 3. Clear Board Button

- [x] 3.1 Add `handleClearBoard()` function in `App.js` that resets `solution` to empty and sets all `pieces[].used` to `false`
- [x] 3.2 Add "Clear Board" button in the UI next to the "Solve" button, disabled when `solution` is empty
- [x] 3.3 Style the "Clear Board" button in `App.css` to match the existing button design

## 4. Verification

- [x] 4.1 Test click-to-remove: place a piece manually, click one of its cells, verify piece returns to pieces list and cells clear
- [x] 4.2 Test hover affordance: verify pointer cursor and brightness change on placed piece cells only
- [x] 4.3 Test clear board: place multiple pieces, click "Clear Board", verify all pieces return and board resets
- [x] 4.4 Test clear board disabled state: verify button is disabled when no pieces are placed
- [x] 4.5 Test that clicking empty, target, and off-board cells does nothing

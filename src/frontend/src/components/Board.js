import React from 'react';
import './Board.css';

/**
 * boardData: array of objects { row: number, col: number, label: string }
 * solution: array of placements { pieceId, cells: [{row, col}] }
 */
const Board = ({ boardData, solution }) => {
  if (!boardData.length) return <div className="board">Loading board...</div>;

  // Determine dimensions
  const rows = Math.max(...boardData.map(c => c.row)) + 1;
  const cols = Math.max(...boardData.map(c => c.col)) + 1;

  // Create a map for quick lookup
  const cellMap = {};
  boardData.forEach(cell => {
    cellMap[`${cell.row}-${cell.col}`] = cell;
  });

  // Create a map of solution assignments: key "row-col" to pieceId
  const assignment = {};
  solution.forEach(({ pieceId, cells }) => {
    cells.forEach(({ row, col }) => {
      assignment[`${row}-${col}`] = pieceId;
      console.log(`Assigning piece ${pieceId} to cell ${row}-${col}`);
    });
  });

  return (
    <div
      className="board"
      style={{
        display: 'grid',
        gridTemplateRows: `repeat(${rows}, 40px)`,
        gridTemplateColumns: `repeat(${cols}, 40px)`,
        gap: '0px',
      }}
    >
      {Array.from({ length: rows }).map((_, r) =>
        Array.from({ length: cols }).map((_, c) => {
          const key = `${r}-${c}`;
          const cell = cellMap[key];
          const pieceId = assignment[key];
          const isFilled = pieceId !== undefined && pieceId !== null;

          // If no cell exists in this grid position, render an empty-space placeholder
          if (!cell) {
            return <div key={key} className="empty-cell" />;
          }

          // Determine border sides
          let borderTop, borderRight, borderBottom, borderLeft;

          if (isFilled) {
            // For filled cells, only draw borders where neighbor is different or missing
            const upKey = `${r - 1}-${c}`;
            const downKey = `${r + 1}-${c}`;
            const leftKey = `${r}-${c - 1}`;
            const rightKey = `${r}-${c + 1}`;

            const upSame = assignment[upKey] === pieceId;
            const downSame = assignment[downKey] === pieceId;
            const leftSame = assignment[leftKey] === pieceId;
            const rightSame = assignment[rightKey] === pieceId;

            borderTop = upSame ? 'none' : '2px solid black';
            borderBottom = downSame ? 'none' : '2px solid black';
            borderLeft = leftSame ? 'none' : '2px solid black';
            borderRight = rightSame ? 'none' : '2px solid black';
          } else {
            // For unfilled cells that exist on the board, use a light gray border
            borderTop = borderBottom = borderLeft = borderRight = '1px solid #999';
          }

          return (
            <div
              key={key}
              className="cell"
              style={{
                backgroundColor: isFilled
                  ? `hsl(${(pieceId * 40) % 360}, 70%, 80%)`
                  : '#fff',
                borderTop,
                borderRight,
                borderBottom,
                borderLeft,
              }}
            >
              <span className="cell-label">{cell.label}</span>
            </div>
          );
        })
      )}
    </div>
  );
};


export default Board;

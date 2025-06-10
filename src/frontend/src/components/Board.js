import React from 'react';
import './Board.css';

/**
 * boardData: array of objects { row: number, col: number, label: string }
 * solution: array of placements { pieceId, cells: [{row, col}] }
 * targetCells: array of { row, col }
 */
const Board = ({ boardData, solution, targetCells = [] }) => {
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
    });
  });

  // Create a set for fast target lookup
  const targetSet = new Set(targetCells.map(tc => `${tc.row}-${tc.col}`));

  return (
    <div
      className="board"
      style={{
        display: 'grid',
        gridTemplateRows: `repeat(${rows}, 60px)`,
        gridTemplateColumns: `repeat(${cols}, 60px)`,
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
          if (!cell || !cell.label) {
            return (
              <div
                key={key}
                className="empty-cell"
                style={{
                  backgroundColor: '#e0e0e0',
                  border: '1px solid #bbb',
                  width: '60px',
                  height: '60px',
                }}
              />
            );
          }

          //console.log(`Rendering cell at ${key}:`, cell);

          // Is this cell a target?
          //const isTarget = true if cell.state === 'TARGET' //|| targetSet.has(key);
            const isTarget = cell.state === 'TARGET' || targetSet.has(key);
            if (isTarget) {
            //console.log(`Cell ${key} is a target with state: ${cell.state}`);
            }
        
          //const isTarget = targetSet.has(key) || cell.state === 'TARGET';
          //const isTarget = true

          // Determine border sides
          let borderTop, borderRight, borderBottom, borderLeft;

          if (isFilled) {
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
            borderTop = borderBottom = borderLeft = borderRight = '1px solid #999';
          }

          return (
            <div
              key={key}
              className="cell"
              style={{
                backgroundColor: isTarget
                  ? '#ff3b3b'
                  : isFilled
                  ? `hsl(${(pieceId * 40) % 360}, 70%, 50%)`
                  : '#fff',
                borderTop,
                borderRight,
                borderBottom,
                borderLeft,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <span
                className="cell-label"
                style={
                  isTarget
                    ? { color: '#111', fontWeight: 'bold', fontSize: '1.1em' }
                    : {}
                }
              >
                {cell.label}
              </span>
            </div>
          );
        })
      )}
    </div>
  );
};

export default Board;

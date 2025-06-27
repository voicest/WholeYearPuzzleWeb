import React, { useState } from 'react';
import './Board.css';

/**
 * boardData: array of objects { row: number, col: number, label: string }
 * solution: array of placements { pieceId, cells: [{row, col}] }
 * targetCells: array of { row, col }
 */
const Board = ({ boardData, solution, targetCells = [], onDrop }) => {
  const [hoveredCell, setHoveredCell] = useState(null); // Track the currently hovered cell

  if (!boardData.length) return <div className="board">Loading board...</div>;

  // Determine dimensions
  const rows = Math.max(...boardData.map((c) => c.row)) + 1;
  const cols = Math.max(...boardData.map((c) => c.col)) + 1;

  // Create a map for quick lookup
  const cellMap = {};
  boardData.forEach((cell) => {
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
  const targetSet = new Set(targetCells.map((tc) => `${tc.row}-${tc.col}`));

  const handleDrop = (e, row, col) => {
    e.preventDefault();
    setHoveredCell(null); // Clear the hovered cell
    onDrop(row, col);
  };

  const handleDragOver = (e, row, col) => {
    e.preventDefault();
    setHoveredCell(`${row}-${col}`); // Update the hovered cell
  };

  const handleDragLeave = () => {
    setHoveredCell(null); // Clear the hovered cell when dragging leaves
  };


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
          const isHovered = hoveredCell === key;

          //console.log(`pieceId: ${pieceId}, hoveredCell: ${hoveredCell}, key: ${key}`);

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

          const isTarget = cell.state === 'TARGET' || targetSet.has(key);
        
          //Determine border sides
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
           
            //If the downKey is the edge of the board I want to draw a thicker border
            //console.log(`${r}-${c} ${rows}-${cols} downKey: ${downKey}, leftKey: ${leftKey}, rightKey: ${rightKey}`);

            let topSize =2;
            let downSize = 2;
            let leftSize = 2;
            let rightSize = 2;
            

            if (downKey.startsWith(`${rows}-`)) {
              downSize = 4;

            } 
            
            if (upKey.startsWith('-')) {
              topSize = 4;
          
            } 
            
            if (leftKey.endsWith('-0')) {
              leftSize =4
            } 
            
            if (rightKey.endsWith(`-${cols - 1}`)) {
              rightSize = 4;
            }
            
            borderTop = upSame ? 'none' : `${topSize}px solid black`;
            borderBottom = downSame ? 'none' : `${downSize}px solid black`;
            borderLeft = leftSame ? 'none' : `${leftSize}px solid black`;
            borderRight =  rightSame ? 'none' : `${rightSize}px solid black`;
             
          } else {

            borderTop = borderBottom = borderLeft = borderRight = '1px solid #999';
          }

          if (isTarget) {
            //IF the cell next to the target is filled, make the border thicker
            const upKey = `${r - 1}-${c}`;
            const downKey = `${r + 1}-${c}`;
            const leftKey = `${r}-${c - 1}`;
            const rightKey = `${r}-${c + 1}`;     
            const upFilled = assignment[upKey] !== undefined;
            const downFilled = assignment[downKey] !== undefined;
            const leftFilled = assignment[leftKey] !== undefined;
            const rightFilled = assignment[rightKey] !== undefined;
            if (upFilled) borderTop = '2px solid black';
            if (downFilled) borderBottom = '2px solid black';
            if (leftFilled) borderLeft = '2px solid black';
            if (rightFilled) borderRight = '2px solid black';
          }


          return (
            <div
              key={key}
              className={`cell ${cell.state === 'FILLED' ? 'filled-cell' : ''} ${
                isHovered ? 'hovered-cell' : ''
              }`}
              style={{
                backgroundColor: isTarget
                  ? '#ff3b3b'
                  : isHovered
                  ? '#ffcccb'
                  : isFilled
                  ? `hsl(${(pieceId * 40) % 360}, 70%, 80%)`
                  : '#fff',
                borderTop,
                borderRight,
                borderBottom,
                borderLeft,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
              onDrop={(e) => {
                e.preventDefault();
                setHoveredCell(null);
                if (onDrop) onDrop(r, c);
              }}
              onDragOver={(e) => {
                e.preventDefault();
                setHoveredCell(`${r}-${c}`);
              }}
              onDragLeave={handleDragLeave}
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

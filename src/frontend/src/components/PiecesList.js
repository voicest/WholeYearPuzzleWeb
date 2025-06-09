import React from 'react';
import './PiecesList.css';

/**
 * pieces: array of objects { id: number, name: string, shape: array of {row, col} }
 */
const PiecesList = ({ pieces }) => {
  if (!pieces || !pieces.length) return <div className="pieces-list">Loading pieces...</div>;

  return (
    <div className="pieces-list">
      <h2>Available Pieces</h2>
      <ul>
        {pieces.map(piece => (
          <li key={piece.id} className="piece-item">
            <strong>{piece.name}</strong>
            <div className="piece-shape">
              {renderShape(piece.shape,piece.id)}
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};

function renderShape(shape, id) {
  // If shape is undefined or empty, show placeholder
  if (!shape || !shape.length) {
    return <div className="empty-shape">No shape defined</div>;
  }

  // Assume shape is array of {row, col}. Normalize to grid
  const rows = Math.max(...shape.map(c => c.row)) - Math.min(...shape.map(c => c.row)) + 1;
  const cols = Math.max(...shape.map(c => c.col)) - Math.min(...shape.map(c => c.col)) + 1;
  const minRow = Math.min(...shape.map(c => c.row));
  const minCol = Math.min(...shape.map(c => c.col));
  const grid = Array.from({ length: rows }).map(() => Array(cols).fill(false));
  shape.forEach(({ row, col }) => {
    grid[row - minRow][col - minCol] = true;
  });

  return (
    <div
      style={{
        display: 'grid',
        gridTemplateRows: `repeat(${rows}, 20px)`,
        gridTemplateColumns: `repeat(${cols}, 20px)`,
        gap: '1px',
      }}
    >
      {grid.flat().map((filled, idx) => (
        <div
          key={idx}
          style={{
            width: '20px',
            height: '20px',
            backgroundColor: filled ? `hsl(${(id * 40) % 360}, 70%, 80%)`
            : 'transparent',
            border: filled ? '1px solid #333' : '1px dashed #ccc',
          }}
        />
      ))}
    </div>
  );
}

export default PiecesList;

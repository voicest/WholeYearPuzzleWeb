import React, { useState, useEffect } from 'react';
import './PiecesList.css';

/**
 * pieces: array of objects { id: number, name: string, shape: array of {row, col} }
 */
const PiecesList = ({ pieces, selectedPiece, setSelectedPiece, setDraggedPiece, rotatePiece }) => {
  useEffect(() => {
    const handleKeyDown = (event) => {
      if (event.code === 'Space' && selectedPiece) {
        event.preventDefault();
        rotatePiece(selectedPiece.id); // <-- Use the parent function
      }
      if (event.code === 'Escape' || event.code === 'KeyR') {
        setSelectedPiece(null);
      }
    };
    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [selectedPiece, setSelectedPiece, rotatePiece]);

  const handleSelectPiece = (piece) => setSelectedPiece(piece);

  if (!pieces || !pieces.length) return <div className="pieces-list">Loading pieces...</div>;
  return (
    <div className="pieces-list-container">
      <h2 className="pieces-list-title">Available Pieces</h2>
      <div className="pieces-grid">
        {pieces.map((piece) => {
          const isSelected = selectedPiece?.id === piece.id;
          // Use the rotated shape if selected, otherwise the original
          const pieceToRender = isSelected ? selectedPiece : piece;
          return (
            <div
              className={`piece-card ${isSelected ? 'selected-piece' : ''}`}
              key={piece.id}
              onClick={() => handleSelectPiece(piece)}
              draggable
              onDragStart={() => setDraggedPiece && setDraggedPiece(pieceToRender)}
              onDragEnd={() => setDraggedPiece && setDraggedPiece(null)}
            >
              {renderShape(pieceToRender.shape, pieceToRender.id)}
            </div>
          );
        })}
      </div>
    </div>
  );
};

function renderShape(shape, id) {
  if (!shape || shape.length === 0) {
    console.error('Invalid shape:', shape); // Debug: Log invalid shape
    return <div>No shape available</div>;
  }

  const rows = Math.max(...shape.map((c) => c.row)) + 1;
  const cols = Math.max(...shape.map((c) => c.col)) + 1;

  // Ensure rows and cols are valid
  if (rows <= 0 || cols <= 0) {
    console.error('Invalid grid dimensions:', { rows, cols }); // Debug: Log invalid dimensions
    return <div>Invalid grid dimensions</div>;
  }

  const grid = Array.from({ length: rows }, () => Array(cols).fill(false));
  shape.forEach(({ row, col }) => {
    if (grid[row] && grid[row][col] !== undefined) {
      grid[row][col] = true;
    } else {
      console.error('Invalid cell coordinates:', { row, col }); // Debug: Log invalid cell coordinates
    }
  });

  return (
    <div
      style={{
        display: 'grid',
        gridTemplateRows: `repeat(${rows}, 20px)`,
        gridTemplateColumns: `repeat(${cols}, 20px)`,
        gap: '2px',
      }}
    >
      {grid.flat().map((filled, idx) => (
        <div
          key={idx}
          style={{
            width: '20px',
            height: '20px',
            backgroundColor: filled ? `hsl(${(id * 40) % 360}, 70%, 80%)` : 'transparent',
            border: filled ? '1px solid #333' : 'none',
          }}
        />
      ))}
    </div>
  );
}

export default PiecesList;

import React, { useState, useEffect } from 'react';
import './PiecesList.css';

/**
 * pieces: array of objects { id: number, name: string, shape: array of {row, col} }
 */
const PiecesList = ({ pieces }) => {
  const [selectedPiece, setSelectedPiece] = useState(null); // Track the currently selected piece

  useEffect(() => {
    const handleKeyDown = (event) => {
      if (event.code === 'Space' && selectedPiece) {
        event.preventDefault(); // Prevent default space bar behavior
        console.log('Rotating piece:', selectedPiece.id); // Debug: Log the selected piece ID

      /**
      * Rotate a shape (list of Cells) 90° clockwise around (0,0)
      * given that the shape’s bounding‐box is (height × width).
      * NewRow = oldCol; NewCol = (height-1) - oldRow.
      */

      const height = Math.max(...selectedPiece.shape.map(c => c.row)) + 1; // Calculate height
      const width = Math.max(...selectedPiece.shape.map(c => c.col)) + 1; // Calculate width
      if (height > 0 && width > 0) {
        console.log('Shape dimensions:', { height, width }); // Debug: Log the shape dimensions
        // Map the shape to new coordinates after rotation
        const rotatedShape = selectedPiece.shape.map(({ row, col }) => ({
          row: col, // New row is the old column
          col: (height - 1) - row, // New column is (height - 1) - old row
        }));
        console.log('Rotated shape:', rotatedShape); // Debug: Log the rotated shape
        setSelectedPiece({ ...selectedPiece, shape: rotatedShape }); // Update the selected piece's shape
      } else {
        console.error('Invalid shape dimensions:', { height, width }); // Debug: Log invalid dimensions
        // If the shape dimensions are invalid, we can either skip rotation or handle it gracefully
        // For now, we will just log an error and not update the shape
        console.error('Cannot rotate shape with invalid dimensions:', selectedPiece.shape);
        return; 
      }
      }
      if (event.code === 'Escape') {
        console.log('Deselecting piece'); // Debug: Log deselection
        setSelectedPiece(null); // Deselect the piece
      }
      if (event.code === 'KeyR') {
        console.log('Resetting piece'); // Debug: Log reset action
        setSelectedPiece(null); // Reset the selected piece
      }
 

    };

    // Attach the keydown listener to the document
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [selectedPiece]);

  const handleSelectPiece = (piece) => {
    console.log('Selected piece:', piece); // Debug: Log the selected piece
    setSelectedPiece(piece); // Set the selected piece
  };

  if (!pieces || !pieces.length) return <div className="pieces-list">Loading pieces...</div>;
  return (
    <div className="pieces-list-container">
      <h2 className="pieces-list-title">Available Pieces</h2>
      <div className="pieces-grid">
        {pieces.map((piece) => (
          <div
            className={`piece-card ${selectedPiece?.id === piece.id ? 'selected-piece' : ''}`}
            key={piece.id}
            onClick={() => handleSelectPiece(piece)} // Select the piece on click
          >
            {selectedPiece?.id === piece.id
              ? renderShape(selectedPiece.shape, selectedPiece.id) // Render updated shape for selected piece
              : renderShape(piece.shape, piece.id)}
          </div>
        ))}
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

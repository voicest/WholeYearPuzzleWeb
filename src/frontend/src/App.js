import React, { useEffect, useState } from 'react';
import Board from './components/Board';
import PiecesList from './components/PiecesList';
import './App.css';

function App() {
  const [boardData, setBoardData] = useState([]);
  const [pieces, setPieces] = useState([]);
  const [draggedPiece, setDraggedPiece] = useState(null);
  const [solution, setSolution] = useState([]);
  const [loading, setLoading] = useState(false);
  const [targetDate, setTargetDate] = useState(new Date().toISOString().split('T')[0]); // Default to today
  const [selectedPiece, setSelectedPiece] = useState(null);

  // Fetch board and pieces definitions on mount
  useEffect(() => {
    async function fetchData() {
      try {
        const boardRes = await fetch('/api/board');
        if (!boardRes.ok) {
          throw new Error(`HTTP error! status: ${boardRes.status}`);
        }
        const boardJson = await boardRes.json();
        setBoardData(boardJson);

        const piecesRes = await fetch('/api/pieces');
        const piecesJson = await piecesRes.json();
        setPieces(piecesJson);
      } catch (err) {
        console.error('Error fetching data:', err);
      }
    }
    fetchData();
  }, []);

  // Trigger solve
  const handleSolve = async () => {
    setLoading(true);
    try {
      const res = await fetch('/api/solve', { method: 'POST' });
      const solJson = await res.json();
      setSolution(solJson);
    } catch (err) {
      console.error('Error solving puzzle:', err);
    }
    setLoading(false);
  };

  // Handle date change
  const handleDateChange = async (event) => {
    //reset the solution when the date changes
    setSolution([]);
    const selectedDate = event.target.value;
    setTargetDate(selectedDate);

    try {
      await fetch('/api/updateTargetDate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `date=${selectedDate}`,
      });
      // Refetch board data after updating the target date
      const boardRes = await fetch('/api/board');
      const boardJson = await boardRes.json();
      setBoardData(boardJson);
    } catch (err) {
      console.error('Error updating target date:', err);
    }
  };

  const handleDragStart = (piece) => {
    setDraggedPiece(piece);
  };

  // Handle drop on the board
  const handleDropOnBoard = (row, col) => {
    if (!draggedPiece) return;

    // Calculate new filled cells based on piece shape and drop location
    const newCells = draggedPiece.shape.map(({ row: r, col: c }) => ({
      row: row + r,
      col: col + c,
    }));

    // Check for overlap with already filled cells
    const filledCells = new Set();
    solution.forEach(({ cells }) => {
      cells.forEach(({ row, col }) => {
        filledCells.add(`${row}-${col}`);
      });
    });

    // Check for overlap with target cells as well
    const targetCellSet = new Set();
    boardData.forEach(cell => {
      if (cell.state === 'TARGET') {
        targetCellSet.add(`${cell.row}-${cell.col}`);
      }
    });

    // Check that all cells are on the board (i.e., exist in boardData and are not OFF_BOARD)
    const boardCellSet = new Set(
      boardData.filter(cell => cell.state !== 'OFF_BOARD').map(cell => `${cell.row}-${cell.col}`)
    );
    const allOnBoard = newCells.every(({ row, col }) => boardCellSet.has(`${row}-${col}`));
    if (!allOnBoard) {
      alert('Cannot place piece: part of the piece is off the board.');
      setDraggedPiece(null);
      return;
    }

    const willOverlap = newCells.some(({ row, col }) =>
      filledCells.has(`${row}-${col}`) || targetCellSet.has(`${row}-${col}`)
    );
    if (willOverlap) {
      alert('Cannot place piece: it overlaps with already filled spaces or target cells.');
      setDraggedPiece(null);
      return;
    }

    // Mark the piece as used
    setPieces((prevPieces) =>
      prevPieces.map((piece) =>
        piece.id === draggedPiece.id ? { ...piece, used: true } : piece
      )
    );

    const newPlacement = {
      pieceId: draggedPiece.id,
      cells: newCells,
    };

    setSolution((prev) => [...prev, newPlacement]);
    setDraggedPiece(null);
  };

  // Rotate a piece by id
  const rotatePiece = (pieceId) => {
    setPieces((prevPieces) =>
      prevPieces.map((piece) => {
        if (piece.id !== pieceId) return piece;
        const height = Math.max(...piece.shape.map(c => c.row)) + 1;
        const rotatedShape = piece.shape.map(({ row, col }) => ({
          row: col,
          col: (height - 1) - row,
        }));
        // If this piece is selected, update selectedPiece as well
        if (selectedPiece && selectedPiece.id === pieceId) {
          setSelectedPiece({ ...piece, shape: rotatedShape });
        }
        return { ...piece, shape: rotatedShape };
      })
    );
  };

  // Add this function to App.js
  const handlePieceRestore = (pieceId) => {
    // Remove the piece's placement from the solution
    setSolution((prev) => prev.filter((placement) => placement.pieceId !== pieceId));
    // Mark the piece as available again
    setPieces((prevPieces) =>
      prevPieces.map((piece) =>
        piece.id === pieceId ? { ...piece, used: false } : piece
      )
    );
  };

  return (
    <div className="app-root">
      <header className="app-header">
        <h1>Whole Year Puzzle Solver</h1>
      </header>
      <main className="app-main">
        <div className="workspace-card">
          <Board
            boardData={boardData}
            solution={solution}
            onDrop={handleDropOnBoard}
            draggedPiece={draggedPiece}
          />
          <div className="workspace-column">
            <div className="date-picker-container">
              <label htmlFor="target-date-picker">Select Solve Date:</label>
              <input
                type="date"
                id="target-date-picker"
                value={targetDate}
                onChange={handleDateChange}
                className="date-picker"
              />
            </div>
            <PiecesList
              pieces={pieces}
              selectedPiece={selectedPiece}
              setSelectedPiece={setSelectedPiece}
              setDraggedPiece={setDraggedPiece}
              rotatePiece={rotatePiece}
              handlePieceRestore={handlePieceRestore}
            />
          </div>
        </div>
        <button onClick={handleSolve} disabled={loading} className="solve-button">
          {loading ? 'Solving...' : 'Solve Puzzle'}
        </button>
      </main>
    </div>
  );
}

export default App;

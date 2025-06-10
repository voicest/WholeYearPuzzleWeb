import React, { useEffect, useState } from 'react';
import Board from './components/Board';
import PiecesList from './components/PiecesList';
import './App.css';

function App() {
  const [boardData, setBoardData] = useState([]);
  const [pieces, setPieces] = useState([]);
  const [solution, setSolution] = useState([]);
  const [loading, setLoading] = useState(false);
  const [targetDate, setTargetDate] = useState(new Date().toISOString().split('T')[0]); // Default to today

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

  return (
    <div className="app-root">
      <header className="app-header">
        <h1>Whole Year Puzzle Solver</h1>
      </header>
      <main className="app-main">
        <div className="workspace-card">
          <div className="workspace-column">
            <Board boardData={boardData} solution={solution} />
          </div>
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
            <PiecesList pieces={pieces} />
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

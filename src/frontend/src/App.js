import React, { useEffect, useState } from 'react';
import Board from './components/Board';
import PiecesList from './components/PiecesList';
import './App.css';

function App() {
  const [boardData, setBoardData] = useState([]);
  const [pieces, setPieces] = useState([]);
  const [solution, setSolution] = useState([]);
  const [loading, setLoading] = useState(false);

  // Fetch board and pieces definitions on mount
  useEffect(() => {
    async function fetchData() {
      try {
        console.log('Fetching board and pieces data...');
        const boardRes = await fetch('/api/board');
        console.log('Board response:', boardRes);
        if (!boardRes.ok) {
          throw new Error(`HTTP error! status: ${boardRes.status}`);
        }
        console.log('Board data fetched successfully');
        const boardJson = await boardRes.json();
        console.log('Board JSON:', boardJson);
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
      console.log('Solution JSON:', solJson);
      setSolution(solJson);
    } catch (err) {
      console.error('Error solving puzzle:', err);
    }
    setLoading(false);
  };

  return (
    <div className="app-root">
      <header className="app-header">
        <h1>Whole Year Puzzle Solver</h1>
      </header>
      <main className="app-main">
        <div className="workspace-card">
          <Board boardData={boardData} solution={solution} />
          <PiecesList pieces={pieces} />
        </div>
        <button onClick={handleSolve} disabled={loading} className="solve-button">
          {loading ? 'Solving...' : 'Solve Puzzle'}
        </button>
      </main>
    </div>
  );
}

export default App;

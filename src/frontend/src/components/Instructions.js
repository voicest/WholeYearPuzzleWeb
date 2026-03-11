import React, { useState } from 'react';
import './Instructions.css';

const STORAGE_KEY = 'puzzleInstructionsCollapsed';

function Instructions() {
  const [collapsed, setCollapsed] = useState(() => {
    return localStorage.getItem(STORAGE_KEY) === 'true';
  });

  const toggle = () => {
    setCollapsed((prev) => {
      const next = !prev;
      localStorage.setItem(STORAGE_KEY, String(next));
      return next;
    });
  };

  return (
    <div className={`instructions-panel ${collapsed ? 'collapsed' : ''}`}>
      <button className="instructions-header" onClick={toggle} aria-expanded={!collapsed}>
        <span className="instructions-title">How to Play</span>
        <span className="instructions-toggle">{collapsed ? '▸' : '▾'}</span>
      </button>
      {!collapsed && (
        <div className="instructions-body">
          <p className="instructions-goal">
            <strong>Goal:</strong> Cover every cell on the board <em>except</em> the
            two highlighted date cells using all 9 puzzle pieces.
          </p>
          <ul className="instructions-controls">
            <li><strong>Place a piece</strong> — drag it from the sidebar onto the board</li>
            <li><strong>Remove a piece</strong> — click on it on the board</li>
            <li><strong>Rotate a piece</strong> — select it then press <kbd>Space</kbd></li>
            <li><strong>Clear Board</strong> — remove all placed pieces at once</li>
            <li><strong>Solve</strong> — let the computer find a solution automatically</li>
          </ul>
        </div>
      )}
    </div>
  );
}

export default Instructions;

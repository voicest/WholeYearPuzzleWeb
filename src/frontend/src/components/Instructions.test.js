import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import Instructions from './Instructions';

const STORAGE_KEY = 'puzzleInstructionsCollapsed';

beforeEach(() => {
  localStorage.clear();
});

test('renders expanded by default on first visit', () => {
  render(<Instructions />);
  expect(screen.getByText('How to Play')).toBeInTheDocument();
  expect(screen.getByText(/Cover every cell/)).toBeInTheDocument();
  expect(screen.getByText(/Place a piece/)).toBeInTheDocument();
  expect(screen.getByText(/Remove a piece/)).toBeInTheDocument();
  expect(screen.getByText(/Rotate a piece/)).toBeInTheDocument();
  expect(screen.getByText(/Clear Board/)).toBeInTheDocument();
  expect(screen.getByText(/let the computer find a solution/)).toBeInTheDocument();
});

test('collapses when header is clicked', () => {
  render(<Instructions />);
  fireEvent.click(screen.getByText('How to Play'));
  expect(screen.queryByText(/Cover every cell/)).not.toBeInTheDocument();
});

test('expands again when header is clicked twice', () => {
  render(<Instructions />);
  const header = screen.getByText('How to Play');
  fireEvent.click(header);
  fireEvent.click(header);
  expect(screen.getByText(/Cover every cell/)).toBeInTheDocument();
});

test('persists collapsed state to localStorage', () => {
  render(<Instructions />);
  fireEvent.click(screen.getByText('How to Play'));
  expect(localStorage.getItem(STORAGE_KEY)).toBe('true');
});

test('restores collapsed state from localStorage', () => {
  localStorage.setItem(STORAGE_KEY, 'true');
  render(<Instructions />);
  expect(screen.queryByText(/Cover every cell/)).not.toBeInTheDocument();
});

test('restores expanded state from localStorage', () => {
  localStorage.setItem(STORAGE_KEY, 'false');
  render(<Instructions />);
  expect(screen.getByText(/Cover every cell/)).toBeInTheDocument();
});

test('header has correct aria-expanded attribute', () => {
  render(<Instructions />);
  const header = screen.getByRole('button', { name: /How to Play/ });
  expect(header).toHaveAttribute('aria-expanded', 'true');
  fireEvent.click(header);
  expect(header).toHaveAttribute('aria-expanded', 'false');
});

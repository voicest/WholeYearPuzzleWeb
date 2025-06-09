package com.wholeyear.util;


import com.wholeyear.model.Cell;

import java.util.Collections;
import java.util.List;

/**
 * Represents one way of placing a given Piece on the Board.
 * Stores:
 * • pieceId (so we know which piece it is)
 * • orientationIdx (which orientation from Piece.generateAllOrientations())
 * • anchorRow/Col (the board‐coordinate where the piece’s (0,0) lands)
 * • coveredCells (absolute Board cells that this placement occupies)
 * 
 * A Placement is just a data object that describes “one possible way” to put a
 * specific piece onto the board—i.e. which piece it is, which orientation it’s
 * in, where its top‐left anchor sits, and precisely which board cells it would
 * cover. It doesn’t actually modify the board; it simply holds the coordinates
 * and metadata.
 * 
 * By contrast, your PiecePlacer class is a utility that takes a chosen
 * Placement (or equivalent orientation + anchor) and actively “applies”
 * it—calling board.block(r,c) on each covered cell (and telling the BoardPanel
 * to redraw with a bold outline). In other words:
 * 
 * Placement
 *      Purely a model: { pieceId, orientationIdx, anchorRow, anchorCol, coveredCells }
 *      Used when building your exact‐cover matrix (each Placement → one row in DLX).
 *      Immutable, doesn’t touch the board or UI.
 * 
 * PiecePlacer
 *      A helper/action class. Given a board, a panel, and a particular piece + orientation + anchor, it:
 *      Computes the absolute (r,c) cells (same info that Placement already contains).
 *      Calls board.block(r,c) on each cell (so future placements can’t overlap).
 *      Calls boardPanel.addPlacedPiece(...) to draw the bold outline and repaint.
 * 
 * Put simply:
 * 
 * You generate a bunch of Placement instances (one per “legal way” to position each piece).
 * The DLX solver picks a subset of those Placement objects that exactly covers all fillable cells.
 * Once you know which Placements form the solution, you hand each one to PiecePlacer.placePieceOnBoard(...), 
 * which actually blocks those cells in your Board and tells your BoardPanel to show them.
 */

 public class Placement {
    private final String pieceId;
    private final int orientationIdx;
    private final int anchorRow, anchorCol;
    private final List<Cell> coveredCells;

    public Placement(String pieceId, int orientationIdx, int anchorRow, int anchorCol, List<Cell> coveredCells) {
        this.pieceId = pieceId;
        this.orientationIdx = orientationIdx;
        this.anchorRow = anchorRow;
        this.anchorCol = anchorCol;
        this.coveredCells = Collections.unmodifiableList(coveredCells);
    }

    public String getPieceId() {
        return pieceId;
    }

    public int getOrientationIdx() {
        return orientationIdx;
    }

    public int getAnchorRow() {
        return anchorRow;
    }

    public int getAnchorCol() {
        return anchorCol;
    }

    public List<Cell> getCoveredCells() {
        return coveredCells;
    }

    @Override
    public String toString() {
        return String.format(
                "%s @ (%d,%d) ori=%d covers %s",
                pieceId, anchorRow, anchorCol, orientationIdx, coveredCells);
    }
}

package com.wholeyear.solver;

import com.wholeyear.model.Piece;
import com.wholeyear.util.Defintion;
import com.wholeyear.model.Board;
import com.wholeyear.util.Solver;
import com.wholeyear.util.Placement;
import com.wholeyear.model.BoardCell;
import com.wholeyear.model.Cell;
import com.wholeyear.model.PieceDto;
import com.wholeyear.model.Board.CellState;
import com.wholeyear.util.PlacementDto;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class SolverController {
    private final List<Piece> pieces;
    private final Board board;
    //Create a map of peice id to index
    private final Map<String, Integer> pieceIdToIndexMap = new HashMap<>();
    private final List<Cell> targetCells = new ArrayList<>();


    public SolverController() {
        this.pieces = Defintion.loadAllPieces();
        this.board = Defintion.createWholeYearPuzzleBoard();

        //Calculate todays date and month in short format
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String monthLabel = months[month - 1]; // Convert to 3-letter month format
        System.out.println("Today's date: " + day + " " + monthLabel);
        //Set the target cells based on today's date
        //Reset the board


        //Set the target cells for the board
        Cell targetCell1 = board.findCellByLabel("" + day);
        if (targetCell1 == null) {
            System.out.println("No target cell found for day: " + day);
            return; // Exit if no target cell is found
        }
        Cell targetCell2 = board.findCellByLabel(monthLabel);
        if (targetCell2 == null) {
            System.out.println("No target cell found for month: " + monthLabel);
            return; // Exit if no target cell is found
        }
        
        targetCells.add(targetCell1);
        targetCells.add(targetCell2);

        for (Cell cell : targetCells) {
            //Set the target cells on the board
            this.board.setTarget(cell.getRow(), cell.getCol());
        }
        
    }

    @PostMapping("/solve")
    //@GetMapping("/solve")
    public List<PlacementDto> solve() {

        Solver solver = new com.wholeyear.util.Solver(board, pieces);
        List<Placement> placements = solver.solve();

        if (placements == null || placements.isEmpty()) {
            System.out.println("No solution found.");
            return Collections.emptyList();
        }

        List<PlacementDto> placementDtos = new ArrayList<>();
        
        for (Placement placement : placements) {
            //Get the index of the piece in the placement
            int pieceIndex = pieceIdToIndexMap.get(placement.getPieceId());
            PlacementDto dto = new PlacementDto(pieceIndex, placement.getCoveredCells());
            placementDtos.add(dto);     
        
        }
        return placementDtos;

    }

    @GetMapping(path = "/pieces", produces = "application/json")
    public List<PieceDto> getPieces() {
        List<PieceDto> pieceDtos = new ArrayList<>();
        int i = 0;
        for (Piece piece : pieces) {
            PieceDto dto = new PieceDto(i, piece.getId(), piece.getCanonicalCells());
            //Store the piece id to index mapping
            pieceIdToIndexMap.put(piece.getId(), i);
            //System.out.println("Piece ID: " + piece.getId() + ", Index: " + i);
            pieceDtos.add(dto);
            i++;
        }
        return pieceDtos;
    }

    @GetMapping("/board")
    public List<BoardCell> getBoard() {
        //For each cell on the board, create a BoardCell object
        List<BoardCell> boardCells = new ArrayList<>();
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                String label = board.getLabel(r, c);  
                CellState state = board.getCellState(r, c); 
                BoardCell cell = new BoardCell(r, c, label, state);
                boardCells.add(cell);
            }
        }
        return boardCells;
        
        
    }

    @PostMapping("/updateTargetDate")
    public void updateTargetDate(@RequestParam("date") String date) {
        // Parse the date (assuming format is YYYY-MM-DD)
        String[] dateParts = date.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);

        //convert month to three letter month
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        String monthLabel = months[month - 1];  


        System.out.println("Updating target date to: " + year + "-" + month + "-" + day);

        // Reset the board
        this.board.reset();

        // Update target cells based on the selected date
        targetCells.clear();
        Cell targetCell1 = board.findCellByLabel("" + day);
        if (targetCell1 == null) {
            System.out.println("No target cell found for day: " + day);
            return; // Exit if no target cell is found
        }
        //Cell targetCell1 = new Cell(month - 1, day % board.getCols());
        Cell targetCell2 = board.findCellByLabel(monthLabel);
        if (targetCell2 == null) {
            System.out.println("No target cell found for month: " + monthLabel);
            return; // Exit if no target cell is found
        }
        targetCells.add(targetCell1);
        targetCells.add(targetCell2);

        for (Cell cell : targetCells) {
            this.board.setTarget(cell.getRow(), cell.getCol());
        }
    }
}

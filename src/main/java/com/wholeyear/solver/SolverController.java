package com.wholeyear.solver;

import com.wholeyear.model.Piece;
import com.wholeyear.util.Definition;
import com.wholeyear.model.Board;
import com.wholeyear.util.Solver;
import com.wholeyear.util.Placement;
import com.wholeyear.model.BoardCell;
import com.wholeyear.model.Cell;
import com.wholeyear.model.PieceDto;
import com.wholeyear.util.PlacementDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api")
public class SolverController {
    private static final Logger log = LoggerFactory.getLogger(SolverController.class);

    private static final String[] MONTHS = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private final List<Piece> pieces;
    private final Board board;
    private final Map<String, Integer> pieceIdToIndexMap;

    public SolverController() {
        this.pieces = Definition.loadAllPieces();
        this.board = Definition.createWholeYearPuzzleBoard();

        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < pieces.size(); i++) {
            map.put(pieces.get(i).getId(), i);
        }
        this.pieceIdToIndexMap = Collections.unmodifiableMap(map);
    }

    @PostMapping("/solve")
    public List<PlacementDto> solve(@RequestParam(value = "date", required = false) String date) {
        long start = System.currentTimeMillis();
        log.info("POST /api/solve date={}", date);

        String[] labels = parseDateToLabels(date);

        Board boardCopy = new Board(board);
        Cell monthCell = boardCopy.findCellByLabel(labels[0]);
        Cell dayCell = boardCopy.findCellByLabel(labels[1]);

        if (monthCell == null || dayCell == null) {
            log.warn("POST /api/solve — no cell found for {} {} ({}ms)",
                    labels[0], labels[1], System.currentTimeMillis() - start);
            return Collections.emptyList();
        }

        boardCopy.setTarget(monthCell.getRow(), monthCell.getCol());
        boardCopy.setTarget(dayCell.getRow(), dayCell.getCol());

        Solver solver = new Solver(boardCopy, pieces);
        List<Placement> placements = solver.solve();

        if (placements == null || placements.isEmpty()) {
            log.warn("POST /api/solve — no solution for {} {} ({}ms)",
                    labels[0], labels[1], System.currentTimeMillis() - start);
            return Collections.emptyList();
        }

        List<PlacementDto> placementDtos = new ArrayList<>();
        for (Placement placement : placements) {
            int pieceIndex = pieceIdToIndexMap.get(placement.getPieceId());
            placementDtos.add(new PlacementDto(pieceIndex, placement.getCoveredCells()));
        }

        log.info("POST /api/solve — solved {} {} → {} placements ({}ms)",
                labels[0], labels[1], placementDtos.size(), System.currentTimeMillis() - start);
        return placementDtos;
    }

    @GetMapping(path = "/pieces", produces = "application/json")
    public List<PieceDto> getPieces() {
        long start = System.currentTimeMillis();
        log.info("GET /api/pieces");

        List<PieceDto> pieceDtos = new ArrayList<>();
        for (int i = 0; i < pieces.size(); i++) {
            Piece piece = pieces.get(i);
            pieceDtos.add(new PieceDto(i, piece.getId(), piece.getCanonicalCells()));
        }

        log.info("GET /api/pieces — {} pieces ({}ms)", pieceDtos.size(), System.currentTimeMillis() - start);
        return pieceDtos;
    }

    @GetMapping("/board")
    public List<BoardCell> getBoard(@RequestParam(value = "date", required = false) String date) {
        long start = System.currentTimeMillis();
        log.info("GET /api/board date={}", date);

        String[] labels = parseDateToLabels(date);
        List<BoardCell> cells = board.getBoardCellsForDate(labels[0], labels[1]);

        log.info("GET /api/board — {} {} → {} cells ({}ms)",
                labels[0], labels[1], cells.size(), System.currentTimeMillis() - start);
        return cells;
    }

    /**
     * Parses a YYYY-MM-DD date string into [monthLabel, dayLabel].
     * Defaults to today when date is null or empty.
     */
    private String[] parseDateToLabels(String date) {
        if (date == null || date.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            return new String[]{MONTHS[month - 1], String.valueOf(day)};
        }

        String[] dateParts = date.split("-");
        if (dateParts.length != 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid date format. Use YYYY-MM-DD");
        }

        try {
            int month = Integer.parseInt(dateParts[1]);
            int day = Integer.parseInt(dateParts[2]);
            if (month < 1 || month > 12 || day < 1 || day > 31) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid date values");
            }
            return new String[]{MONTHS[month - 1], String.valueOf(day)};
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid date format. Use YYYY-MM-DD");
        }
    }
}

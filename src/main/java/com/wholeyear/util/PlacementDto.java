package com.wholeyear.util;


import com.wholeyear.model.Cell;

import java.util.Collections;
import java.util.List;



 public class PlacementDto {
    private final Integer pieceId;
    private final List<Cell> Cells;

    public PlacementDto(Integer pieceId,  List<Cell> coveredCells) {
        this.pieceId = pieceId;
        this.Cells = Collections.unmodifiableList(coveredCells);
    }

    public Integer getPieceId() {
        return pieceId;
    }

    public List<Cell> getCells() {
        return Cells;
    }

    
}

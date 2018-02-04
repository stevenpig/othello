package com.ubs.othello.component;

import com.ubs.othello.constant.PieceType;
import lombok.Data;

/**
 * Created by Steven on 1/31/2018.
 */
@Data
public class Piece {

    private PieceType pieceType;

    public Piece(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Piece piece = (Piece) o;

        return pieceType == piece.pieceType;
    }

}

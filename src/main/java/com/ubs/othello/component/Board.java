package com.ubs.othello.component;

import com.google.common.collect.Sets;
import com.ubs.othello.constant.MessageConstant;
import com.ubs.othello.constant.OthelloConstant;
import com.ubs.othello.constant.PieceType;
import com.ubs.othello.constant.WinLose;
import lombok.Data;

import java.util.Set;

/**
 * Created by Steven on 1/31/2018.
 */
@Data
public class Board {

    private int row;

    private int col;

    private Piece[][] board;

    public Board() {
        this(OthelloConstant.DEFAULT_ROW, OthelloConstant.DEFAULT_COL);
    }

    public Board(int row, int col) {

        this.row = row;
        this.col = col;
        board = new Piece[row][col];

        initBoard();

    }

    private void initBoard() {

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = new Piece(PieceType.BLANK);
            }
        }

        //init central piece
        board[(row - 1) / 2][(col - 1) / 2] = new Piece(PieceType.O);
        board[(row - 1) / 2][((col - 1) / 2) + 1] = new Piece(PieceType.X);
        board[((row - 1) / 2) + 1][(col - 1) / 2] = new Piece(PieceType.X);
        board[((row - 1) / 2) + 1][((col - 1) / 2) + 1] = new Piece(PieceType.O);
    }


    public boolean isPositionValid(Position p) {

        boolean isValid = false;

        if (p != null) {
            return (p.getX() >= 0 && p.getX() < row) && (p.getY() >= 0 && p.getY() < col);
        }

        return isValid;
    }

    public void displayBoard() {

        char[][] output = new char[row][col];

        String newLine = System.getProperty("line.separator");
        System.out.print(newLine);

        for (int i = 0; i < row; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < col; j++) {

                if (board[i][j].getPieceType() == PieceType.O)
                    System.out.print('O');
                else if (board[i][j].getPieceType() == PieceType.X)
                    System.out.print('X');
                else
                    System.out.print('-');

            }
            System.out.print(newLine);
        }
        System.out.print("  ");
        for (int i = 0; i < OthelloConstant.X_COOR.length; i++)
            System.out.print(OthelloConstant.X_COOR[i]);
        System.out.print(newLine);
        System.out.print(newLine);
    }

    public int countPiece(PieceType pieceType) {
        int count = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j].getPieceType() == pieceType)
                    count++;
            }
        }
        return count;
    }

    private PieceType getOpponent(Piece piece) {
        if (piece.getPieceType() == PieceType.O)
            return PieceType.X;
        else
            return PieceType.O;
    }

    private int getInBetweenPieces(Piece piece, Position pos, int xDir, int yDir) {
        int switches = 0;
        int opponentCount = 0;

        PieceType opponent = getOpponent(piece);

        int step = 1;
        while (true) {

            Position nextPos = new Position(pos.getX() + step * xDir, pos.getY() + step * yDir);
            if (!isPositionValid(nextPos))
                break;

            Piece nextPiece = board[nextPos.getX()][nextPos.getY()];

            //same piece type
            if (piece.getPieceType() == nextPiece.getPieceType()) {
                switches += opponentCount;
                break;
            }

            //opponent piece
            if (nextPiece.getPieceType() == opponent)
                opponentCount++;

            //Blank piece
            if (nextPiece.getPieceType() == PieceType.BLANK) {
                switches = 0;
                break;
            }

            step++;
        }

        return switches;

    }

    public boolean allowMove(Position pos, Piece piece) {

        //if position exceeds the board dimension
        if (!isPositionValid(pos))
            return false;

        //check it's not occupied
        if (board[pos.getX()][pos.getY()].getPieceType() != PieceType.BLANK)
            return false;

        //check if it can switch other pieces
        for (int[] dir : OthelloConstant.directions) {
            if (getInBetweenPieces(piece, pos, dir[0], dir[1]) > 0)
                return true;
        }

        return false;
    }


    public void move(Position pos, Piece piece) throws IllegalArgumentException {

        if (!isPositionValid(pos)) {
            throw new IllegalArgumentException(MessageConstant.WRONG_POSITION);
        }

        if (allowMove(pos, piece)) {
            board[pos.getX()][pos.getY()] = piece;

            // switch all pieces in between
            for (int[] direction : OthelloConstant.directions) {
                if (getInBetweenPieces(piece, pos, direction[0], direction[1]) > 0)
                    flipPiece(piece, pos, direction[0], direction[1]);
            }
        }
    }

    public void flipPiece(Piece piece, Position pos, int x, int y) {

        PieceType opponent = getOpponent(piece);

        int step = 1;
        while (true) {
            PieceType nextPiece = board[pos.getX() + step * x][pos.getY() + step * y].getPieceType();
            if (nextPiece == piece.getPieceType()) {
                break;
            } else if (nextPiece == opponent) {
                board[pos.getX() + step * x][pos.getY() + step * y] = piece;
            }
            step++;
        }
    }

    public Set<Position> hasPossibleMoves(Piece piece) {

        Set<Position> positionSet = Sets.newHashSet();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {

                Position pos = new Position(i, j);
                if (allowMove(pos, piece))
                    positionSet.add(pos);
            }
        }

        return positionSet;
    }

    public WinLose checkFinish(Set<Position> positionSetX, Set<Position> positionSetO){

        int countPieceX = countPiece(PieceType.X);
        int countPieceO = countPiece(PieceType.O);
        int countPieceBlank = countPiece(PieceType.BLANK);

        if(countPieceBlank == 0){
            return determineWinLose(countPieceX, countPieceO);
        }

        if (positionSetX.isEmpty() && positionSetO.isEmpty()) {
            return determineWinLose(countPieceX, countPieceO);
        }

        return WinLose.NOT_FINISH;
    }

    private WinLose determineWinLose(int countPieceX, int countPieceO) {
        if(countPieceX > countPieceO)
            return WinLose.WIN_X;
        else if(countPieceO > countPieceX)
            return WinLose.WIN_O;
        else
            return WinLose.DRAW;
    }
}

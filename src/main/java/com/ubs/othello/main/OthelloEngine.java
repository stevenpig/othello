package com.ubs.othello.main;

import com.ubs.othello.component.Board;
import com.ubs.othello.component.Piece;
import com.ubs.othello.component.Position;
import com.ubs.othello.constant.MessageConstant;
import com.ubs.othello.constant.OthelloConstant;
import com.ubs.othello.constant.PieceType;
import com.ubs.othello.constant.WinLose;
import com.ubs.othello.util.OthelloUtil;

import java.util.Scanner;
import java.util.Set;

/**
 * Created by Steven on 1/31/2018.
 */
public class OthelloEngine {

    private Board board = null;

    public OthelloEngine(Board board) {
        this.board = board;
    }

    public void printResult(WinLose winLose) {
        System.out.println(MessageConstant.NO_FURTHER_MOVE);

        int countPieceX = board.countPiece(PieceType.X);
        int countPieceO = board.countPiece(PieceType.O);

        if (winLose == WinLose.WIN_O)
            System.out.println(String.format(MessageConstant.FINISH_MESSAGE, PieceType.O, countPieceO, countPieceX));
        else if (winLose == WinLose.WIN_X)
            System.out.println(String.format(MessageConstant.FINISH_MESSAGE, PieceType.X, countPieceX, countPieceO));
        else
            System.out.println(WinLose.DRAW);
    }

    public Position getPosition(Piece piece) {
        Position pos = null;
        Scanner scan = null;
        while (true) {
            try {
                System.out.print(String.format(MessageConstant.PLAYER_MOVE, piece.getPieceType()));
                scan = new Scanner(System.in);
                String input = scan.next();
                pos = OthelloUtil.INSTANCE.getCoordinate(input);

                if (pos != null && board.allowMove(pos, piece))
                    break;
                else {
                    System.out.println(MessageConstant.INVALID_MOVE);
                    System.out.println("");
                }
            } catch (IllegalArgumentException iae) {
                System.out.println(MessageConstant.INVALID_MOVE);
                System.out.println("");
            }
        }

        return pos;
    }

    public boolean checkFinish(Set<Position> positionSetX, Set<Position> positionSetO) {

        WinLose winLose = board.checkFinish(positionSetX, positionSetO);
        if (winLose != WinLose.NOT_FINISH) {
            board.displayBoard();
            printResult(winLose);
            return true;
        }
        return false;
    }

    public boolean move(Piece piece, Piece opponentPiece) {
        Position pos = getPosition(piece);

        board.move(pos, piece);

        Set<Position> positionSetX = board.hasPossibleMoves(piece);
        Set<Position> positionSetO = board.hasPossibleMoves(opponentPiece);

        if (checkFinish(positionSetX, positionSetO))
            return false;

        return true;
    }

    public void start() {

        Piece pieceX = new Piece(PieceType.X);
        Piece pieceO = new Piece(PieceType.O);
        board.displayBoard();

        while (true) {

            Set<Position> positionSetX = board.hasPossibleMoves(pieceX);
            Set<Position> positionSetO = board.hasPossibleMoves(pieceO);

            //check the game finish or not
            if (checkFinish(positionSetX, positionSetO)) {
                break;
            }

            //Piece X
            if (positionSetX.size() == 0) {
                System.out.println(MessageConstant.X_SKIP_MESSAGE);
            } else {

                if (move(pieceX, pieceO))
                    board.displayBoard();
                else
                    break;
                positionSetO = board.hasPossibleMoves(pieceO);
            }

            //Piece O
            if (positionSetO.size() == 0) {
                System.out.println(MessageConstant.O_SKIP_MESSAGE);
            } else {

                if (move(pieceO, pieceX))
                    board.displayBoard();
                else
                    break;
            }

        }
    }

    public static void main(String[] args) {

        Board board = new Board(OthelloConstant.DEFAULT_ROW, OthelloConstant.DEFAULT_COL);

        OthelloEngine othelloEngine = new OthelloEngine(board);

        othelloEngine.start();

    }
}


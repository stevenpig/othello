package com.ubs.othello.component;

import com.ubs.othello.constant.OthelloConstant;
import com.ubs.othello.constant.PieceType;
import com.ubs.othello.constant.WinLose;
import javafx.geometry.Pos;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Steven on 2/3/2018.
 */
public class BoardTest {

    private Board b = null;

    private ByteArrayOutputStream outContent = null;

    @Before
    public void setUp() {
        b = new Board();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testInitBoard() {

        assertEquals(OthelloConstant.DEFAULT_ROW, b.getRow());
        assertEquals(OthelloConstant.DEFAULT_COL, b.getCol());

        for (int i = 0; i < b.getRow(); i++) {
            for (int j = 0; j < b.getCol(); j++) {
                if (i == 3 && j == 3)
                    assertEquals(PieceType.O, b.getBoard()[i][j].getPieceType());
                else if (i == 3 && j == 4)
                    assertEquals(PieceType.X, b.getBoard()[i][j].getPieceType());
                else if (i == 4 && j == 3)
                    assertEquals(PieceType.X, b.getBoard()[i][j].getPieceType());
                else if (i == 4 && j == 4)
                    assertEquals(PieceType.O, b.getBoard()[i][j].getPieceType());
                else
                    assertEquals(PieceType.BLANK, b.getBoard()[i][j].getPieceType());
            }
        }
    }

    @Test
    public void testIsPositionValid() {

        //null test
        assertEquals(false, b.isPositionValid(null));

        //both x and y are out of range
        Position p = new Position(-1, -1);
        assertEquals(false, b.isPositionValid(p));

        //only x is in
        p = new Position(1, 999);
        assertEquals(false, b.isPositionValid(p));

        //only y is in
        p = new Position(999, 1);
        assertEquals(false, b.isPositionValid(p));

        //both x and y are in
        p = new Position(1, 1);
        assertEquals(true, b.isPositionValid(p));
    }

    @Test
    public void testDisplayBoard() {
        String newLine = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder();
        sb.append(newLine);
        sb.append("1 --------").append(newLine);
        sb.append("2 --------").append(newLine);
        sb.append("3 --------").append(newLine);
        sb.append("4 ---OX---").append(newLine);
        sb.append("5 ---XO---").append(newLine);
        sb.append("6 --------").append(newLine);
        sb.append("7 --------").append(newLine);
        sb.append("8 --------").append(newLine);
        sb.append("  abcdefgh");
        sb.append(newLine);
        sb.append(newLine);

        b.displayBoard();
        assertEquals(sb.toString(), outContent.toString());
    }

    @Test
    public void testCountPiece() {

        int countO = b.countPiece(PieceType.O);
        assertEquals(2, countO);

        int countX = b.countPiece(PieceType.X);
        assertEquals(2, countX);

        int remainings = b.getRow() * b.getCol() - countO - countX;
        assertEquals(60, remainings);
    }

    @Test
    public void testGetOppoent() throws Exception {

        try {
            Object[] param = new Object[1];
            param[0] = new Piece(PieceType.O);
            PieceType opp = ReflectionTestUtils.invokeMethod(b, "getOpponent", param);
            assertEquals(PieceType.X, opp);

            param = new Object[1];
            param[0] = new Piece(PieceType.X);
            opp = ReflectionTestUtils.invokeMethod(b, "getOpponent", param);
            assertEquals(PieceType.O, opp);
        } catch (Exception e) {
            throw e;
        }

    }

    @Test
    public void testAllowMove() {

        //out of range
        Position pos = new Position(-1, -1);
        Piece piece = new Piece(PieceType.O);
        assertEquals(false, b.allowMove(pos, piece));

        //if there is already a piece
        pos = new Position(3, 3);
        assertEquals(false, b.allowMove(pos, piece));

        //check any opp piece in between
        pos = new Position(0, 0);
        assertEquals(false, b.allowMove(pos, piece));

        pos = new Position(5, 3);
        assertEquals(true, b.allowMove(pos, piece));
    }

    @Test
    public void testFlipPiece() {

        //test first flip
        Piece p = new Piece(PieceType.X);
        Position pos = new Position(2, 3);

        assertEquals(PieceType.O, b.getBoard()[3][3].getPieceType());
        b.flipPiece(p, pos, 1, 0);
        assertEquals(PieceType.X, b.getBoard()[3][3].getPieceType());

        //build the custom board to check all directions
        b.getBoard()[1][1] = new Piece(PieceType.O);
        b.getBoard()[1][3] = new Piece(PieceType.O);
        b.getBoard()[1][5] = new Piece(PieceType.O);

        b.getBoard()[2][2] = new Piece(PieceType.X);
        b.getBoard()[2][3] = new Piece(PieceType.X);
        b.getBoard()[2][4] = new Piece(PieceType.X);

        b.getBoard()[3][1] = new Piece(PieceType.O);
        b.getBoard()[3][3] = new Piece(PieceType.BLANK);
        b.getBoard()[3][5] = new Piece(PieceType.O);

        b.getBoard()[4][2] = new Piece(PieceType.X);
        b.getBoard()[4][3] = new Piece(PieceType.X);
        b.getBoard()[4][4] = new Piece(PieceType.X);

        b.getBoard()[5][1] = new Piece(PieceType.O);
        b.getBoard()[5][3] = new Piece(PieceType.O);
        b.getBoard()[5][5] = new Piece(PieceType.O);

        p = new Piece(PieceType.O);
        pos = new Position(3, 3);

        assertEquals(PieceType.BLANK, b.getBoard()[3][3].getPieceType());
        for (int[] dir : OthelloConstant.directions)
            b.flipPiece(p, pos, dir[0], dir[1]);

        assertEquals(PieceType.O, b.getBoard()[1][1].getPieceType());
        assertEquals(PieceType.O, b.getBoard()[1][3].getPieceType());
        assertEquals(PieceType.O, b.getBoard()[1][5].getPieceType());

        assertEquals(PieceType.O, b.getBoard()[2][2].getPieceType());
        assertEquals(PieceType.O, b.getBoard()[2][3].getPieceType());
        assertEquals(PieceType.O, b.getBoard()[2][4].getPieceType());

        assertEquals(PieceType.O, b.getBoard()[3][1].getPieceType());
        assertEquals(PieceType.BLANK, b.getBoard()[3][3].getPieceType());
        assertEquals(PieceType.O, b.getBoard()[3][5].getPieceType());

        assertEquals(PieceType.O, b.getBoard()[4][2].getPieceType());
        assertEquals(PieceType.O, b.getBoard()[4][3].getPieceType());
        assertEquals(PieceType.O, b.getBoard()[4][4].getPieceType());

        assertEquals(PieceType.O, b.getBoard()[5][1].getPieceType());
        assertEquals(PieceType.O, b.getBoard()[5][3].getPieceType());
        assertEquals(PieceType.O, b.getBoard()[5][5].getPieceType());

    }

    @Test
    public void testHasPossibleMoves() {

        //test first move
        Set<Position> moveSet = b.hasPossibleMoves(new Piece(PieceType.X));
        assertEquals(4, moveSet.size());
        assertThat(moveSet, containsInAnyOrder(new Position(4, 5), new Position(2, 3), new Position(3, 2), new Position(5,4)));

        //test no moves
        b = blankBoard();
        b.getBoard()[0][1] = new Piece(PieceType.X);
        moveSet = b.hasPossibleMoves(new Piece(PieceType.X));
        assertEquals(0, moveSet.size());
    }

    @Test
    public void testCheckFinish() {

        //test just load
        WinLose winLose = b.checkFinish(b.hasPossibleMoves(new Piece(PieceType.X)), b.hasPossibleMoves(new Piece(PieceType.O)));
        assertEquals(WinLose.NOT_FINISH, winLose.NOT_FINISH);

        //fake result test
        //no X , only O
        Board mockBoard = new Board();
        mockBoard.getBoard()[(mockBoard.getRow() - 1) / 2][(mockBoard.getCol() - 1) / 2] = new Piece(PieceType.O);
        mockBoard.getBoard()[(mockBoard.getRow() - 1) / 2][((mockBoard.getCol() - 1) / 2) + 1] = new Piece(PieceType.O);
        mockBoard.getBoard()[((mockBoard.getRow() - 1) / 2) + 1][(mockBoard.getCol() - 1) / 2] = new Piece(PieceType.O);
        mockBoard.getBoard()[((mockBoard.getRow() - 1) / 2) + 1][((mockBoard.getCol() - 1) / 2) + 1] = new Piece(PieceType.O);
        winLose = mockBoard.checkFinish(mockBoard.hasPossibleMoves(new Piece(PieceType.X)), mockBoard.hasPossibleMoves(new Piece(PieceType.O)));
        assertEquals(WinLose.WIN_O, winLose);

        //no O, only X
        mockBoard.getBoard()[(mockBoard.getRow() - 1) / 2][(mockBoard.getCol() - 1) / 2] = new Piece(PieceType.X);
        mockBoard.getBoard()[(mockBoard.getRow() - 1) / 2][((mockBoard.getCol() - 1) / 2) + 1] = new Piece(PieceType.X);
        mockBoard.getBoard()[((mockBoard.getRow() - 1) / 2) + 1][(mockBoard.getCol() - 1) / 2] = new Piece(PieceType.X);
        mockBoard.getBoard()[((mockBoard.getRow() - 1) / 2) + 1][((mockBoard.getCol() - 1) / 2) + 1] = new Piece(PieceType.X);
        winLose = mockBoard.checkFinish(mockBoard.hasPossibleMoves(new Piece(PieceType.X)), mockBoard.hasPossibleMoves(new Piece(PieceType.O)));
        assertEquals(WinLose.WIN_X, winLose);

        //no possible move, draw
        mockBoard.getBoard()[(mockBoard.getRow() - 1) / 2][(mockBoard.getCol() - 1) / 2] = new Piece(PieceType.BLANK);
        mockBoard.getBoard()[(mockBoard.getRow() - 1) / 2][((mockBoard.getCol() - 1) / 2) + 1] = new Piece(PieceType.BLANK);
        mockBoard.getBoard()[((mockBoard.getRow() - 1) / 2) + 1][(mockBoard.getCol() - 1) / 2] = new Piece(PieceType.BLANK);
        mockBoard.getBoard()[((mockBoard.getRow() - 1) / 2) + 1][((mockBoard.getCol() - 1) / 2) + 1] = new Piece(PieceType.BLANK);
        mockBoard.getBoard()[0][0] = new Piece(PieceType.O);
        mockBoard.getBoard()[0][2] = new Piece(PieceType.X);

        winLose = mockBoard.checkFinish(mockBoard.hasPossibleMoves(new Piece(PieceType.X)), mockBoard.hasPossibleMoves(new Piece(PieceType.O)));
        assertEquals(WinLose.DRAW, winLose);

        //no possible move, x wins
        mockBoard.getBoard()[0][0] = new Piece(PieceType.X);
        mockBoard.getBoard()[0][2] = new Piece(PieceType.X);

        winLose = mockBoard.checkFinish(mockBoard.hasPossibleMoves(new Piece(PieceType.X)), mockBoard.hasPossibleMoves(new Piece(PieceType.O)));
        assertEquals(WinLose.WIN_X, winLose);

        //no possible move, o wins
        mockBoard.getBoard()[0][0] = new Piece(PieceType.O);
        mockBoard.getBoard()[0][2] = new Piece(PieceType.O);

        winLose = mockBoard.checkFinish(mockBoard.hasPossibleMoves(new Piece(PieceType.X)), mockBoard.hasPossibleMoves(new Piece(PieceType.O)));
        assertEquals(WinLose.WIN_O, winLose);

        //if the board is full
        for (int i = 0; i < mockBoard.getRow(); i++) {
            for (int j = 0; j < mockBoard.getCol(); j++) {
                mockBoard.getBoard()[i][j] = new Piece(PieceType.O);
            }
        }

        winLose = mockBoard.checkFinish(mockBoard.hasPossibleMoves(new Piece(PieceType.X)), mockBoard.hasPossibleMoves(new Piece(PieceType.O)));
        assertEquals(WinLose.WIN_O, winLose);
    }

    @Test
    public void testDetermineWinLose() {

        WinLose winLose = ReflectionTestUtils.invokeMethod(b, "determineWinLose", 1, 0);
        assertEquals(WinLose.WIN_X, winLose);

        winLose = ReflectionTestUtils.invokeMethod(b, "determineWinLose", 0, 1);
        assertEquals(WinLose.WIN_O, winLose);

        winLose = ReflectionTestUtils.invokeMethod(b, "determineWinLose", 0, 0);
        assertEquals(WinLose.DRAW, winLose);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMove_InvalidMove() {

        //invalid position
        Position pos = new Position(-999,-999);
        b.move(pos, new Piece(PieceType.O));

    }

    @Test
    public void testMove_ValidMove() {

        //test valid flip
        Position pos = new Position(2,3);
        b.move(pos, new Piece(PieceType.X));
        assertEquals(PieceType.X, b.getBoard()[3][3].getPieceType());
        assertEquals(PieceType.X, b.getBoard()[2][3].getPieceType());

        //test no flip

        pos = new Position(0, 0);
        b.move(pos, new Piece(PieceType.O));
        assertEquals(PieceType.BLANK, b.getBoard()[0][0].getPieceType());
    }

    private Board blankBoard() {
        Board blankBoard = new Board();

        for (int i = 0; i < blankBoard.getRow(); i++) {
            for (int j = 0; j < blankBoard.getCol(); j++) {
                blankBoard.getBoard()[i][j] = new Piece(PieceType.BLANK);
            }
        }
        return blankBoard;
    }

}

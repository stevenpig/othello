package com.ubs.othello.main;

import com.google.common.collect.Sets;
import com.ubs.othello.component.Board;
import com.ubs.othello.component.Piece;
import com.ubs.othello.component.Position;
import com.ubs.othello.constant.MessageConstant;
import com.ubs.othello.constant.OthelloConstant;
import com.ubs.othello.constant.PieceType;
import com.ubs.othello.constant.WinLose;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.test.util.ReflectionTestUtils;

import java.io.*;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Steven on 2/3/2018.
 */

public class OthelloEngineTest {


    private Board b = null;

    private OthelloEngine engine = null;

    private ByteArrayOutputStream outContent = null;

    @Before
    public void setUp() {
        b = Mockito.mock(Board.class);
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        engine = Mockito.spy(new OthelloEngine(b));
    }

    @Test
    public void testPrintResult() throws IOException {

        StringBuilder sb = new StringBuilder();
        sb.append(MessageConstant.NO_FURTHER_MOVE);
        sb.append("\r\n");
        sb.append(WinLose.DRAW);
        sb.append("\r\n");

        //draw
        engine.printResult(WinLose.DRAW);
        assertEquals(sb.toString(), outContent.toString());

        //x win
        outContent.reset();
        Mockito.when(b.countPiece(PieceType.X)).thenReturn(55);
        Mockito.when(b.countPiece(PieceType.O)).thenReturn(9);
        engine.printResult(WinLose.WIN_X);

        sb = new StringBuilder();
        sb.append(MessageConstant.NO_FURTHER_MOVE);
        sb.append("\r\n");
        sb.append(String.format(MessageConstant.FINISH_MESSAGE, PieceType.X, 55, 9));
        sb.append("\r\n");
        assertEquals(sb.toString(), outContent.toString());

        //o win
        outContent.reset();
        Mockito.when(b.countPiece(PieceType.X)).thenReturn(10);
        Mockito.when(b.countPiece(PieceType.O)).thenReturn(54);
        engine.printResult(WinLose.WIN_O);

        sb = new StringBuilder();
        sb.append(MessageConstant.NO_FURTHER_MOVE);
        sb.append("\r\n");
        sb.append(String.format(MessageConstant.FINISH_MESSAGE, PieceType.O, 54, 10));
        sb.append("\r\n");
        assertEquals(sb.toString(), outContent.toString());

    }

    @Test
    public void testGetPosition() {

        //good move (3d)
        String data = "3d\r\n";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Mockito.when(b.allowMove(Mockito.any(), Mockito.any())).thenReturn(true);

        Piece pieceX = new Piece(PieceType.X);
        Position pos = ReflectionTestUtils.invokeMethod(engine, "getPosition", pieceX);
        assertEquals(2, pos.getX());
        assertEquals(3, pos.getY());

        //good move (d3)
        data = "d3\r\n";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Mockito.when(b.allowMove(Mockito.any(), Mockito.any())).thenReturn(true);

        pieceX = new Piece(PieceType.X);
        pos = ReflectionTestUtils.invokeMethod(engine, "getPosition", pieceX);
        assertEquals(2, pos.getX());
        assertEquals(3, pos.getY());

        //test invalid move
        try {
            outContent.reset();
            data = "a1\r\n";
            InputStream is = new ByteArrayInputStream(data.getBytes());
            System.setIn(is);
            Mockito.when(b.allowMove(Mockito.any(), Mockito.any())).thenReturn(false);

            pieceX = new Piece(PieceType.X);
            pos = ReflectionTestUtils.invokeMethod(engine, "getPosition", pieceX);

            //     assertEquals(3, pos.getY());
        } catch (Exception e) {
            assertTrue(outContent.toString().contains(MessageConstant.INVALID_MOVE));
        }

        //wrong coordinate
        try {
            outContent.reset();
            data = "a1h\r\n";
            InputStream is = new ByteArrayInputStream(data.getBytes());
            System.setIn(is);
            Mockito.when(b.allowMove(Mockito.any(), Mockito.any())).thenReturn(false);

            pieceX = new Piece(PieceType.X);
            pos = ReflectionTestUtils.invokeMethod(engine, "getPosition", pieceX);

            //     assertEquals(3, pos.getY());
        } catch (Exception e) {
            assertTrue(outContent.toString().contains(MessageConstant.INVALID_MOVE));
        }
    }

    @Test
    public void testCheckFinish() {

        //not finish
        Mockito.when(b.checkFinish(Mockito.any(), Mockito.any())).thenReturn(WinLose.NOT_FINISH);
        boolean result = ReflectionTestUtils.invokeMethod(engine, "checkFinish", Sets.newHashSet(), Sets.newHashSet());
        assertEquals(false, result);

        //x win
        Mockito.when(b.checkFinish(Mockito.any(), Mockito.any())).thenReturn(WinLose.WIN_X);
        result = ReflectionTestUtils.invokeMethod(engine, "checkFinish", Sets.newHashSet(), Sets.newHashSet());
        assertEquals(true, result);

        //o win
        Mockito.when(b.checkFinish(Mockito.any(), Mockito.any())).thenReturn(WinLose.WIN_O);
        result = ReflectionTestUtils.invokeMethod(engine, "checkFinish", Sets.newHashSet(), Sets.newHashSet());
        assertEquals(true, result);
    }

    @Test
    public void testMove() throws Exception {

        //Not finish
        Piece currentPlayer = new Piece(PieceType.X);
        Piece opponent = new Piece(PieceType.O);

        String data = "3d\r\n";
        System.setIn(new ByteArrayInputStream(data.getBytes()));

        Position pos = new Position(2,3 );
        Mockito.when(b.allowMove(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.doReturn(pos).when(engine).getPosition(currentPlayer);
        Mockito.doReturn(true).when(engine).checkFinish(Mockito.anySet(), Mockito.anySet());

        //finish , cant move
        boolean result = engine.move(currentPlayer, opponent);
        assertEquals(false, result);

        //not finish, can move
        Mockito.doReturn(false).when(engine).checkFinish(Mockito.anySet(), Mockito.anySet());
        result = engine.move(currentPlayer, opponent);
        assertEquals(true, result);

    }

    @Test
    public void testStart_Empty() {

        Mockito.when(b.hasPossibleMoves(new Piece(PieceType.X))).thenReturn(Sets.newHashSet());
        Mockito.when(b.hasPossibleMoves(new Piece(PieceType.O))).thenReturn(Sets.newHashSet());
        Mockito.doReturn(true).when(engine).checkFinish(Mockito.anySet(), Mockito.anySet());
        engine.start();
        Mockito.verify(b, Mockito.times(1)).displayBoard();

    }

    @Test
    public void testStart_SkipX() {

        //check skip x
        String data = "d3\r\n";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Mockito.when(b.hasPossibleMoves(new Piece(PieceType.X))).thenReturn(Sets.newHashSet());
        Mockito.when(b.hasPossibleMoves(new Piece(PieceType.O))).thenReturn(Sets.newHashSet(new Position(2,3))).thenReturn(Sets.newHashSet());
        Mockito.when(b.checkFinish(Mockito.anySet(), Mockito.anySet())).thenReturn(WinLose.NOT_FINISH).thenReturn(WinLose.WIN_X);
        Mockito.doReturn(true).when(engine).move(Mockito.any(), Mockito.any());
        engine.start();
        Mockito.verify(b, Mockito.times(3)).displayBoard();
        Mockito.verify(engine, Mockito.times(1)).move(Mockito.any(), Mockito.any());
    }

    @Test
    public void testStart_SkipO() {
        //check skip x
        String data = "d3\r\n";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Mockito.when(b.hasPossibleMoves(new Piece(PieceType.O))).thenReturn(Sets.newHashSet());
        Mockito.when(b.hasPossibleMoves(new Piece(PieceType.X))).thenReturn(Sets.newHashSet(new Position(2,3))).thenReturn(Sets.newHashSet());
        Mockito.when(b.checkFinish(Mockito.anySet(), Mockito.anySet())).thenReturn(WinLose.NOT_FINISH).thenReturn(WinLose.WIN_X);
        Mockito.doReturn(true).when(engine).move(Mockito.any(), Mockito.any());
        engine.start();
        Mockito.verify(b, Mockito.times(3)).displayBoard();
        Mockito.verify(engine, Mockito.times(1)).move(Mockito.any(), Mockito.any());
    }

    @Test
    public void testStart_NoSkip() {

        String data = "d3\r\n";
        System.setIn(new ByteArrayInputStream(data.getBytes()));
        Mockito.when(b.hasPossibleMoves(new Piece(PieceType.O))).thenReturn(Sets.newHashSet(new Position(2,3))).thenReturn(Sets.newHashSet(new Position(2,3))).thenReturn(Sets.newHashSet());
        Mockito.when(b.hasPossibleMoves(new Piece(PieceType.X))).thenReturn(Sets.newHashSet(new Position(4,3))).thenReturn(Sets.newHashSet());
        Mockito.when(b.checkFinish(Mockito.anySet(), Mockito.anySet())).thenReturn(WinLose.NOT_FINISH).thenReturn(WinLose.WIN_X);
        Mockito.doReturn(true).when(engine).move(Mockito.any(), Mockito.any());
        engine.start();
        Mockito.verify(engine, Mockito.times(2)).move(Mockito.any(), Mockito.any());
        Mockito.verify(b, Mockito.times(4)).displayBoard();

    }


}

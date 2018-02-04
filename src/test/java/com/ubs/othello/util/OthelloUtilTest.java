package com.ubs.othello.util;

import com.ubs.othello.component.Position;
import com.ubs.othello.constant.OthelloConstant;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Steven on 2/1/2018.
 */
public class OthelloUtilTest {

    @Test
    public void testConvertXCoordinate() throws Exception {

        //wrong input
        char input = 'z';
        int result = OthelloUtil.INSTANCE.convertXCoordinate(input);
        assertEquals(-1, result);

        for (int i = 0; i < OthelloConstant.X_COOR.length; i++) {
            result = OthelloUtil.INSTANCE.convertXCoordinate(OthelloConstant.X_COOR[i]);
            assertEquals(i, result);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCoordinate_NullTest() {

        OthelloUtil.INSTANCE.getCoordinate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCoordinate_InvalidInput() {
        OthelloUtil.INSTANCE.getCoordinate("ddd");
    }

    @Test
    public void testGetCoordinate_ValidInput() {

        Position pos = OthelloUtil.INSTANCE.getCoordinate("d3");
        assertNotNull(pos);
        assertEquals(2, pos.getX());
        assertEquals(3, pos.getY());

        pos = OthelloUtil.INSTANCE.getCoordinate("3d");
        assertNotNull(pos);
        assertEquals(2, pos.getX());
        assertEquals(3, pos.getY());
    }

}
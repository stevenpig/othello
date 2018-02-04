package com.ubs.othello.util;

import com.ubs.othello.component.Position;
import com.ubs.othello.constant.OthelloConstant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Steven on 2/1/2018.
 */
public enum OthelloUtil {
    INSTANCE;

    public int convertXCoordinate(char x) {

        for (int i = 0; i < OthelloConstant.X_COOR.length; i++) {
            if (Character.toLowerCase(OthelloConstant.X_COOR[i]) == Character.toLowerCase(x))
                return i;
        }

        return -1;
    }

    public Position getCoordinate(String coordinate) {

        if (coordinate == null) {
            throw new IllegalArgumentException("Wrong input! Format: 3d or d3");
        }

        //if both characters are numbers or characters, throw exception
        String pattern = "^[a-hA-H]{1}[1-8]{1}$|^[1-8]{1}[a-hA-H]{1}$";
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(coordinate);
        if (m.find()) {
            int x, y;
            if (Character.isDigit(coordinate.charAt(0))) {
                y = convertXCoordinate(coordinate.charAt(1));
                x = (Integer.parseInt(coordinate.charAt(0) + "") - 1);
            } else {
                y = convertXCoordinate(coordinate.charAt(0));
                x = (Integer.parseInt(coordinate.charAt(1) + "") - 1);
            }
            return new Position(x, y);

        } else {
            throw new IllegalArgumentException("Wrong input! Format: 3d or d3");
        }

    }
}

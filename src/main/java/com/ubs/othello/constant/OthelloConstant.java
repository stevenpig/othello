package com.ubs.othello.constant;

/**
 * Created by Steven on 1/31/2018.
 */
public interface OthelloConstant {

    char[] X_COOR = new char[]{'a','b','c','d','e','f','g','h'};

    int [][] directions = {{-1, -1}, {0, -1}, {1, -1}, {-1, 0}, {1, 0}, {-1, 1}, {0, 1}, {1, 1}};

    int DEFAULT_ROW = 8;

    int DEFAULT_COL = 8;

}

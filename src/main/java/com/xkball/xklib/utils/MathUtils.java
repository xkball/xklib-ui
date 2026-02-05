package com.xkball.xklib.utils;

public class MathUtils {
    public static int roundToward(int value, int factor) {
        return positiveCeilDiv(value, factor) * factor;
    }
    
    public static int positiveCeilDiv(int x, int y) {
        return -Math.floorDiv(-x, y);
    }
}

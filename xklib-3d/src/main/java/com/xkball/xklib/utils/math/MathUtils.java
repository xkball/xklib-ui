package com.xkball.xklib.utils.math;

public class MathUtils {
    public static int roundToward(int value, int factor) {
        return positiveCeilDiv(value, factor) * factor;
    }
    
    public static int positiveCeilDiv(int x, int y) {
        return -Math.floorDiv(-x, y);
    }
    
    public static int floor(float v){
        return (int) Math.floor(v);
    }
    
    public static int ceil(float v) {
        return (int)Math.ceil(v);
    }
}

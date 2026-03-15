package com.xkball.xklib.ui.css.property.value;

public class CssLengthUnit {
    
    private final Type type;
    private final float value;
    
    public CssLengthUnit(Type type, float value) {
        this.type = type;
        this.value = value;
    }
    
    
    
    @Override
    public String toString() {
        return super.toString();
    }
    
    public enum Type{
        LENGTH,
        PERCENTAGE,
        EM,
        AUTO,
        MIN_CONTENT,
        MAX_CONTENT,
        CONTENT;
    }
}

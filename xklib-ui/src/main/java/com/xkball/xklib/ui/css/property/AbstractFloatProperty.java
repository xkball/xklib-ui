package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;

public abstract class AbstractFloatProperty implements IStyleProperty<Float> {
    
    protected float value;
    
    protected AbstractFloatProperty(float value) {
        this.value = value;
    }
    
    @Override
    public String valueString() {
        return String.format("%.2f",value);
    }
    
    @Override
    public Float value() {
        return value;
    }
    
    @Override
    public void setValue(Float value) {
        this.value = value;
    }
}

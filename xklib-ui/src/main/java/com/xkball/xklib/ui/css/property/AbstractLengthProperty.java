package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.ui.css.property.value.CssLengthUnit;

public abstract class AbstractLengthProperty implements IStyleProperty<CssLengthUnit> {
    protected CssLengthUnit value;
    
    public AbstractLengthProperty(CssLengthUnit value){
        this.value = value;
    }
    
    @Override
    public String valueString() {
        return value.toString();
    }
    
    @Override
    public CssLengthUnit value() {
        return value;
    }
    
    @Override
    public void setValue(CssLengthUnit value) {
        this.value = value;
    }
    
}

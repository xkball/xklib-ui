package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.css.property.value.CssSize;

public class SizeProperty implements IStyleProperty<CssSize> {
    
    public static final String NAME = "size";
    private CssSize value;
    
    public SizeProperty(CssSize value) {
        this.value = value;
    }
    
    @Override
    public String propertyName() {
        return NAME;
    }
    
    @Override
    public String valueString() {
        return value.toString();
    }
    
    @Override
    public CssSize value() {
        return value;
    }
    
    @Override
    public void setValue(CssSize value) {
        this.value = value;
    }
    
    @Override
    public void apply(IStyleSheet sheet, IGuiWidget widget) {
        widget.setStyle(s -> s.size = value.toDimension());
    }
}

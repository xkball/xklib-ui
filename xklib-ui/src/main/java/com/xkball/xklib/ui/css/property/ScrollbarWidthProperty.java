package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

public class ScrollbarWidthProperty extends AbstractFloatProperty {
    
    public static final String NAME = "scrollbar-width";
    
    public ScrollbarWidthProperty(float value) {
        super(value);
    }
    
    @Override
    public String propertyName() {
        return NAME;
    }
    
    @Override
    public void apply(IStyleSheet sheet, IGuiWidget widget) {
        widget.setStyle(s -> s.scrollbarWidth = value);
    }
}

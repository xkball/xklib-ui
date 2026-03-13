package com.xkball.xklib.api.gui.css;

import com.xkball.xklib.api.gui.widget.IDecoration;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

public interface IStyleProperty extends IDecoration {
    
    String propertyName();
    
    String valueString();
    
    void apply(IGuiWidget widget);
    
}

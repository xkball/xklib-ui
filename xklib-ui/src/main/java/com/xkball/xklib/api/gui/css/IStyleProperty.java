package com.xkball.xklib.api.gui.css;

import com.xkball.xklib.api.gui.widget.IDecoration;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

public interface IStyleProperty<T> extends IDecoration {
    
    String propertyName();
    
    String valueString();
    
    T value();
    
    void apply(IStyleSheet sheet, IGuiWidget widget);
    
    default boolean renderable(){
        return false;
    }
    
}

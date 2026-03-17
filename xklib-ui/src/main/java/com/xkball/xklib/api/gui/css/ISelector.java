package com.xkball.xklib.api.gui.css;

import com.xkball.xklib.api.gui.widget.IGuiWidget;

public interface ISelector {
    
    boolean match(IGuiWidget widget);
    
    int weight();
    
    default boolean isDynamic(){
        return false;
    }
}

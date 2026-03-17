package com.xkball.xklib.ui.css.selector;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

public class TypeSelector implements ISelector {
    
    private final String type;
    
    public TypeSelector(String type) {
        this.type = type;
    }
    
    @Override
    public boolean match(IGuiWidget widget) {
        return widget.getCSSType().equals(type);
    }
    
    @Override
    public int weight() {
        return 1;
    }
}

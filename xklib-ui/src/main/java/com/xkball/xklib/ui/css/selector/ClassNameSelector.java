package com.xkball.xklib.ui.css.selector;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

public class ClassNameSelector implements ISelector {
    
    private final String className;
    
    public ClassNameSelector(String className) {
        this.className = className;
    }
    
    @Override
    public boolean match(IGuiWidget widget) {
        return widget.getCSSClassName().equals(className);
    }
    
    @Override
    public int weight() {
        return 10;
    }
}

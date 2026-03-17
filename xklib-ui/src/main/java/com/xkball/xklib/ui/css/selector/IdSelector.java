package com.xkball.xklib.ui.css.selector;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

public class IdSelector implements ISelector {
    
    private final String id;
    
    public IdSelector(String id) {
        this.id = id;
    }
    
    @Override
    public boolean match(IGuiWidget widget) {
        return widget.getCSSId().equals(id);
    }
    
    @Override
    public int weight() {
        return 100;
    }
}

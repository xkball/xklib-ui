package com.xkball.xklib.ui.css.selector;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

public class FirstChildSelector implements ISelector {
    @Override
    public boolean match(IGuiWidget widget) {
        if(widget.getParent() == null) return false;
        return widget.getParent().getChildren().getFirst().equals(widget);
    }
    
    @Override
    public int weight() {
        return 10;
    }
}

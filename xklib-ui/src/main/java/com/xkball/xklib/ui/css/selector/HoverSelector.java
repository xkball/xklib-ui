package com.xkball.xklib.ui.css.selector;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

public class HoverSelector implements ISelector {
    
    @Override
    public boolean match(IGuiWidget widget) {
        return widget.isHovered();
    }
    
    @Override
    public int weight() {
        return 10;
    }
    
    @Override
    public boolean isDynamic() {
        return true;
    }
}

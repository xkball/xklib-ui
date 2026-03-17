package com.xkball.xklib.ui.css.selector;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

public class FocusedSelector implements ISelector {
    
    @Override
    public boolean match(IGuiWidget widget) {
        return widget.isPrimaryFocused();
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

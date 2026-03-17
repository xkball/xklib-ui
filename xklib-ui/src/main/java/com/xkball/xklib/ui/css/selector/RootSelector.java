package com.xkball.xklib.ui.css.selector;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

public class RootSelector implements ISelector {
    
    @Override
    public boolean match(IGuiWidget widget) {
        return widget.getParent() == null;
    }
    
    @Override
    public int weight() {
        return 10;
    }
}

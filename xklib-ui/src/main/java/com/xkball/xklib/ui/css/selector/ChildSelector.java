package com.xkball.xklib.ui.css.selector;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

public class ChildSelector implements ISelector {
    
    private final ISelector parent;
    private final ISelector child;
    
    public ChildSelector(ISelector parent, ISelector child) {
        this.parent = parent;
        this.child = child;
    }
    
    @Override
    public boolean match(IGuiWidget widget) {
        if(widget.getParent() == null) return false;
        return this.child.match(widget) && this.parent.match(widget.getParent());
    }
    
    @Override
    public int weight() {
        return parent.weight() + child.weight();
    }
    
    @Override
    public boolean isDynamic() {
        return parent.isDynamic() || child.isDynamic();
    }
}

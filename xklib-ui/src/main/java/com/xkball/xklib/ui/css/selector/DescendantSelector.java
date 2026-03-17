package com.xkball.xklib.ui.css.selector;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

public class DescendantSelector implements ISelector {
    
    private final ISelector parent;
    private final ISelector child;
    
    public DescendantSelector(ISelector parent, ISelector child) {
        this.parent = parent;
        this.child = child;
    }
    
    @Override
    public boolean match(IGuiWidget widget) {
        if(!child.match(widget)) return false;
        var parentW = widget.getParent();
        while (parentW != null){
            if(parent.match(parentW)) return true;
            parentW = parentW.getParent();
        }
        return false;
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

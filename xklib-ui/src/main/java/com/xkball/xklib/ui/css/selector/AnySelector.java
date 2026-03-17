package com.xkball.xklib.ui.css.selector;

import com.xkball.xklib.api.gui.css.ISelector;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

import java.util.ArrayList;
import java.util.List;

public class AnySelector implements ISelector {
    
    private final List<ISelector> selectors = new ArrayList<>();
    
    public AnySelector(List<ISelector> selectors){
        this.selectors.addAll(selectors);
    }
    
    @Override
    public boolean match(IGuiWidget widget) {
        return this.selectors.stream().anyMatch(s -> s.match(widget));
    }
    
    @Override
    public int weight() {
        return this.selectors.stream().mapToInt(ISelector::weight).max().orElse(0);
    }
    
    @Override
    public boolean isDynamic() {
        var result = false;
        for(var s : this.selectors){
            result |= s.isDynamic();
        }
        return result;
    }
}

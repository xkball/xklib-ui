package com.xkball.xklib.ui.widget.layout;

import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.widget.AbstractWidget;

public class ScrollableFlexLayout extends GridLayout {
    
    public boolean xScrollable = false;
    public boolean yScrollable = false;
    public boolean xScrollBarVisible = true;
    public boolean yScrollBarVisible = true;
    public int xScrollBarSize = 10;
    public int yScrollBarSize = 10;
    public FlexLayout inner;
    
    public ScrollableFlexLayout addChild(AbstractWidget widget, FlexElementParam param){
        this.inner.addChild(widget, param);
        return this;
    }
    
}

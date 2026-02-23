package com.xkball.xklib.ui.widget.layout;

import com.xkball.xklib.ui.layout.ScreenRectangle;
import com.xkball.xklib.ui.widget.AbstractContainerWidget;

public class AbsolutePosLayout extends AbstractContainerWidget<AbsolutePosLayout,ScreenRectangle> {
    
    public AbsolutePosLayout(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    public AbsolutePosLayout() {
        super();
    }
    
    @Override
    public void resize() {
        super.resize();
        for(var entry : this.children.entrySet()){
            entry.getKey().setRectangle(entry.getValue().offset(this.x, this.y));
        }
    }
    
}

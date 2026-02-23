package com.xkball.xklib.ui.widget.layout;

import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.widget.AbstractContainerWidget;

public class FlexLayout extends AbstractContainerWidget<FlexLayout, FlexElementParam> {
    
    public FlexParam flexParam;
    public int offsetX = 0;
    public int offsetY = 0;
    
    public FlexLayout(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    public FlexLayout(FlexParam param) {
        super();
        this.setOverflow(param.overflow);
        this.flexParam = param;
    }
    
    public FlexLayout() {
        super();
    }
    
    public void setOffsetX(int offsetX){
        this.offsetX = offsetX;
        this.markDirty();
    }
    
    public void setOffsetY(int offsetY){
        this.offsetY = offsetY;
        this.markDirty();
    }
    
    @Override
    public void resize() {
        super.resize();

    }
}

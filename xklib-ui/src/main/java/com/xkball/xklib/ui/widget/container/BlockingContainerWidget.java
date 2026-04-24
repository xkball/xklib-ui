package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;

//阻止任何层次在其下的组件处理交互事件
@GuiWidgetClass
public class BlockingContainerWidget extends ContainerWidget {
    
    @Override
    protected boolean onMouseScrolled(double x, double y, double scrollX, double scrollY) {
        super.onMouseScrolled(x, y, scrollX, scrollY);
        return true;
    }
    
    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        super.onMouseDragged(event, dx, dy);
        return true;
    }
    
    @Override
    protected boolean onCharTyped(ICharEvent event) {
        super.onCharTyped(event);
        return true;
    }
    
    @Override
    protected boolean onKeyReleased(IKeyEvent event) {
        super.onKeyReleased(event);
        return true;
    }
    
    @Override
    protected boolean onKeyPressed(IKeyEvent event) {
        super.onKeyPressed(event);
        return true;
    }
    
    @Override
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        super.onMouseReleased(event);
        return true;
    }
    
    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        super.onMouseClicked(event, doubleClick);
        return true;
    }
}

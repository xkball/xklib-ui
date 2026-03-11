package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;

public class DraggableContainer extends ContainerWidget{

    private boolean dragging = false;

    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            this.dragging = true;
        }
        return true;
    }

    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (this.dragging && event.button() == 0) {
            this.setAbsoluteX(this.absoluteX + (float) dx);
            this.setAbsoluteY(this.absoluteY + (float) dy);
            this.markDirty();
            return true;
        }
        return super.onMouseDragged(event, dx, dy);
    }

    @Override
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        if (this.dragging && event.button() == 0) {
            this.dragging = false;
            return true;
        }
        return super.onMouseReleased(event);
    }

    @Override
    public void onFocusChanged(boolean focused) {
        if (!focused) {
            this.dragging = false;
        }
        super.onFocusChanged(focused);
    }
}

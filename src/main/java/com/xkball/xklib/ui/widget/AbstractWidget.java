package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.api.gui.widget.IGuiEventListener;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.api.gui.widget.IRenderable;
import com.xkball.xklib.ui.navigation.ScreenRectangle;

public class AbstractWidget implements IGuiWidget, IRenderable, IGuiEventListener {
    
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean enabled = true;
    protected boolean visible = true;
    protected boolean focused = false;
    protected boolean hovered = false;
    protected volatile boolean dirty = false;
    
    public AbstractWidget(){
        this.markDirty();
    }
    
    public AbstractWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.hovered = this.isMouseOver(mouseX, mouseY);
    }
    
    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        
        if (this.isMouseOver(event.x(), event.y())) {
            if (this.shouldTakeFocusAfterInteraction()) {
                this.setFocused(true);
            }
            return this.onMouseClicked(event, doubleClick);
        }
        return false;
    }
    
    @Override
    public boolean mouseReleased(IMouseButtonEvent event) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        if(this.isMouseOver(event.x(),event.y())){
            return this.onMouseReleased(event);
        }
        return false;
    }
    
    @Override
    public boolean mouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        return this.onMouseDragged(event, dx, dy);
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        if (this.isMouseOver(x, y)) {
            return this.onMouseScrolled(x, y, scrollX, scrollY);
        }
        return false;
    }
    
    @Override
    public boolean keyPressed(IKeyEvent event) {
        if (!this.enabled || !this.visible || !this.focused) {
            return false;
        }
        return this.onKeyPressed(event);
    }
    
    @Override
    public boolean keyReleased(IKeyEvent event) {
        if (!this.enabled || !this.visible || !this.focused) {
            return false;
        }
        return this.onKeyReleased(event);
    }
    
    @Override
    public boolean charTyped(ICharEvent event) {
        if (!this.enabled || !this.visible || !this.focused) {
            return false;
        }
        return this.onCharTyped(event);
    }
    
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.enabled && this.visible &&
               mouseX >= this.x && mouseX < this.x + this.width &&
               mouseY >= this.y && mouseY < this.y + this.height;
    }
    
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        return false;
    }
    
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        return false;
    }
    
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        return false;
    }
    
    protected boolean onMouseScrolled(double x, double y, double scrollX, double scrollY) {
        return false;
    }
    
    protected boolean onKeyPressed(IKeyEvent event) {
        return false;
    }
    
    protected boolean onKeyReleased(IKeyEvent event) {
        return false;
    }
    
    protected boolean onCharTyped(ICharEvent event) {
        return false;
    }
    
    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }
    
    @Override
    public boolean isFocused() {
        return this.focused;
    }
    
    @Override
    public void setX(int x) {
        this.x = x;
    }
    
    @Override
    public void setY(int y) {
        this.y = y;
    }
    
    @Override
    public int getX() {
        return this.x;
    }
    
    @Override
    public int getY() {
        return this.y;
    }
    
    @Override
    public void setWidth(int width) {
        this.width = width;
    }
    
    @Override
    public void setHeight(int height) {
        this.height = height;
    }
    
    @Override
    public int getWidth() {
        return this.width;
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public boolean enabled() {
        return this.enabled;
    }
    
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    @Override
    public boolean visible() {
        return this.visible;
    }
    
    @Override
    public boolean isHovered() {
        return this.hovered;
    }
    
    @Override
    public void markDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    @Override
    public boolean isDirty() {
        return this.dirty;
    }
    
    @Override
    public ScreenRectangle getRectangle() {
        return IGuiWidget.super.getRectangle();
    }
    
    @Override
    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
    
    }
}

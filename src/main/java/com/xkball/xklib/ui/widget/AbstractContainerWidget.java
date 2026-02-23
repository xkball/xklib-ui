package com.xkball.xklib.ui.widget;

import com.xkball.xklib.XKLibWorkaround;
import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.api.gui.widget.ILayoutParma;
import com.xkball.xklib.ui.layout.ScreenRectangle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AbstractContainerWidget<S extends AbstractContainerWidget<S,T>,T extends ILayoutParma> extends AbstractWidget {
    
    protected final Map<AbstractWidget,T> children = new LinkedHashMap<>();
    
    public AbstractContainerWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    public AbstractContainerWidget() {
        super();
    }
    
    @SuppressWarnings("unchecked")
    public S addChild(AbstractWidget widget, T layoutParam) {
        this.children.put(widget, layoutParam);
        widget.init();
        this.markDirty();
        return (S)this;
    }
    
    public T getLayoutParam(AbstractWidget widget) {
        return this.children.get(widget);
    }
    
    public void removeChild(AbstractWidget widget) {
        this.children.remove(widget);
        this.markDirty();
    }
    
    public List<AbstractWidget> getChildren() {
        return this.children.keySet().stream().toList();
    }
    
    private boolean isPointInBounds(double x, double y) {
        if (this.overflow()) {
            return true;
        }
        return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
    }
    
    @Override
    public boolean mouseMoved(double mouseX, double mouseY) {
        if (!this.enabled || !this.visible) {
            this.hovered = false;
            return false;
        }
        
        boolean inBounds = this.isPointInBounds(mouseX, mouseY);
        
        boolean handled = false;
        for (var child : this.children.keySet()) {
            if (!handled && child.visible && inBounds) {
                if (child.mouseMoved(mouseX, mouseY)) {
                    handled = true;
                    continue;
                }
            }
            if (child instanceof AbstractContainerWidget acw) {
                acw.clearHoveredRecursive();
            } else {
                child.hovered = false;
            }
        }
        
        if (!handled) {
            boolean wasMouseOver = this.isMouseOver(mouseX, mouseY);
            if (wasMouseOver) {
                this.hovered = true;
                return true;
            }
        }
        
        this.hovered = false;
        return handled;
    }
    
    public void clearHoveredRecursive() {
        this.hovered = false;
        for (AbstractWidget child : this.children.keySet()) {
            if (child instanceof AbstractContainerWidget acw) {
                acw.clearHoveredRecursive();
            } else {
                child.hovered = false;
            }
        }
    }
    
    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        
        boolean inBounds = this.isPointInBounds(event.x(), event.y());
        
        for (AbstractWidget child : this.children.keySet()) {
            if (child.visible && child.enabled && inBounds) {
                ScreenRectangle rect = child.getRectangle();
                if (rect.containsPoint((int) event.x(), (int) event.y())) {
                    if (child.mouseClicked(event, doubleClick)) {
                        clearFocusedExcept(child);
                        return true;
                    }
                }
            }
        }
        
        boolean handled = super.mouseClicked(event, doubleClick);
        if (handled) {
            clearFocusedExcept(null);
        }
        return handled;
    }
    
    private void clearFocusedExcept(AbstractWidget except) {
        for (AbstractWidget child : this.children.keySet()) {
            if (child != except) {
                if (child instanceof AbstractContainerWidget acw) {
                    acw.clearFocusedRecursive();
                } else {
                    child.focused = false;
                }
            }
        }
    }
    
    public void clearFocusedRecursive() {
        this.focused = false;
        for (AbstractWidget child : this.children.keySet()) {
            if (child instanceof AbstractContainerWidget acw) {
                acw.clearFocusedRecursive();
            } else {
                child.focused = false;
            }
        }
    }
    
    @Override
    public boolean mouseReleased(IMouseButtonEvent event) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        
        boolean inBounds = this.isPointInBounds(event.x(), event.y());
        
        for (AbstractWidget child : this.children.keySet()) {
            if (child.visible && child.enabled && inBounds) {
                if (child.mouseReleased(event)) {
                    return true;
                }
            }
        }
        
        return super.mouseReleased(event);
    }
    
    @Override
    public boolean mouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        
        boolean inBounds = this.isPointInBounds(event.x(), event.y());
        
        for (AbstractWidget child : this.children.keySet()) {
            if (child.visible && child.enabled && inBounds) {
                if (child.mouseDragged(event, dx, dy)) {
                    return true;
                }
            }
        }
        
        return super.mouseDragged(event, dx, dy);
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        
        boolean inBounds = this.isPointInBounds(x, y);
        
        for (AbstractWidget child : this.children.keySet()) {
            if (child.visible && child.enabled && inBounds) {
                ScreenRectangle rect = child.getRectangle();
                if (rect.containsPoint((int) x, (int) y)) {
                    if (child.mouseScrolled(x, y, scrollX, scrollY)) {
                        return true;
                    }
                }
            }
        }
        
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }
    
    @Override
    public boolean keyPressed(IKeyEvent event) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        
        for (AbstractWidget child : this.children.keySet()) {
            if (child.visible && child.enabled && child.isFocused()) {
                if (child.keyPressed(event)) {
                    return true;
                }
            }
        }
        
        return super.keyPressed(event);
    }
    
    @Override
    public boolean keyReleased(IKeyEvent event) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        
        for (AbstractWidget child : this.children.keySet()) {
            if (child.visible && child.enabled && child.isFocused()) {
                if (child.keyReleased(event)) {
                    return true;
                }
            }
        }
        
        return super.keyReleased(event);
    }
    
    @Override
    public boolean charTyped(ICharEvent event) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        
        for (AbstractWidget child : this.children.keySet()) {
            if (child.visible && child.enabled && child.isFocused()) {
                if (child.charTyped(event)) {
                    return true;
                }
            }
        }
        
        return super.charTyped(event);
    }
    
    @Override
    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        var selfRect = this.getRectangle();
        this.renderInScissor(graphics, () -> {
            for (AbstractWidget child : this.children.keySet()) {
                if (child.visible && (this.overflow() || child.getRectangle().intersects(selfRect))) {
                    child.render(graphics, mouseX, mouseY, a);
                }
            }
        });
        super.render(graphics, mouseX, mouseY, a);
    }
    
    @Override
    public void visitWidgets(Consumer<IGuiWidget> widgetVisitor) {
        widgetVisitor.accept(this);
        for (AbstractWidget child : this.children.keySet()) {
            child.visitWidgets(widgetVisitor);
        }
    }
}


package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.navigation.ScreenRectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AbstractContainerWidget extends AbstractWidget {
    
    protected final List<AbstractWidget> children = new ArrayList<>();
    
    public AbstractContainerWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    public void addChild(AbstractWidget widget) {
        this.children.add(widget);
    }
    
    public void removeChild(AbstractWidget widget) {
        this.children.remove(widget);
    }
    
    public List<AbstractWidget> getChildren() {
        return this.children;
    }
    
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        for (AbstractWidget child : this.children) {
            if (child.visible) {
                child.mouseMoved(mouseX, mouseY);
            }
        }
    }
    
    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        
        for (AbstractWidget child : this.children) {
            if (child.visible && child.enabled) {
                ScreenRectangle rect = child.getRectangle();
                if (rect.containsPoint((int) event.x(), (int) event.y())) {
                    if (child.mouseClicked(event, doubleClick)) {
                        return true;
                    }
                }
            }
        }
        
        return super.mouseClicked(event, doubleClick);
    }
    
    @Override
    public boolean mouseReleased(IMouseButtonEvent event) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        
        for (AbstractWidget child : this.children) {
            if (child.visible && child.enabled) {
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
        
        for (AbstractWidget child : this.children) {
            if (child.visible && child.enabled) {
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
        
        for (AbstractWidget child : this.children) {
            if (child.visible && child.enabled) {
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
        
        for (AbstractWidget child : this.children) {
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
        
        for (AbstractWidget child : this.children) {
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
        
        for (AbstractWidget child : this.children) {
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
        if (!this.visible) {
            return;
        }
        
        for (AbstractWidget child : this.children) {
            if (child.visible) {
                child.render(graphics, mouseX, mouseY, a);
            }
        }
    }
    
    @Override
    public void visitWidgets(Consumer<IGuiWidget> widgetVisitor) {
        widgetVisitor.accept(this);
        for (AbstractWidget child : this.children) {
            child.visitWidgets(widgetVisitor);
        }
    }
}

package com.xkball.xklib.ui.widget.layout;

import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.api.gui.widget.ILayoutParma;
import com.xkball.xklib.ui.backend.gl.OpenGLWorkaround;
import com.xkball.xklib.ui.backend.input.MouseButtonEvent;
import com.xkball.xklib.ui.widget.AbstractContainerWidget;
import com.xkball.xklib.ui.widget.AbstractWidget;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class ScalePanel extends AbstractContainerWidget<ScalePanel, ScalePanel.NoParma> {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(ScalePanel.class);
    
    protected AbstractWidget child;
    protected double scale = 1.0;
    protected double minScale = 0.05;
    protected double maxScale = 5.0;
    protected double offsetX = 0.0;
    protected double offsetY = 0.0;
    protected boolean draggingPanel = false;
    
    public ScalePanel setChild(AbstractWidget widget){
        if (this.child != null) {
            this.removeChild(this.child);
        }
        this.child = widget;
        if (widget != null) {
            super.addChild(widget, NoParma.Instance);
        }
        return this;
    }
    
    @Override
    public ScalePanel addChild(AbstractWidget widget, NoParma layoutParam) {
        return this.setChild(widget);
    }
    
    public ScalePanel setScale(double value) {
        this.scale = clampScale(value);
        this.markDirty();
        return this;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    public ScalePanel setMinScale(double value) {
        this.minScale = value;
        this.scale = clampScale(this.scale);
        this.markDirty();
        return this;
    }
    
    public ScalePanel setMaxScale(double value) {
        this.maxScale = value;
        this.scale = clampScale(this.scale);
        this.markDirty();
        return this;
    }
    
    protected double clampScale(double value) {
        if (value < this.minScale) {
            return this.minScale;
        }
        if (value > this.maxScale) {
            return this.maxScale;
        }
        return value;
    }
    
    @Override
    public void resize() {
        super.resize();
        if (this.child != null) {
            this.child.setX(this.contentX);
            this.child.setY(this.contentY);
            this.child.setWidth(this.contentWidth);
            this.child.setHeight(this.contentHeight);
            this.child.resize();
        }
    }
    
    @Override
    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if (this.child == null || !this.visible) {
            return;
        }
        graphics.getPose().pushMatrix();
        graphics.getPose().translate(this.contentX + (int) this.offsetX, this.contentY + (int) this.offsetY);
        graphics.getPose().scale((float) this.scale, (float) this.scale);
        this.child.render(graphics, mouseX, mouseY, a);
        graphics.getPose().popMatrix();
    }
    
    protected double toChildX(double x) {
        return (x - this.contentX - this.offsetX) / this.scale;
    }
    
    protected double toChildY(double y) {
        return (y - this.contentY - this.offsetY) / this.scale;
    }
    
    @Override
    public boolean mouseMoved(double mouseX, double mouseY) {
        if (!this.enabled || !this.visible || this.child == null) {
            this.hovered = false;
            return false;
        }
        double cx = this.toChildX(mouseX);
        double cy = this.toChildY(mouseY);
        boolean handled = this.child.mouseMoved(cx, cy);
        this.hovered = handled || this.isMouseOver(mouseX, mouseY);
        return handled;
    }
    
    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (!this.enabled || !this.visible || this.child == null) {
            return false;
        }
        double mx = event.x();
        double my = event.y();
        if (!this.isMouseOver(mx, my)) {
            return false;
        }
        IMouseButtonEvent mapped = new MouseButtonEvent(this.toChildX(mx), this.toChildY(my), event.button(), event.modifiers());
        if (this.child.mouseClicked(mapped, doubleClick)) {
            this.clearFocusedExcept(this.child);
            this.draggingPanel = false;
            return true;
        }
        if (event.button() == 0) {
            this.draggingPanel = true;
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
    
    @Override
    public boolean mouseReleased(IMouseButtonEvent event) {
        if (!this.enabled || !this.visible || this.child == null) {
            return false;
        }
        if (this.draggingPanel && event.button() == 0) {
            this.draggingPanel = false;
            return true;
        }
        double mx = event.x();
        double my = event.y();
        if (!this.isMouseOver(mx, my)) {
            return false;
        }
        IMouseButtonEvent mapped = new MouseButtonEvent(this.toChildX(mx), this.toChildY(my), event.button(), event.modifiers());
        if (this.child.mouseReleased(mapped)) {
            return true;
        }
        return super.mouseReleased(event);
    }
    
    @Override
    public boolean mouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (!this.enabled || !this.visible || this.child == null) {
            return false;
        }
        IMouseButtonEvent mapped = new MouseButtonEvent(this.toChildX(event.x()), this.toChildY(event.y()), event.button(), event.modifiers());
        double cdx = dx / this.scale;
        double cdy = dy / this.scale;
        if (this.child.mouseDragged(mapped, cdx, cdy)) {
            this.draggingPanel = false;
            return true;
        }
        if (this.draggingPanel && event.button() == 0) {
            this.offsetX += dx;
            this.offsetY += dy;
            this.markDirty();
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }
    
    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (!this.enabled || !this.visible || this.child == null) {
            return false;
        }
        if (!this.isMouseOver(x, y)) {
            return false;
        }
        boolean shiftDown = GLFW.glfwGetKey(OpenGLWorkaround.window.getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(OpenGLWorkaround.window.getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        boolean controlDown = GLFW.glfwGetKey(OpenGLWorkaround.window.getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(OpenGLWorkaround.window.getHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
        
        if (scrollY != 0 && !shiftDown && !controlDown) {
            double oldScale = this.scale;
            double factor = 1.0 + scrollY * 0.1;
            double target = this.clampScale(this.scale * factor);
            if (target != oldScale) {
                double cx = this.toChildX(x);
                double cy = this.toChildY(y);
                this.scale = target;
                double nx = this.contentX + this.offsetX + cx * this.scale;
                double ny = this.contentY + this.offsetY + cy * this.scale;
                this.offsetX += x - nx;
                this.offsetY += y - ny;
                this.markDirty();
            }
            return true;
        }
        if (shiftDown && scrollY != 0) {
            this.offsetY += scrollY * 20.0;
            this.markDirty();
            return true;
        }
        if (controlDown && scrollY != 0) {
            this.offsetX += scrollY * 20.0;
            this.markDirty();
            return true;
        }
        double cx = this.toChildX(x);
        double cy = this.toChildY(y);
        if (this.child.mouseScrolled(cx, cy, scrollX, scrollY)) {
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }
    
    @Override
    public void visitWidgets(Consumer<IGuiWidget> widgetVisitor) {
        widgetVisitor.accept(this);
        if (this.child != null) {
            this.child.visitWidgets(widgetVisitor);
        }
    }
    
    @Override
    public int expectWidth() {
        if (this.child == null) {
            return 100;
        }
        return this.child.expectWidth();
    }
    
    @Override
    public int expectHeight() {
        if (this.child == null) {
            return 100;
        }
        return this.child.expectHeight();
    }
    
    protected void clearFocusedExcept(AbstractWidget except) {
        for (AbstractWidget w : this.children.keySet()) {
            if (w != except) {
                if (w instanceof AbstractContainerWidget<?,?> acw) {
                    acw.clearFocusedRecursive();
                } else {
                    w.setFocused(false);
                }
            }
        }
    }
    
    public static class NoParma implements ILayoutParma{
        public static NoParma Instance = new NoParma();
    }
}

package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.input.MouseButtonEvent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.system.GuiSystem;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AvailableSpace;
import dev.vfyjxf.taffy.style.TaffyStyle;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

public class ScalableContainer extends ContainerWidget{
    
    public static final Logger LOGGER = LoggerFactory.getLogger(ScalableContainer.class);
    
    protected Widget child;
    protected float scale = 1;
    protected float minScale = 0.05f;
    protected float maxScale = 5f;
    protected float xOffset = 0;
    protected float yOffset = 0;
    protected boolean draggingPanel = false;
    
    public ScalableContainer setChild(Widget widget){
        this.child = widget;
        widget.asTreeRoot();
        widget.setParent(this);
        widget.init();
        this.focusNode.addChild(widget.getFocusNode());
        return this;
    }
    
    @Override
    public ContainerWidget addChild(Widget widget, TaffyStyle style) {
        widget.setStyle(style);
        return this.setChild(widget);
    }
    
    @Override
    public List<Widget> getChildren() {
        return List.of(child);
    }
    
    public ScalableContainer setScale(float value) {
        this.scale = clampScale(value);
        this.markDirty();
        return this;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    public ScalableContainer setMinScale(float value) {
        this.minScale = value;
        this.scale = clampScale(this.scale);
        this.markDirty();
        return this;
    }
    
    public ScalableContainer setMaxScale(float value) {
        this.maxScale = value;
        this.scale = clampScale(this.scale);
        this.markDirty();
        return this;
    }
    
    protected float clampScale(float value) {
        return Math.clamp(value, minScale, maxScale);
    }
    
    @Override
    public void resize(float offsetX, float offsetY) {
        super.resize(offsetX, offsetY);
        var availableW = this.width * this.scale;
        var availableH = this.height * this.scale;
        this.child.tree.computeLayout(this.child.nodeId, TaffySize.of(AvailableSpace.definite(availableW), AvailableSpace.definite(availableH)));
        this.child.resize(offsetX, offsetY);
    }
    
    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        graphics.getPose().pushMatrix();
        graphics.getPose().translate(this.x + this.xOffset, this.y + this.yOffset);
        graphics.getPose().scale(this.scale, this.scale);
        child.render(graphics, mouseX, mouseY, a);
        graphics.getPose().popMatrix();
        graphics.disableScissor();
    }
    
    @Override
    public void renderBelow(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if (!this.visible) return;
        super.renderBelow(graphics, mouseX, mouseY, a);
        graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        graphics.getPose().pushMatrix();
        graphics.getPose().translate(this.x + this.xOffset, this.y + this.yOffset);
        graphics.getPose().scale(this.scale, this.scale);
        child.renderBelow(graphics, mouseX, mouseY, a);
        graphics.getPose().popMatrix();
        graphics.disableScissor();
    }
    
    @Override
    public void renderAbove(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if (!this.visible) return;
        super.renderAbove(graphics, mouseX, mouseY, a);
        graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        graphics.getPose().pushMatrix();
        graphics.getPose().translate(this.x + this.xOffset, this.y + this.yOffset);
        graphics.getPose().scale(this.scale, this.scale);
        child.renderAbove(graphics, mouseX, mouseY, a);
        graphics.getPose().popMatrix();
        graphics.disableScissor();
    }
    
    @Override
    public void renderDebug(IGUIGraphics graphics, int mouseX, int mouseY) {
        super.renderDebug(graphics, mouseX, mouseY);
        graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        graphics.getPose().pushMatrix();
        graphics.getPose().translate(this.x + this.xOffset, this.y + this.yOffset);
        graphics.getPose().scale(this.scale, this.scale);
        var xy = graphics.getPose().transformPosition(new Vector2f(0,0));
        graphics.getPose().popMatrix();
        graphics.hLine(-100000, 100000, xy.y, 0xFFFF0000);
        graphics.vLine(xy.x, -100000, 100000, 0xFF00FF00);
        graphics.disableScissor();
    }
    
    protected double toChildX(double x) {
        return (x - this.x - this.xOffset) / this.scale;
    }
    
    protected double toChildY(double y) {
        return (y - this.y - this.yOffset) / this.scale;
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
        if(!handled){
            this.hovered = this.isMouseOver(mouseX, mouseY);
            if(this.hovered) return true;
        }
        else hovered = false;
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
            this.xOffset += (float) dx;
            this.yOffset += (float) dy;
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
        var shiftDown = GuiSystem.INSTANCE.get().isShiftDown();
        var controlDown = GuiSystem.INSTANCE.get().isCtrlDown();
        if (scrollY != 0 && !shiftDown && !controlDown) {
            var oldScale = this.scale;
            var factor = 1f + scrollY * 0.1f;
            var target = this.clampScale((float) (this.scale * factor));
            if (target != oldScale) {
                var cx = this.toChildX(x);
                var cy = this.toChildY(y);
                this.scale = target;
                var nx = this.x + this.xOffset + cx * this.scale;
                var ny = this.y + this.yOffset + cy * this.scale;
                this.xOffset += (float) (x - nx);
                this.yOffset += (float) (y - ny);
                this.markDirty();
            }
            return true;
        }
        if (shiftDown && scrollY != 0) {
            this.yOffset += (float) (scrollY * 20.0);
            this.markDirty();
            return true;
        }
        if (controlDown && scrollY != 0) {
            this.xOffset += (float) (scrollY * 20.0);
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
    
}

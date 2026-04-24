package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.input.MouseButtonEvent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.Widget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AvailableSpace;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GuiWidgetClass
public class ScalableContainer extends AbsoluteContainer {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(ScalableContainer.class);
    
    protected float scale = 1;
    protected float minScale = 0.05f;
    protected float maxScale = 5f;
    protected float xOffset = 0;
    protected float yOffset = 0;
    protected boolean draggingPanel = false;
    protected boolean renderGrid = false;
    protected int gridColor = 0x404B5563;
    
    public ScalableContainer() {
        this.clampWidget = false;
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
    
    public ScalableContainer setGridEnabled(boolean value) {
        this.renderGrid = value;
        this.markDirty();
        return this;
    }
    
    public ScalableContainer setGridColor(int value) {
        this.gridColor = value;
        this.markDirty();
        return this;
    }
    
    protected float clampScale(float value) {
        return Math.clamp(value, minScale, maxScale);
    }
    
    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        for(var p : this.styleSheet.renderableProperty()){
            p.render(this,graphics,mouseX,mouseY,a);
        }
        var selfRect = this.getRectangle();
        graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        var mat = graphics.getPose().pushMatrix();
        mat.translate(this.x + this.xOffset, this.y + this.yOffset);
        mat.scale(this.scale, this.scale);
        if (this.renderGrid) {
            this.renderGrid(graphics);
        }
        for (Widget child : this.children.reversed()) {
            if (child.visible && (this.overflow() || child.getRectangle().transformAxisAligned(mat).intersects(selfRect))) {
                child.render(graphics, mouseX, mouseY, a);
            }
        }
        mat.popMatrix();
        graphics.disableScissor();
        this.scrollBarX.render(graphics, mouseX, mouseY, a);
        this.scrollBarY.render(graphics, mouseX, mouseY, a);
    }
    
    @Override
    public void renderBelow(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if (!this.visible) return;
//        super.renderBelow(graphics, mouseX, mouseY, a);
        var selfRect = this.getRectangle();
        graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        var mat = graphics.getPose().pushMatrix();
        mat.translate(this.x + this.xOffset, this.y + this.yOffset);
        mat.scale(this.scale, this.scale);
        for (Widget child : this.children.reversed()) {
            if (child.visible && (this.overflow() || child.getRectangle().transformAxisAligned(mat).intersects(selfRect))) {
                child.renderBelow(graphics, mouseX, mouseY, a);
            }
        }
        mat.popMatrix();
        graphics.disableScissor();
    }
    
    @Override
    public void renderAbove(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if (!this.visible) return;
//        super.renderAbove(graphics, mouseX, mouseY, a);
        var selfRect = this.getRectangle();
        graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        var mat = graphics.getPose().pushMatrix();
        mat.translate(this.x + this.xOffset, this.y + this.yOffset);
        mat.scale(this.scale, this.scale);
        for (Widget child : this.children.reversed()) {
            if (child.visible && (this.overflow() || child.getRectangle().transformAxisAligned(mat).intersects(selfRect))) {
                child.renderAbove(graphics, mouseX, mouseY, a);
            }
        }
        mat.popMatrix();
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
        if (!this.enabled || !this.visible) {
            this.setHovered(false);
            return false;
        }
        double cx = this.toChildX(mouseX);
        double cy = this.toChildY(mouseY);
        boolean handled = false;
        for (var child : this.children) {
            if (!handled && child.visible) {
                if (child.mouseMoved(cx, cy)) {
                    handled = true;
                    continue;
                }
            }
            if (child instanceof ContainerWidget acw) {
                acw.clearHoveredRecursive();
            } else {
                child.setHovered(false);
            }
        }
        if(!handled){
            this.setHovered(this.isMouseOver(mouseX, mouseY));
            if(this.hovered) return true;
        }
        else this.setHovered(false);
        return handled;
    }
    
    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        double mx = event.x();
        double my = event.y();
        if (!this.isMouseOver(mx, my)) {
            return false;
        }
        Widget clickedChild = null;
        IMouseButtonEvent mapped = new MouseButtonEvent(this.toChildX(mx), this.toChildY(my), event.button(), event.modifiers());
        for (Widget child : this.children) {
            if (child.visible && child.enabled) {
                if (child.mouseClicked(mapped, doubleClick)) {
                    this.draggingPanel = false;
                    clickedChild = child;
                    break;
                }
                
            }
        }
        if (clickedChild != null && autoReorder && this.children.size() > 1) {
            this.children.remove(clickedChild);
            this.children.addFirst(clickedChild);
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
        if (!this.enabled || !this.visible) {
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
        for (Widget child : this.children) {
            if (child.visible && child.enabled) {
                if (child.mouseReleased(mapped)) {
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
        IMouseButtonEvent mapped = new MouseButtonEvent(this.toChildX(event.x()), this.toChildY(event.y()), event.button(), event.modifiers());
        double cdx = dx / this.scale;
        double cdy = dy / this.scale;
        for (Widget child : this.children) {
            if (child.visible && child.enabled) {
                if (child.mouseDragged(mapped, cdx, cdy)) {
                    this.draggingPanel = false;
                    return true;
                }
            }
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
        if (!this.enabled || !this.visible) {
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
        for (Widget child : this.children) {
            if (child.visible && child.enabled) {
                if (child.mouseScrolled(cx, cy, scrollX, scrollY)) {
                    return true;
                }
                
            }
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }
    
    @Override
    public void resize(float offsetX, float offsetY) {
        for(var widget : this.children) {
            widget.tree.computeLayout(widget.nodeId,
                    TaffySize.of(AvailableSpace.definite(widget.getWidth()), AvailableSpace.definite(widget.getHeight())));
        }
        var layout = this.getLayout();
        if (layout != null) {
            this.setPosition(layout.contentBoxX() + offsetX, layout.contentBoxY() + offsetY);
            this.setSize(layout.contentBoxWidth(), layout.contentBoxHeight());
            for (var widget : this.children) {
                widget.resize(widget.getAbsoluteX(), widget.getAbsoluteY());
            }
            this.resizeScrollBar();
        }
    }
    
    protected void renderGrid(IGUIGraphics graphics) {
        float viewLeft = -this.xOffset / scale;
        float viewRight = (this.width - this.xOffset) / scale;
        float viewTop = -this.yOffset / scale;
        float viewBottom = (this.height - this.yOffset) / scale;
        float target = 100f / this.scale;
        double pow = Math.pow(10, Math.round(Math.log10(Math.max(target, 1e-6f))));
        float spacing = (float) pow;
        if (spacing <= 0) {
            return;
        }
        float startX = (float) Math.floor(viewLeft / spacing) * spacing;
        float startY = (float) Math.floor(viewTop / spacing) * spacing;
        var font = graphics.defaultFont();
        float textHeight = font.lineHeight() / this.scale;
        for (float x = startX; x <= viewRight; x += spacing) {
            graphics.renderLine(x, viewTop, x, viewBottom, this.gridColor);
            var label = String.valueOf((long) x);
            graphics.drawString(label, x + 2, viewTop + 2, this.gridColor, textHeight);
        }
        for (float y = startY; y <= viewBottom; y += spacing) {
            graphics.renderLine(viewLeft, y, viewRight, y, this.gridColor);
            var label = String.valueOf((long) y);
            graphics.drawString(label, viewLeft + 2, y + 2, this.gridColor, textHeight);
        }
    }
    
}

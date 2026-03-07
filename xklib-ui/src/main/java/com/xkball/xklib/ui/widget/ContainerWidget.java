package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.tree.TaffyTree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

public class ContainerWidget extends Widget {
    
    protected final List<Widget> children = new ArrayList<>();
    protected final Queue<Runnable> untilSetTree = new ArrayDeque<>();
    
    public ContainerWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    public ContainerWidget() {
        super();
    }
    
    private void untilSetTree(Runnable runnable) {
        if(this.tree == null) {
            untilSetTree.add(runnable);
        }
        else {
            runnable.run();
        }
    }
    
    public ContainerWidget addChild(Widget widget, TaffyStyle style){
        untilSetTree(() -> {
            widget.setTree(this.tree);
            var node = this.tree.newLeaf(style);
            this.tree.addChild(this.nodeId, node);
            widget.setNodeId(node);
            widget.setParent(this);
            widget.init();
            widget.setStyle(style);
            this.children.add(widget);
            this.focusNode.addChild(widget.getFocusNode());
            this.markDirty();
        });
        return this;
    }
    
    public ContainerWidget addChild(Widget widget) {
        return this.addChild(widget, widget.getStyle());
    }
    
    public void removeChild(Widget widget) {
        if(!this.children.contains(widget)) return;
        this.tree.removeChild(this.nodeId,widget.nodeId);
        this.children.remove(widget);
        this.focusNode.removeChild(widget.getFocusNode());
        widget.setParent(null);
        this.markDirty();
    }
    
    @Override
    public void afterTreeAndNodeSet() {
        super.afterTreeAndNodeSet();
        while (!this.untilSetTree.isEmpty()) {
            this.untilSetTree.poll().run();
        }
    }
    
    @Override
    public List<Widget> getChildren() {
        return this.children;
    }
    
    @Override
    public boolean mouseMoved(double mouseX, double mouseY) {
        if (!this.enabled || !this.visible) {
            this.hovered = false;
            return false;
        }
        boolean isMouseOver = this.isMouseOver(mouseX, mouseY);
        boolean handled = false;
        for (var child : this.children) {
            if (!handled && child.visible && isMouseOver) {
                if (child.mouseMoved(mouseX, mouseY)) {
                    handled = true;
                    continue;
                }
            }
            if (child instanceof ContainerWidget acw) {
                acw.clearHoveredRecursive();
            } else {
                child.hovered = false;
            }
        }
        
        if (!handled) {
            if (isMouseOver) {
                this.hovered = true;
                return true;
            }
        }
        
        this.hovered = false;
        return handled;
    }
    
    public void clearHoveredRecursive() {
        this.hovered = false;
        for (Widget child : this.children) {
            if (child instanceof ContainerWidget acw) {
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
        if (!this.isMouseOver(x, y)) return false;
        
        for (Widget child : this.children) {
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
        if (!this.isMouseOver(x, y)) return false;
        
        for (Widget child : this.children) {
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
        if (!this.isMouseOver(x, y)) return false;
        
        for (Widget child : this.children) {
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
        if (!this.isMouseOver(x, y)) return false;
        
        for (Widget child : this.children) {
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
        if (!this.enabled || !this.visible || !this.isFocused()) {
            return false;
        }
        
        for (Widget child : this.children) {
            if (child.visible && child.enabled) {
                if (child.keyPressed(event)) {
                    return true;
                }
            }
        }
        
        return super.keyPressed(event);
    }
    
    @Override
    public boolean keyReleased(IKeyEvent event) {
        if (!this.enabled || !this.visible || !this.isFocused()) {
            return false;
        }
        
        for (Widget child : this.children) {
            if (child.visible && child.enabled) {
                if (child.keyReleased(event)) {
                    return true;
                }
            }
        }
        
        return super.keyReleased(event);
    }
    
    @Override
    public boolean charTyped(ICharEvent event) {
        if (!this.enabled || !this.visible || !this.isFocused()) {
            return false;
        }
        
        for (Widget child : this.children) {
            if (child.charTyped(event)) {
                return true;
            }
        }
        
        return super.charTyped(event);
    }
    
    @Override
    public void resize(float offsetX, float offsetY) {
        var layout = this.tree.getLayout(this.nodeId);
        if (layout != null) {
            this.setPosition(layout.contentBoxX() + offsetX, layout.contentBoxY() + offsetY);
            this.setSize(layout.contentBoxWidth(), layout.contentBoxHeight());
            for (Widget child : this.children) {
                child.resize(offsetX + this.x, offsetY + this.y);
            }
        }
    }
    
    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        var selfRect = this.getRectangle();
        this.renderInScissor(graphics, () -> {
            for (Widget child : this.children) {
                if (child.visible && (this.overflow() || child.getRectangle().intersects(selfRect))) {
                    child.render(graphics, mouseX, mouseY, a);
                }
            }
        });
        
    }
    
    @Override
    public void renderBelow(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.renderBelow(graphics, mouseX, mouseY, a);
        var selfRect = this.getRectangle();
        this.renderInScissor(graphics, () -> {
            for (Widget child : this.children) {
                if (child.visible && (this.overflow() || child.getRectangle().intersects(selfRect))) {
                    child.renderBelow(graphics, mouseX, mouseY, a);
                }
            }
        });
    }
    
    @Override
    public void renderAbove(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.renderAbove(graphics, mouseX, mouseY, a);
        var selfRect = this.getRectangle();
        this.renderInScissor(graphics, () -> {
            for (Widget child : this.children) {
                if (child.visible && (this.overflow() || child.getRectangle().intersects(selfRect))) {
                    child.renderAbove(graphics, mouseX, mouseY, a);
                }
            }
        });
    }
    
    @Override
    public void visitWidgets(Consumer<IGuiWidget> widgetVisitor) {
        widgetVisitor.accept(this);
        for (Widget child : this.children) {
            child.visitWidgets(widgetVisitor);
        }
    }
}


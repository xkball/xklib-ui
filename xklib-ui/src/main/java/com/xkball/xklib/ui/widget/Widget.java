package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.layout.FocusNode;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.api.gui.widget.IDecoration;
import com.xkball.xklib.api.gui.widget.IGuiEventListener;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.api.gui.widget.IRenderable;
import com.xkball.xklib.ui.deco.CombinedDecoration;
import com.xkball.xklib.ui.system.GuiSystem;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.tree.NodeId;
import dev.vfyjxf.taffy.tree.TaffyTree;

public class Widget implements IGuiWidget, IRenderable, IGuiEventListener {
    
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected boolean enabled = true;
    protected boolean visible = true;
    protected boolean hovered = false;
    protected boolean overflow = true;
    
    protected TaffyTree tree = null;
    protected TaffyStyle style = new TaffyStyle();
    protected NodeId nodeId = null;
    protected IDecoration decoration;
    protected IGuiWidget parent = null;
    protected FocusNode focusNode;
    
    public Widget(){
        this(0, 0, 0, 0);
    }
    
    public Widget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.focusNode = new FocusNode(null);
        this.focusNode.widget = this;
        this.focusNode.setCanTakePrimaryFocus(this.isFocusable());
    }
    
    @Override
    public boolean mouseMoved(double mouseX, double mouseY) {
        boolean wasMouseOver = this.isMouseOver(mouseX, mouseY);
        if (wasMouseOver && this.enabled && this.visible) {
            this.hovered = true;
            return true;
        }
        this.hovered = false;
        return false;
    }
    
    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        if (this.isMouseOver(event.x(), event.y())) {
            GuiSystem.INSTANCE.get().getFocusManager().takeFocus(this.getFocusNode());
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
        if (!this.enabled || !this.visible || !this.isPrimaryFocused()) {
            return false;
        }
        return this.onKeyPressed(event);
    }
    
    @Override
    public boolean keyReleased(IKeyEvent event) {
        if (!this.enabled || !this.visible || !this.isPrimaryFocused()) {
            return false;
        }
        return this.onKeyReleased(event);
    }
    
    @Override
    public boolean charTyped(ICharEvent event) {
        if (!this.enabled || !this.visible || !this.isPrimaryFocused()) {
            return false;
        }
        return this.onCharTyped(event);
    }
    
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.enabled && this.visible && getRectangle().containsPoint((int) mouseX, (int) mouseY);
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
    public void setX(float x) {
        this.x = x;
    }
    
    @Override
    public void setY(float y) {
        this.y = y;
    }
    
    @Override
    public float getX() {
        return this.x;
    }
    
    @Override
    public float getY() {
        return this.y;
    }
    
    @Override
    public void setWidth(float width) {
        this.width = width;
    }
    
    @Override
    public void setHeight(float height) {
        this.height = height;
    }
    
    @Override
    public float getWidth() {
        return this.width;
    }
    
    @Override
    public float getHeight() {
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
    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }
    
    @Override
    public boolean isHovered() {
        return this.hovered;
    }
    
    @Override
    public FocusNode getFocusNode() {
        return this.focusNode;
    }
    
    @Override
    public void setNodeId(NodeId nodeId) {
        this.nodeId = nodeId;
        if(this.tree != null) {
            this.afterTreeAndNodeSet();
        }
    }
    
    @Override
    public NodeId getNodeId() {
        return this.nodeId;
    }
    
    @Override
    public boolean isFocused() {
        return this.focusNode.isFocused();
    }
    
    @Override
    public boolean isPrimaryFocused() {
        return this.focusNode.isPrimaryFocused();
    }
    
    @Override
    public void markDirty() {
        this.tree.markDirty(nodeId);
    }
    
    @Override
    public boolean isDirty() {
        return this.tree.isDirty(nodeId);
    }
    
    @Override
    public void setOverflow(boolean overflow) {
        this.overflow = overflow;
    }
    
    @Override
    public boolean overflow() {
        return this.overflow;
    }
    
    public void afterTreeAndNodeSet(){
        this.tree.setStyle(this.nodeId, this.style);
    }
    
    @Override
    public void setStyle(TaffyStyle style) {
        this.style = style;
        if(this.tree != null && this.nodeId != null) {
            this.tree.setStyle(this.nodeId, this.style);
        }
    }
    
    @Override
    public TaffyStyle getStyle() {
        return this.style;
    }
    
    @Override
    public void setTree(TaffyTree tree) {
        this.tree = tree;
        if(this.nodeId != null) {
            this.afterTreeAndNodeSet();
        }
    }
    
    @Override
    public TaffyTree getTree() {
        return this.tree;
    }
    
    @Override
    public void addDecoration(IDecoration deco) {
        if(this.decoration == null){
            this.decoration = new CombinedDecoration();
            ((CombinedDecoration)this.decoration).addDecoration(deco);
        }
        else{
            ((CombinedDecoration)this.decoration).addDecoration(deco);
        }
    }
    
    @Override
    public ScreenRectangle getRectangle() {
        return IGuiWidget.super.getRectangle();
    }
    
    @Override
    public void resize(float offsetX, float offsetY) {
        var layout = this.getLayout();
        if (layout != null) {
            this.setPosition(layout.contentBoxX() + offsetX, layout.contentBoxY() + offsetY);
            this.setSize(layout.contentBoxWidth(), layout.contentBoxHeight());
        }
    }
    
    @Override
    public final void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if(!this.visible) return;
        if (this.overflow()){
            this.doRender(graphics, mouseX, mouseY, a);
        }
        else {
            this.renderInScissor(graphics,() -> this.doRender(graphics, mouseX, mouseY, a));
        }
        
    }
    
    @Override
    public void renderAbove(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if(this.hovered){
            this.renderDebug(graphics, mouseX, mouseY);
        }
    }
    
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a){
        if(this.decoration != null){
            this.decoration.render(this, graphics, mouseX, mouseY, a);
        }
    }
    
    @Override
    public void renderDebug(IGUIGraphics graphics, int mouseX, int mouseY) {
        int marginColor = 0x80FFA500;
        int paddingColor = 0x8000FF00;
        int contentColor = 0x800000FF;
        var layout = this.tree.getLayout(this.nodeId);
        if (layout != null) {
            var offsetX = this.x - layout.contentBoxX();
            var offsetY = this.y - layout.contentBoxY();
            graphics.renderOutline(layout.margin().left + offsetX, layout.margin().top + offsetY, layout.margin().right - layout.margin().left, layout.margin().bottom - layout.margin().top, marginColor);
            graphics.renderOutline(layout.padding().left + offsetX, layout.padding().top + offsetY, layout.padding().right - layout.padding().left, layout.padding().bottom - layout.padding().top, paddingColor);
        }
        graphics.renderOutline(this.x, this.y, this.width, this.height, contentColor);
    
    }
    
    public IGuiWidget getParent(){
        return this.parent;
    }
    
    public void setParent(IGuiWidget widget){
        this.parent = widget;
    }
    
    public void renderInScissor(IGUIGraphics graphics, Runnable renderer){
        graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        renderer.run();
        graphics.disableScissor();
    }
}

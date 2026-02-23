package com.xkball.xklib.ui.widget;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.api.gui.widget.IDecoration;
import com.xkball.xklib.api.gui.widget.IGuiEventListener;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.api.gui.widget.ILayoutElement;
import com.xkball.xklib.api.gui.widget.IRenderable;
import com.xkball.xklib.ui.deco.CombinedDecoration;
import com.xkball.xklib.ui.layout.HorizontalAlign;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import com.xkball.xklib.ui.layout.VerticalAlign;

public class AbstractWidget implements IGuiWidget, IRenderable, IGuiEventListener, ILayoutElement {
    
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean enabled = true;
    protected boolean visible = true;
    protected boolean focused = false;
    protected boolean hovered = false;
    protected volatile boolean dirty = false;
    
    protected int contentX;
    protected int contentY;
    protected int contentWidth;
    protected int contentHeight;
    protected boolean useFixWidth = false;
    protected boolean useFixHeight = false;
    protected int fixWidth;
    protected int fixHeight;
    protected HorizontalAlign innerHorizontalAlign = HorizontalAlign.CENTER;
    protected VerticalAlign innerVerticalAlign = VerticalAlign.CENTER;
    protected boolean paddingLeftPercent = false;
    protected boolean paddingRightPercent = false;
    protected boolean paddingTopPercent = false;
    protected boolean paddingBottomPercent = false;
    protected float paddingLeft;
    protected float paddingRight;
    protected float paddingTop;
    protected float paddingBottom;
    public boolean marginLeftPercent = false;
    public boolean marginRightPercent = false;
    public boolean marginTopPercent = false;
    public boolean marginBottomPercent = false;
    public float marginLeft;
    public float marginRight;
    public float marginTop;
    public float marginBottom;
    protected boolean overflow = true;
    public IDecoration decoration;
    
    public AbstractWidget(){
        this.markDirty();
    }
    
    public AbstractWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.markDirty();
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
               mouseX >= this.contentX && mouseX < this.contentX + this.contentWidth &&
               mouseY >= this.contentY && mouseY < this.contentY + this.contentHeight;
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
    public boolean isFocusable() {
        return true;
    }
    
    @Override
    public void setFocused(boolean focused) {
        if (focused && !this.isFocusable()) {
            return;
        }
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
    public void setOverflow(boolean overflow) {
        this.overflow = overflow;
    }
    
    @Override
    public boolean overflow() {
        return this.overflow;
    }
    
    @Override
    public int expectWidth() {
        return 0;
    }
    
    @Override
    public int expectHeight() {
        return 0;
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
    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        if(this.decoration != null){
            this.decoration.render(this, graphics, mouseX, mouseY, a);
        }
        if(XKLib.gui.isDebug()){
            this.renderDebug(graphics, mouseX, mouseY);
        }
    }
    
    @Override
    public void renderDebug(IGUIGraphics graphics, int mouseX, int mouseY) {
        if (this.hovered) {
            int marginColor = 0x80FFA500;
            int paddingColor = 0x8000FF00;
            int contentColor = 0x800000FF;
            
            graphics.renderOutline(this.x, this.y, this.width, this.height, paddingColor);
            graphics.renderOutline(this.contentX, this.contentY, this.contentWidth, this.contentHeight, contentColor);
        }
    }
    
    @Override
    public void resize() {
        if(this.useFixWidth){
            this.contentWidth = this.fixWidth;
            this.contentX = switch (this.innerHorizontalAlign) {
                case LEFT -> this.x;
                case CENTER -> (int) (this.x + (this.width - this.fixWidth) / 2.0);
                case RIGHT -> this.x + this.width - this.fixWidth;
            };
        }
        else{
            var paddingL = this.paddingLeftPercent ? (int)(this.paddingLeft * this.width) : this.paddingLeft;
            var paddingR = this.paddingRightPercent ? (int)(this.paddingRight * this.width) : this.paddingRight;
            this.contentX = (int) (this.x + paddingL);
            this.contentWidth = (int) (Math.max(0, this.width - paddingL - paddingR));
        }
        if(this.useFixHeight){
            this.contentHeight = this.fixHeight;
            this.contentY = switch (this.innerVerticalAlign) {
                case TOP -> this.y;
                case CENTER -> (int) (this.y + (this.height - this.fixHeight) / 2.0);
                case BOTTOM -> this.y + this.height - this.fixHeight;
            };
        }
        else{
            var paddingT = this.paddingTopPercent ? (int)(this.paddingTop * this.height) : this.paddingTop;
            var paddingB = this.paddingBottomPercent ? (int)(this.paddingBottom * this.height) : this.paddingBottom;
            this.contentY = (int) (this.y + paddingT);
            this.contentHeight = (int) (Math.max(0, this.height - paddingT - paddingB));
        }
        
    }
    
    @Override
    public int getContentX() {
        return this.contentX;
    }
    
    @Override
    public int getContentY() {
        return this.contentY;
    }
    
    @Override
    public int getContentWidth() {
        return this.contentWidth;
    }
    
    @Override
    public int getContentHeight() {
        return this.contentHeight;
    }
    
    @Override
    public void setFixWidth(int width) {
        this.fixWidth = width;
        this.useFixWidth = true;
    }
    
    @Override
    public void setFixHeight(int height) {
        this.fixHeight = height;
        this.useFixHeight = true;
    }
    
    @Override
    public void setInnerHorizontalAlign(HorizontalAlign align) {
        this.innerHorizontalAlign = align;
    }
    
    @Override
    public void setInnerVerticalAlign(VerticalAlign align) {
        this.innerVerticalAlign = align;
    }
    
    @Override
    public void setPaddingLeft(int padding) {
        this.paddingLeft = padding;
        this.paddingLeftPercent = false;
    }
    
    @Override
    public void setPaddingRight(int padding) {
        this.paddingRight = padding;
        this.paddingRightPercent = false;
    }
    
    @Override
    public void setPaddingTop(int padding) {
        this.paddingTop = padding;
        this.paddingTopPercent = false;
    }
    
    @Override
    public void setPaddingBottom(int padding) {
        this.paddingBottom = padding;
        this.paddingBottomPercent = false;
    }
    
    @Override
    public void setPaddingLeftPercent(float percent) {
        this.paddingLeftPercent = true;
        this.paddingLeft = percent;
    }
    
    @Override
    public void setPaddingRightPercent(float percent) {
        this.paddingRightPercent = true;
        this.paddingRight = percent;
    }
    
    @Override
    public void setPaddingTopPercent(float percent) {
        this.paddingTopPercent = true;
        this.paddingTop = percent;
    }
    
    @Override
    public void setPaddingBottomPercent(float percent) {
        this.paddingBottomPercent = true;
        this.paddingBottom = percent;
    }
    
    @Override
    public void setMarginLeft(int margin) {
        this.marginLeft = margin;
        this.marginLeftPercent = false;
    }
    
    @Override
    public void setMarginRight(int margin) {
        this.marginRight = margin;
        this.marginRightPercent = false;
    }
    
    @Override
    public void setMarginTop(int margin) {
        this.marginTop = margin;
        this.marginTopPercent = false;
    }
    
    @Override
    public void setMarginBottom(int margin) {
        this.marginBottom = margin;
        this.marginBottomPercent = false;
    }
    
    @Override
    public void setMarginLeftPercent(float percent) {
        this.marginLeftPercent = true;
        this.marginLeft = percent;
    }
    
    @Override
    public void setMarginRightPercent(float percent) {
        this.marginRightPercent = true;
        this.marginRight = percent;
    }
    
    @Override
    public void setMarginTopPercent(float percent) {
        this.marginTopPercent = true;
        this.marginTop = percent;
    }
    
    @Override
    public void setMarginBottomPercent(float percent) {
        this.marginBottomPercent = true;
        this.marginBottom = percent;
    }
    
    public void renderInScissor(IGUIGraphics graphics, Runnable renderer){
        var flag = !this.overflow();
        if(flag){
            graphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        }
        renderer.run();
        if(flag){
            graphics.disableScissor();
        }
    }
}

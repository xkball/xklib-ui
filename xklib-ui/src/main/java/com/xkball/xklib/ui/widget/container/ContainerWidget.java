package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.api.gui.input.ICharEvent;
import com.xkball.xklib.api.gui.input.IKeyEvent;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.widget.IGuiEventListener;
import com.xkball.xklib.api.gui.widget.IRenderable;
import com.xkball.xklib.ui.css.CascadingStyleSheets;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import com.xkball.xklib.ui.system.GuiSystem;
import com.xkball.xklib.ui.widget.Widget;
import dev.vfyjxf.taffy.geometry.TaffyPoint;
import dev.vfyjxf.taffy.style.Overflow;
import dev.vfyjxf.taffy.style.TaffyStyle;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ContainerWidget extends Widget {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerWidget.class);
    
    protected final List<Widget> children = new ArrayList<>();
    protected float xScrollOffset = 0;
    protected float yScrollOffset = 0;
    protected ScrollBar scrollBarX = new ScrollBar(false);
    protected ScrollBar scrollBarY = new ScrollBar(true);
    protected int scrollBarTrackColor = 0xFF2D2D2D;
    protected int scrollBarThumbColor = 0xFF888888;
    protected int scrollBarThumbHoverColor = 0xFFAAAAAA;
    
    
    public ContainerWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    public ContainerWidget() {
        super();
    }
    
    @Override
    public ContainerWidget inlineStyle(String style) {
        super.inlineStyle(style);
        return this;
    }
    
    @Override
    public ContainerWidget asRootStyle(String style) {
        super.asRootStyle(style);
        return this;
    }
    
    public void setScrollBarTrackColor(int scrollBarTrackColor) {
        this.scrollBarTrackColor = scrollBarTrackColor;
    }

    public void setScrollBarThumbColor(int scrollBarThumbColor) {
        this.scrollBarThumbColor = scrollBarThumbColor;
    }

    public void setScrollBarThumbHoverColor(int scrollBarThumbHoverColor) {
        this.scrollBarThumbHoverColor = scrollBarThumbHoverColor;
    }
    
    /**
     * 会覆盖组件原有的样式, 外部不要调用
     */
    protected ContainerWidget addChild(Widget widget, TaffyStyle style){
        untilSetTree(() -> {
            if(this.children.contains(widget)) return;
            widget.setParent(this);
            widget.setTree(this.tree);
            var node = this.tree.newLeaf(style);
            this.tree.addChild(this.nodeId, node);
//            LOGGER.debug("{} added child: {}", this.getName(), widget.getName());
            widget.setNodeId(node);
            widget.init();
            widget.setStyle(style);
            this.children.add(widget);
            this.focusNode.addChild(widget.getFocusNode());
            this.markDirty();
        });
        return this;
    }
    
    public ContainerWidget addChild(Supplier<Widget> widgetSupplier){
        return this.addChild(widgetSupplier.get());
    }
    
    public ContainerWidget addChild(Widget widget) {
        return this.addChild(widget, widget.getStyle());
    }
    
    /**
     * 被删除的组件不应该再被复用, 内部树结构已经被破坏
     */
    public void removeChild(Widget widget) {
        if(!this.children.contains(widget)) return;
//        LOGGER.debug("{} removed child: {}", this.getName(), widget.getName());
        this.tree.removeChild(this.nodeId,widget.nodeId);
        this.children.remove(widget);
        widget.onRemove();
        this.focusNode.removeChild(widget.getFocusNode());
        widget.setParent(null);
        this.markDirty();
    }
    
    public void clearChildren() {
        for(var widget : this.children) {
            this.tree.removeChild(this.nodeId,widget.nodeId);
            this.focusNode.removeChild(widget.getFocusNode());
            widget.setParent(null);
        }
        this.children.clear();
    }
    
    @Override
    public void afterTreeAndNodeSet() {
        super.afterTreeAndNodeSet();
        
    }
    
    
    
    @Override
    public List<Widget> getChildren() {
        return this.children;
    }
    
    public void setXScrollEnable(){
        this.setXScrollEnable(true);
    }
    
    public void setYScrollEnable(){
        this.setYScrollEnable(true);
    }
    
    public void setXScrollEnable(boolean xScroll) {
        this.setStyle(s -> {
            if(xScroll) {
                s.overflow = new TaffyPoint<>(Overflow.SCROLL,s.overflow.y);
                s.scrollbarWidth = 8;
            }
            else{
                s.overflow = new TaffyPoint<>(Overflow.VISIBLE,s.overflow.y);
                this.xScrollOffset = 0;
            }
        });
    }
    
    public void setYScrollEnable(boolean yScroll) {
        this.setStyle(s -> {
            if(yScroll) {
                s.overflow = new TaffyPoint<>(s.overflow.x,Overflow.SCROLL);
                s.scrollbarWidth = 8;
            }
            else{
                s.overflow = new TaffyPoint<>(s.overflow.x,Overflow.VISIBLE);
                this.yScrollOffset = 0;
            }
        });
    }
    
    public boolean scrollX(){
        return this.style.overflow.x == Overflow.SCROLL;
    }
    
    public boolean scrollY(){
        return this.style.overflow.y == Overflow.SCROLL;
    }
    
    @Override
    public boolean mouseMoved(double mouseX, double mouseY) {
        if (!this.enabled || !this.visible) {
            this.setHovered(false);
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
                child.setHovered(false);
            }
        }
        
        if (!handled) {
            if (isMouseOver) {
                this.setHovered(true);
                return true;
            }
        }
        
        this.setHovered(false);
        return handled;
    }
    
    public void clearHoveredRecursive() {
        this.setHovered(false);
        for (Widget child : this.children) {
            if (child instanceof ContainerWidget acw) {
                acw.clearHoveredRecursive();
            } else {
                child.setHovered(false);
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
    public boolean preeditUpdated(@Nullable Object event) {
        if (!this.enabled || !this.visible || !this.isFocused()) {
            return false;
        }
        
        for (Widget child : this.children) {
            if (child.preeditUpdated(event)) {
                return true;
            }
        }
        
        return super.preeditUpdated(event);
    }
    
    public void scrollToX(float x){
        var layout = this.getLayout();
        this.xScrollOffset = Math.clamp(x, 0, layout.scrollWidth());
    }
    
    public void scrollToY(float y){
        var layout = this.getLayout();
        this.yScrollOffset = Math.clamp(y, 0, layout.scrollHeight());
    }
    
    public void scrollToBottom(){
        var layout = this.getLayout();
        if(layout == null) return;
        this.yScrollOffset = layout.scrollHeight();
    }
    
    public void autoScrollToBottom(){
        var layout = this.getLayout();
        if(layout == null) return;
        if (this.yScrollOffset + 50 > layout.scrollHeight()){
            this.scrollToBottom();
        }
    }
    
    @Override
    protected boolean onMouseScrolled(double x, double y, double scrollX, double scrollY) {
        var layout = this.getLayout();
        var doScrollX = this.scrollX() && layout.scrollWidth() > 0;
        var doScrollY = this.scrollY() && layout.scrollHeight() > 0;
        var isShiftDown = GuiSystem.INSTANCE.get().isShiftDown();
        if(!doScrollX && !doScrollY) return false;
        else if(doScrollX && !doScrollY){
            this.xScrollOffset = Math.clamp(this.xScrollOffset + (float) (scrollY + scrollX)  * -20f, 0, layout.scrollWidth());
        }
        else if(!doScrollX){
            this.yScrollOffset = Math.clamp(this.yScrollOffset + (float) (scrollY)  * -20f, 0, layout.scrollHeight());
        }
        else {
            if(isShiftDown){
                this.xScrollOffset = Math.clamp(this.xScrollOffset + (float) (scrollY + scrollX)  * -20f, 0, layout.scrollWidth());
            }
            else{
                this.yScrollOffset = Math.clamp(this.yScrollOffset + (float) (scrollY)  * -20f, 0, layout.scrollHeight());
            }
        }
        return true;
    }
    
    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        var flag = false;
        flag |= scrollBarX.mouseDragged(event, dx, dy);
        flag |= scrollBarY.mouseDragged(event, dx, dy);
        return flag;
    }
    
    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        var flag = false;
        if(this.scrollBarX.getRectangle().containsPoint((int) event.x(), (int) event.y())){
            flag |= scrollBarX.mouseClicked(event, doubleClick);
        }
        else{
            scrollBarX.dragging = false;
        }
        if(this.scrollBarY.getRectangle().containsPoint((int) event.x(), (int) event.y())){
            flag |= scrollBarY.mouseClicked(event, doubleClick);
        }
        else {
            scrollBarY.dragging = false;
        }
        return flag;
    }
    
    @Override
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        var flag = false;
        if(this.scrollBarX.getRectangle().containsPoint((int) event.x(), (int) event.y())){
            flag |= scrollBarX.mouseReleased(event);
        }
        if(this.scrollBarY.getRectangle().containsPoint((int) event.x(), (int) event.y())){
            flag |= scrollBarY.mouseReleased(event);
        }
        return flag;
    }
    
    @Override
    public void onFocusChanged(boolean focused) {
        if(!focused){
            this.scrollBarX.dragging = false;
            this.scrollBarY.dragging = false;
        }
    }
    
    @Override
    public void resize(float offsetX, float offsetY) {
        var layout = this.getLayout();
        if (layout != null) {
            this.setPosition(layout.contentBoxX() + offsetX, layout.contentBoxY() + offsetY);
            this.setSize(layout.contentBoxWidth(), layout.contentBoxHeight());
            for (Widget child : this.children) {
                child.resize(this.x - layout.padding().left - layout.border().left - this.xScrollOffset, this.y - layout.padding().top - layout.border().top - this.yScrollOffset);
            }
            this.resizeScrollBar();
        }
    }
    
    public void resizeScrollBar(){
        var layout = this.getLayout();
        if(this.scrollX() && layout.scrollWidth() > 0){
            this.scrollBarX.x = this.getX();
            this.scrollBarX.y = this.getMaxY() - layout.scrollbarSize().height;
            this.scrollBarX.width = this.width;
            this.scrollBarX.height = layout.scrollbarSize().height;
            this.scrollBarX.maxScroll = layout.scrollWidth();
            this.scrollBarX.scroll = this.xScrollOffset;
        }
        else{
            this.scrollBarX.clear();
            this.xScrollOffset = 0;
        }
        if(this.scrollY() && layout.scrollHeight() > 0){
            this.scrollBarY.x = this.getMaxX() - layout.scrollbarSize().width;
            this.scrollBarY.y = this.getY();
            this.scrollBarY.width = layout.scrollbarSize().width;
            this.scrollBarY.height = this.height;
            this.scrollBarY.maxScroll = layout.scrollHeight();
            this.scrollBarY.scroll = this.yScrollOffset;
        }
        else {
            this.scrollBarY.clear();
            this.yScrollOffset = 0;
        }
    }
    
    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        var selfRect = this.getRectangle();
        if(!this.children.isEmpty()){
            this.renderInScissor(graphics, () -> {
                for (Widget child : this.children.reversed()) {
                    if (child.visible && child.getRectangle().intersects(selfRect)) {
                        child.render(graphics, mouseX, mouseY, a);
                    }
                }
            });
        }
        this.scrollBarX.render(graphics, mouseX, mouseY, a);
        this.scrollBarY.render(graphics, mouseX, mouseY, a);
    }
    
    @Override
    public void renderBelow(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.renderBelow(graphics, mouseX, mouseY, a);
        var selfRect = this.getRectangle();
        this.renderInScissor(graphics, () -> {
            for (Widget child : this.children.reversed()) {
                if (child.visible && child.getRectangle().intersects(selfRect)) {
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
            for (Widget child : this.children.reversed()) {
                if (child.visible && child.getRectangle().intersects(selfRect)) {
                    child.renderAbove(graphics, mouseX, mouseY, a);
                }
            }
        });
    }
    
    @Override
    public void visitWidgets(Consumer<IGuiWidget> widgetVisitor) {
        for (Widget child : this.children) {
            child.visitWidgets(widgetVisitor);
        }
        widgetVisitor.accept(this);
    }
    
    @Override
    public void updateStyle(CascadingStyleSheets sheet) {
        super.updateStyle(sheet);
        var asRoot = this.styleSheetAsRoot;
        var sheet_  = sheet;
        if(!asRoot.sheets().isEmpty()){
            sheet_ = new CascadingStyleSheets();
            sheet_.addAll(sheet);
            sheet_.addAll(asRoot);
        }
        for(var w : this.children){
            w.updateStyle(sheet_);
        }
    }
    
    public class ScrollBar implements IGuiEventListener, IRenderable {

        float x;
        float y;
        float width;
        float height;
        boolean vertical;
        float maxScroll;
        float scroll;
        boolean dragging = false;
        float dragStartMouse;
        float dragStartScroll;

        public ScrollBar(boolean vertical) {
            this.vertical = vertical;
        }
        
        public void clear(){
            this.x = 0;
            this.y = 0;
            this.width = 0;
            this.height = 0;
            this.scroll = 0;
            this.dragging = false;
            this.maxScroll = 0;
        }

        private float thumbSize() {
            if (vertical) {
                float ratio = maxScroll > 0 ? height / (height + maxScroll) : 1f;
                return Math.max(20f, height * ratio);
            } else {
                float ratio = maxScroll > 0 ? width / (width + maxScroll) : 1f;
                return Math.max(20f, width * ratio);
            }
        }

        private float thumbOffset() {
            float trackLen;
            if (vertical) {
                trackLen = height - thumbSize();
            } else {
                trackLen = width - thumbSize();
            }
            return trackLen > 0 ? (scroll / maxScroll) * trackLen : 0f;
        }

        private boolean isMouseOverThumb(double mouseX, double mouseY) {
            float offset = thumbOffset();
            float ts = thumbSize();
            if (vertical) {
                float ty = y + offset;
                return mouseX >= x && mouseX < x + width && mouseY >= ty && mouseY < ty + ts;
            } else {
                float tx = x + offset;
                return mouseX >= tx && mouseX < tx + ts && mouseY >= y && mouseY < y + height;
            }
        }

        @Override
        public ScreenRectangle getRectangle() {
            return new ScreenRectangle((int) x, (int) y, (int) width, (int) height);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }

        @Override
        public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            if (width == 0 || height == 0) return false;
            if (!isMouseOver(event.x(), event.y())) return false;
            if (isMouseOverThumb(event.x(), event.y())) {
                dragging = true;
                dragStartMouse = vertical ? (float) event.y() : (float) event.x();
                dragStartScroll = scroll;
                return true;
            }
            float offset = thumbOffset();
            float ts = thumbSize();
            if (vertical) {
                float ty = y + offset;
                if (event.y() < ty) {
                    scroll = Math.max(0, scroll - height);
                } else if (event.y() >= ty + ts) {
                    scroll = Math.min(maxScroll, scroll + height);
                }
            } else {
                float tx = x + offset;
                if (event.x() < tx) {
                    scroll = Math.max(0, scroll - width);
                } else if (event.x() >= tx + ts) {
                    scroll = Math.min(maxScroll, scroll + width);
                }
            }
            syncScrollToContainer();
            return true;
        }

        @Override
        public boolean mouseDragged(IMouseButtonEvent event, double dx, double dy) {
            if (!dragging) return false;
            float ts = thumbSize();
            if (vertical) {
                float trackLen = height - ts;
                if (trackLen <= 0) return true;
                float delta = (float) event.y() - dragStartMouse;
                scroll = Math.clamp(dragStartScroll + delta * maxScroll / trackLen, 0, maxScroll);
            } else {
                float trackLen = width - ts;
                if (trackLen <= 0) return true;
                float delta = (float) event.x() - dragStartMouse;
                scroll = Math.clamp(dragStartScroll + delta * maxScroll / trackLen, 0, maxScroll);
            }
            syncScrollToContainer();
            return true;
        }

        @Override
        public boolean mouseReleased(IMouseButtonEvent event) {
            dragging = false;
            return false;
        }

        private void syncScrollToContainer() {
            if (vertical) {
                ContainerWidget.this.yScrollOffset = scroll;
            } else {
                ContainerWidget.this.xScrollOffset = scroll;
            }
        }

        @Override
        public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            if (width == 0 || height == 0) return;
            graphics.fill(x, y, x + width, y + height, ContainerWidget.this.scrollBarTrackColor);
            float offset = thumbOffset();
            float ts = thumbSize();
            boolean hover = isMouseOverThumb(mouseX, mouseY) || dragging;
            int thumbColor = hover ? ContainerWidget.this.scrollBarThumbHoverColor : ContainerWidget.this.scrollBarThumbColor;
            if (vertical) {
                graphics.fill(x, y + offset, x + width, y + offset + ts, thumbColor);
            } else {
                graphics.fill(x + offset, y, x + offset + ts, y + height, thumbColor);
            }
        }
    }
}


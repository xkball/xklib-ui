package com.xkball.xklib.ui.widget.layout;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.backend.gl.OpenGLWorkaround;
import com.xkball.xklib.ui.layout.FlexElementParam;
import com.xkball.xklib.ui.layout.FlexParam;
import com.xkball.xklib.ui.layout.GridElementParam;
import com.xkball.xklib.ui.layout.GridParam;
import com.xkball.xklib.ui.widget.AbstractWidget;
import org.lwjgl.glfw.GLFW;

public class ScrollableFlexLayout extends GridLayout {
    
    public boolean xScrollable = false;
    public boolean yScrollable = false;
    public boolean xScrollBarVisible = true;
    public boolean yScrollBarVisible = true;
    public int xScrollBarSize = 10;
    public int yScrollBarSize = 10;
    public FlexLayout inner;
    
    private ScrollBar xScrollBar;
    private ScrollBar yScrollBar;
    private boolean lastXBarVisible = false;
    private boolean lastYBarVisible = false;
    
    public ScrollableFlexLayout() {
        this(new FlexParam.Builder().build());
    }
    
    public ScrollableFlexLayout(FlexParam param) {
        this.inner = new FlexLayout(param){
            @Override
            public void markDirty() {
                super.markDirty();
                ScrollableFlexLayout.this.markDirty();
            }
        };
        this.xScrollBar = new ScrollBar(true);
        this.yScrollBar = new ScrollBar(false);
    }
    
    public ScrollableFlexLayout setFlexParam(FlexParam param) {
        this.inner.setFlexParam(param);
        return this;
    }
    
    public ScrollableFlexLayout addChild(AbstractWidget widget, FlexElementParam param) {
        this.inner.addChild(widget, param);
        return this;
    }
    
    @Override
    public void init() {
        super.init();
        this.rebuildLayout();
    }
    
    private void rebuildLayout() {
        this.children.clear();
        
        int availableWidth = this.contentWidth > 0 ? this.contentWidth : this.width;
        int availableHeight = this.contentHeight > 0 ? this.contentHeight : this.height;
        
        boolean needXBar = this.xScrollable && this.xScrollBarVisible && this.inner.actualWidth > availableWidth;
        boolean needYBar = this.yScrollable && this.yScrollBarVisible && this.inner.actualHeight > availableHeight;
        
        GridParam.Builder builder = new GridParam.Builder();
        
        if (needYBar) {
            builder.addCol("1").addCol(this.yScrollBarSize + "px");
        } else {
            builder.addCol("1");
        }
        
        if (needXBar) {
            builder.addRow("1").addRow(this.xScrollBarSize + "px");
        } else {
            builder.addRow("1");
        }
        
        this.setGridParam(builder.build());
        
        super.addChild(this.inner, new GridElementParam(0, 0, 1, 1));
        
        if (needYBar) {
            super.addChild(this.yScrollBar, new GridElementParam(0, 1, 1, 1));
        }
        
        if (needXBar) {
            super.addChild(this.xScrollBar, new GridElementParam(1, 0, 1, 1));
        }
        
        this.lastXBarVisible = needXBar;
        this.lastYBarVisible = needYBar;
        this.markDirty();
    }
    
    @Override
    public void resize() {
        super.resize();
        if(this.inner.actualWidth == 0 || this.inner.actualHeight == 0){
            this.inner.markDirty();
            this.submitTreeUpdate(this::rebuildLayout);
            return;
        }
        int innerViewWidth = this.inner.getWidth();
        int innerViewHeight = this.inner.getHeight();
        boolean needXBar = this.xScrollable && this.xScrollBarVisible && this.inner.actualWidth > innerViewWidth;
        boolean needYBar = this.yScrollable && this.yScrollBarVisible && this.inner.actualHeight > innerViewHeight;
        
        if (needXBar != this.lastXBarVisible || needYBar != this.lastYBarVisible) {
            this.submitTreeUpdate(this::rebuildLayout);
        }
        
        this.clampOffset();
        this.updateScrollBarParams();
    }
    
    private void clampOffset() {
        if (this.inner.actualWidth > this.inner.getWidth()) {
            int maxOffsetX = this.inner.actualWidth - this.inner.getWidth();
            this.inner.setOffsetX(Math.max(-maxOffsetX, Math.min(0, this.inner.offsetX)));
        } else {
            this.inner.setOffsetX(0);
        }
        
        if (this.inner.actualHeight > this.inner.getHeight()) {
            int maxOffsetY = this.inner.actualHeight - this.inner.getHeight();
            this.inner.setOffsetY(Math.max(-maxOffsetY, Math.min(0, this.inner.offsetY)));
        } else {
            this.inner.setOffsetY(0);
        }
    }
    
    private void updateScrollBarParams() {
        int viewHeight = this.inner.getHeight();
        int actualHeight = this.inner.actualHeight;
        this.yScrollBar.setScrollParams(viewHeight, actualHeight);
        int viewWidth = this.inner.getWidth();
        int actualWidth = this.inner.actualWidth;
        this.xScrollBar.setScrollParams(viewWidth, actualWidth);
    }
    
    @Override
    protected boolean onMouseScrolled(double x, double y, double scrollX, double scrollY) {
        boolean canScrollY = this.yScrollable && this.inner.actualHeight > this.inner.getHeight();
        boolean canScrollX = this.xScrollable && this.inner.actualWidth > this.inner.getWidth();
        
        if (!canScrollX && !canScrollY) {
            return false;
        }
        
        if (canScrollX && !canScrollY) {
            if (scrollY != 0) {
                int maxOffset = this.inner.actualWidth - this.inner.getWidth();
                this.inner.setOffsetX(Math.max(-maxOffset, Math.min(0, this.inner.offsetX + (int)(scrollY * 20))));
                return true;
            }
            return false;
        }
        
        if (canScrollY && !canScrollX) {
            if (scrollY != 0) {
                int maxOffset = this.inner.actualHeight - this.inner.getHeight();
                this.inner.setOffsetY(Math.max(-maxOffset, Math.min(0, this.inner.offsetY + (int)(scrollY * 20))));
                return true;
            }
            return false;
        }
        
        boolean shiftDown = GLFW.glfwGetKey(OpenGLWorkaround.window.getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(OpenGLWorkaround.window.getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        
        if (scrollX != 0) {
            int maxOffset = this.inner.actualWidth - this.inner.getWidth();
            this.inner.setOffsetX(Math.max(-maxOffset, Math.min(0, this.inner.offsetX + (int)(scrollX * 20))));
            
            return true;
        }
        
        if (scrollY != 0) {
            if (shiftDown) {
                int maxOffset = this.inner.actualWidth - this.inner.getWidth();
                this.inner.setOffsetX(Math.max(-maxOffset, Math.min(0, this.inner.offsetX + (int)(scrollY * 20))));
            } else {
                int maxOffset = this.inner.actualHeight - this.inner.getHeight();
                this.inner.setOffsetY(Math.max(-maxOffset, Math.min(0, this.inner.offsetY + (int)(scrollY * 20))));
            }
            return true;
        }
        
        return false;
    }
    
    private class ScrollBar extends AbstractWidget {
        private final boolean horizontal;
        private int viewSize;
        private int actualSize;
        private boolean dragging = false;
        private double dragStartPos;
        private int dragStartOffset;
        
        public ScrollBar(boolean horizontal) {
            this.horizontal = horizontal;
        }
        
        public void setScrollParams(int viewSize, int actualSize) {
            this.viewSize = viewSize;
            this.actualSize = actualSize;
        }
        
        @Override
        public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF404040);
            
            if (this.actualSize <= 0 || this.viewSize <= 0) {
                return;
            }
            
            int barLength = this.horizontal ? this.width : this.height;
            int thumbSize = Math.max(20, (int)((float)this.viewSize / this.actualSize * barLength));
            
            int maxScroll = this.actualSize - this.viewSize;
            int currentOffset = this.horizontal ? -ScrollableFlexLayout.this.inner.offsetX : -ScrollableFlexLayout.this.inner.offsetY;
            
            float scrollRatio = maxScroll > 0 ? (float)currentOffset / maxScroll : 0;
            int thumbPos = (int)(scrollRatio * (barLength - thumbSize));
            
            int thumbX, thumbY, thumbW, thumbH;
            if (this.horizontal) {
                thumbX = this.x + thumbPos;
                thumbY = this.y;
                thumbW = thumbSize;
                thumbH = this.height;
            } else {
                thumbX = this.x;
                thumbY = this.y + thumbPos;
                thumbW = this.width;
                thumbH = thumbSize;
            }
            
            int thumbColor = this.dragging ? 0xFFA0A0A0 : 0xFFC0C0C0;
            graphics.fill(thumbX, thumbY, thumbX + thumbW, thumbY + thumbH, thumbColor);
        }
        
        @Override
        protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            if (event.button() == 0) {
                this.dragging = true;
                this.dragStartPos = this.horizontal ? event.x() : event.y();
                this.dragStartOffset = this.horizontal ? ScrollableFlexLayout.this.inner.offsetX : ScrollableFlexLayout.this.inner.offsetY;
                return true;
            }
            return false;
        }
        
        @Override
        protected boolean onMouseReleased(IMouseButtonEvent event) {
            if (event.button() == 0 && this.dragging) {
                this.dragging = false;
                return true;
            }
            return false;
        }
        
        @Override
        protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
            if (this.dragging && this.actualSize > this.viewSize) {
                int barLength = this.horizontal ? this.width : this.height;
                int thumbSize = Math.max(20, (int)((float)this.viewSize / this.actualSize * barLength));
                int trackLength = barLength - thumbSize;
                
                if (trackLength <= 0) {
                    return true;
                }
                
                double currentPos = this.horizontal ? event.x() : event.y();
                double delta = currentPos - this.dragStartPos;
                
                int maxScroll = this.actualSize - this.viewSize;
                int newOffset = this.dragStartOffset - (int)(delta / trackLength * maxScroll);
                newOffset = Math.max(-maxScroll, Math.min(0, newOffset));
                
                if (this.horizontal) {
                    ScrollableFlexLayout.this.inner.setOffsetX(newOffset);
                } else {
                    ScrollableFlexLayout.this.inner.setOffsetY(newOffset);
                }
                return true;
            }
            return false;
        }
        
        @Override
        public boolean mouseDragged(IMouseButtonEvent event, double dx, double dy) {
            if (this.dragging) {
                return this.onMouseDragged(event, dx, dy);
            }
            return super.mouseDragged(event, dx, dy);
        }
        
        @Override
        public boolean mouseReleased(IMouseButtonEvent event) {
            if (this.dragging) {
                this.dragging = false;
                return true;
            }
            return super.mouseReleased(event);
        }
    }
}

package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.layout.ScreenRectangle;
import com.xkball.xklib.ui.widget.Widget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AvailableSpace;
import dev.vfyjxf.taffy.style.TaffyStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbsoluteContainer extends ContainerWidget {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbsoluteContainer.class);
    
    public boolean autoReorder = true;
    public boolean clampWidget = true;
    
    @Override
    protected ContainerWidget addChild(Widget widget, TaffyStyle style) {
        if(this.children.contains(widget)) return this;
        widget.setParent(this);
        widget.asTreeRoot();
//        LOGGER.debug("{} added child: {}", this.getName(), widget.getName());
        widget.init();
        widget.setStyle(style);
        this.children.add(widget);
        this.focusNode.addChild(widget.getFocusNode());
        this.markDirty();
        return this;
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
                widget.resize(this.x - this.xScrollOffset + widget.getAbsoluteX(), this.y - this.yScrollOffset + widget.getAbsoluteY());
                if(clampWidget){
                    var wx = widget.getX();
                    var wy = widget.getY();
                    var wmx = widget.getMaxX();
                    var wmy = widget.getMaxY();
                    float xOffset = 0;
                    float yOffset = 0;
                    if(wx > this.getMaxX()) xOffset = this.getMaxX() - wx - 4;
                    if(wy > this.getMaxY()) yOffset = this.getMaxY() - wy - 4;
                    if(wmx < this.getX()) xOffset = this.getX() - wmx + 4;
                    if(wmy < this.getY()) yOffset = this.getY() - wmy + 4;
                    if(xOffset != 0 || yOffset != 0) {
                        widget.resize(this.x - this.xScrollOffset + widget.getAbsoluteX() + xOffset, this.y - this.yScrollOffset + widget.getAbsoluteY() + yOffset);
                    }
                }
            }
            this.resizeScrollBar();
        }
    }
    
    @Override
    public boolean mouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        if (!this.isMouseOver(x, y)) return false;
        
        Widget clickedChild = null;
        for (Widget child : this.children) {
            if (child.visible && child.enabled) {
                ScreenRectangle rect = child.getRectangle();
                if (rect.containsPoint((int) event.x(), (int) event.y())) {
                    if (child.mouseClicked(event, doubleClick)) {
                        clickedChild = child;
                        break;
                    }
                }
            }
        }
        
        if (clickedChild != null && autoReorder && this.children.size() > 1) {
            this.children.remove(clickedChild);
            this.children.addFirst(clickedChild);
            return true;
        }
        
        return super.mouseClicked(event, doubleClick);
    }
}

package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.ui.widget.Widget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AvailableSpace;
import dev.vfyjxf.taffy.style.TaffyStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbsoluteContainer extends ContainerWidget {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbsoluteContainer.class);
    
    @Override
    public ContainerWidget addChild(Widget widget, TaffyStyle style) {
        if(this.children.contains(widget)) return this;
        widget.setParent(this);
        widget.asTreeRoot();
        LOGGER.debug("{} added child: {}", this.getName(), widget.getName());
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
        super.resize(offsetX, offsetY);
    }
}

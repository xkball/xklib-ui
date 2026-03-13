package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.ui.layout.DefaultStyles;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TextAlign;

public class SectionContainer extends ContainerWidget {
    
    private final Label header;
    private Widget content;
    private boolean expanded = true;
    private TaffyDisplay contentOriginalDisplay = TaffyDisplay.FLEX;
    
    public SectionContainer(String title) {
        this(title, createDefaultContent());
    }
    
    public SectionContainer(String title, Widget content) {
        super();
        this.header = new Label(title, TextAlign.LEFT, 0xFFE2E8F0);
        this.header.setCSSId("header");
        this.content = content;
        this.addChild(header);
    }
    
    private static Widget createDefaultContent() {
        Widget widget = new Widget();
        widget.setStyle(s -> s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(200)));
        return widget;
    }
    
    @Override
    public void init() {
        super.init();
        this.applyStyle(DefaultStyles::flexCenteredColum);
        this.header.setStyle(s -> s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.length(32)));
    }
    
    @Override
    protected boolean onMouseClicked(com.xkball.xklib.api.gui.input.IMouseButtonEvent event, boolean doubleClick) {
        if (header.isMouseOver(event.x(), event.y())) {
            toggleExpanded();
            return true;
        }
        return super.onMouseClicked(event, doubleClick);
    }
    
    public void toggleExpanded() {
        setExpanded(!expanded);
    }
    
    public void setExpanded(boolean expanded) {
        if (this.expanded == expanded || content == null) {
            return;
        }
        
        this.expanded = expanded;
        content.setStyle(s -> s.display = expanded ? contentOriginalDisplay : TaffyDisplay.NONE);
    }
    
    public boolean isExpanded() {
        return expanded;
    }
    
    public SectionContainer setContent(Widget content) {
        if (this.content != null) {
            this.removeChild(this.content);
        }
        
        this.content = content;
        if (content != null) {
            contentOriginalDisplay = content.getStyle().display;
            if (!expanded) {
                content.setStyle(s -> s.display = TaffyDisplay.NONE);
            }
            this.addChild(content);
        }
        return this;
    }
    
    public Widget getContent() {
        return content;
    }
    
    public Label getHeader() {
        return header;
    }
}

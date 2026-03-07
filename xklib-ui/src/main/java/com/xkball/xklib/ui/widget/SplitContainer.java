package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TaffyStyle;
import dev.vfyjxf.taffy.style.TrackSizingFunction;

import java.util.List;

public class SplitContainer extends ContainerWidget {

    private static final float BAR_SIZE = 4f;
    private static final int BAR_COLOR = 0xFF444444;
    private static final int BAR_HOVER_COLOR = 0xFF888888;

    protected boolean vertical;
    private final ContainerWidget firstPanel;
    private final ContainerWidget secondPanel;
    private float splitRatio = 0.5f;

    private boolean barDragging = false;
    private float barDragStartMouse = 0f;
    private float barDragStartRatio = 0f;

    public SplitContainer(boolean vertical) {
        this.vertical = vertical;
        this.firstPanel = new ContainerWidget();
        this.secondPanel = new ContainerWidget();
        applyContainerStyle();
    }

    public SplitContainer() {
        this(false);
    }

    private void applyContainerStyle() {
        var s = this.style;
        s.display = TaffyDisplay.GRID;
        s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        s.alignItems = AlignItems.STRETCH;
        s.justifyContent = AlignContent.STRETCH;
        rebuildGridTemplate(s);
    }
    
    @Override
    public void setStyle(TaffyStyle style) {
        super.setStyle(style);
        this.applyContainerStyle();
    }
    
    private void rebuildGridTemplate(TaffyStyle s) {
        if (!vertical) {
            s.gridTemplateColumns = List.of(
                    TrackSizingFunction.fr(splitRatio),
                    TrackSizingFunction.fixed(LengthPercentage.length(BAR_SIZE)),
                    TrackSizingFunction.fr(1f - splitRatio)
            );
            s.gridTemplateRows = List.of(TrackSizingFunction.fr(1f));
        } else {
            s.gridTemplateRows = List.of(
                    TrackSizingFunction.fr(splitRatio),
                    TrackSizingFunction.fixed(LengthPercentage.length(BAR_SIZE)),
                    TrackSizingFunction.fr(1f - splitRatio)
            );
            s.gridTemplateColumns = List.of(TrackSizingFunction.fr(1f));
        }
    }

    @Override
    public void afterTreeAndNodeSet() {
        super.afterTreeAndNodeSet();
        var firstStyle = new TaffyStyle();
        this.addChild(firstPanel, firstStyle);
        var barWidget = new SplitBar();
        var barStyle = new TaffyStyle();
        this.addChild(barWidget, barStyle);
        var secondStyle = new TaffyStyle();
        this.addChild(secondPanel, secondStyle);
    }

    public void setFirst(Widget widget, TaffyStyle style) {
        style.size =TaffySize.all(TaffyDimension.percent(1f));
        firstPanel.addChild(widget, style);
    }

    public void setFirst(Widget widget) {
        setFirst(widget, widget.getStyle());
    }

    public void setSecond(Widget widget, TaffyStyle style) {
        style.size =TaffySize.all(TaffyDimension.percent(1f));
        secondPanel.addChild(widget, style);
    }

    public void setSecond(Widget widget) {
        setSecond(widget, widget.getStyle());
    }

    public ContainerWidget getFirstPanel() {
        return firstPanel;
    }

    public ContainerWidget getSecondPanel() {
        return secondPanel;
    }

    public float getSplitRatio() {
        return splitRatio;
    }

    public void setSplitRatio(float ratio) {
        this.splitRatio = Math.clamp(ratio, 0.05f, 0.95f);
        this.setStyle(this::rebuildGridTemplate);
    }

    private boolean isMouseOverBar(double mouseX, double mouseY) {
        if (children.size() < 2) return false;
        var bar = children.get(1);
        return bar.getRectangle().containsPoint((int) mouseX, (int) mouseY);
    }

    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if (isMouseOverBar(event.x(), event.y())) {
            barDragging = true;
            barDragStartMouse = vertical ? (float) event.y() : (float) event.x();
            barDragStartRatio = splitRatio;
            return true;
        }
        return super.onMouseClicked(event, doubleClick);
    }

    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (barDragging) {
            float totalSize = vertical ? this.height : this.width;
            if (totalSize <= BAR_SIZE) return true;
            float usable = totalSize - BAR_SIZE;
            if (usable <= 0) return true;
            float currentMouse = vertical ? (float) event.y() : (float) event.x();
            float delta = currentMouse - barDragStartMouse;
            float newRatio = barDragStartRatio + delta / usable;
            setSplitRatio(newRatio);
            return true;
        }
        return super.onMouseDragged(event, dx, dy);
    }

    @Override
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        barDragging = false;
        return super.onMouseReleased(event);
    }

    public class SplitBar extends Widget {

        @Override
        public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            super.doRender(graphics, mouseX, mouseY, a);
            int color = (this.hovered || barDragging) ? BAR_HOVER_COLOR : BAR_COLOR;
            graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, color);
        }
    }
}

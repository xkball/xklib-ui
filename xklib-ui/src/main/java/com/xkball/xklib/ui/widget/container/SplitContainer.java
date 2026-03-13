package com.xkball.xklib.ui.widget.container;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.Widget;
import dev.vfyjxf.taffy.geometry.TaffySize;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.CalcExpression;
import dev.vfyjxf.taffy.style.LengthPercentage;
import dev.vfyjxf.taffy.style.TaffyDimension;
import dev.vfyjxf.taffy.style.TaffyDisplay;
import dev.vfyjxf.taffy.style.TaffyStyle;

import java.util.ArrayList;
import java.util.List;

import static dev.vfyjxf.taffy.style.TrackSizingFunction.*;

public class SplitContainer extends ContainerWidget {

    private static final float BAR_SIZE = 4f;
    private static final int BAR_COLOR = 0xFF444444;
    private static final int BAR_HOVER_COLOR = 0xFF888888;

    protected boolean vertical;
    private final int count;
    private final float[] ratios;
    private final List<ContainerWidget> panels = new ArrayList<>();

    private int draggingBarIndex = -1;
    private float barDragStartMouse = 0f;
    private float barDragStartRatio = 0f;

    public SplitContainer(boolean vertical, int count) {
        if (count < 2) throw new IllegalArgumentException("count must >= 2");
        this.vertical = vertical;
        this.count = count;
        this.ratios = new float[count - 1];
        for (int i = 0; i < ratios.length; i++) {
            ratios[i] = (i + 1f) / count;
        }
        for (int i = 0; i < count; i++) {
            panels.add(new ContainerWidget());
        }
        applyContainerStyle();
    }

    public SplitContainer(boolean vertical) {
        this(vertical, 2);
    }

    public SplitContainer(int count) {
        this(false, count);
    }

    public SplitContainer() {
        this(false, 2);
    }

    private void applyContainerStyle() {
        var s = this.style;
        s.display = TaffyDisplay.GRID;
        s.size = new TaffySize<>(TaffyDimension.percent(1f), TaffyDimension.percent(1f));
        s.alignItems = AlignItems.STRETCH;
        s.justifyContent = AlignContent.STRETCH;
        rebuildGridTemplate(s);
        this.markDirty();
    }

    @Override
    public void setStyle(TaffyStyle style) {
        super.setStyle(style);
        this.applyContainerStyle();
    }

    private void rebuildGridTemplate(TaffyStyle s) {
        var tracks = new ArrayList<dev.vfyjxf.taffy.style.TrackSizingFunction>();
        float totalBars = (count - 1) * BAR_SIZE;
        for (int i = 0; i < count; i++) {
            float panelPercent = panelPercent(i);
            tracks.add(minmax(fixed(0), fixed(LengthPercentage.calc(
                    CalcExpression.percentMinusLength(panelPercent, totalBars * panelPercent)))));
            if (i < count - 1) {
                tracks.add(fixed(LengthPercentage.length(BAR_SIZE)));
            }
        }
        if (!vertical) {
            s.gridTemplateColumns = tracks;
            s.gridTemplateRows = List.of(percent(1f));
        } else {
            s.gridTemplateRows = tracks;
            s.gridTemplateColumns = List.of(percent(1f));
        }
    }

    private float panelPercent(int index) {
        float start = index == 0 ? 0f : ratios[index - 1];
        float end = index == count - 1 ? 1f : ratios[index];
        return end - start;
    }

    @Override
    public void afterTreeAndNodeSet() {
        super.afterTreeAndNodeSet();
        for (int i = 0; i < count; i++) {
            var panel = panels.get(i);
            panel.setStyle(s -> {
                s.minSize = TaffySize.all(TaffyDimension.ZERO);
                s.size = TaffySize.of(vertical ? TaffyDimension.percent(1) : TaffyDimension.auto(),
                        vertical ? TaffyDimension.auto() : TaffyDimension.percent(1));
            });
            this.addChild(panel);
            if (i < count - 1) {
                this.addChild(new SplitBar(i));
            }
        }
    }

    public SplitContainer setPanel(int index, Widget widget, TaffyStyle style) {
        style.size = TaffySize.all(TaffyDimension.percent(1f));
        panels.get(index).addChild(widget, style);
        return this;
    }

    public SplitContainer setPanel(int index, Widget widget) {
        return setPanel(index, widget, widget.getStyle());
    }

    public ContainerWidget getPanel(int index) {
        return panels.get(index);
    }

    public int getCount() {
        return count;
    }

    public float getRatio(int barIndex) {
        return ratios[barIndex];
    }

    public void setRatio(int barIndex, float ratio) {
        float min = barIndex == 0 ? 0.01f : ratios[barIndex - 1] + 0.01f;
        float max = barIndex == ratios.length - 1 ? 0.99f : ratios[barIndex + 1] - 0.01f;
        ratios[barIndex] = Math.clamp(ratio, min, max);
        this.setStyle(this::rebuildGridTemplate);
    }

    private int findBarIndex(double mouseX, double mouseY) {
        int barChildIndex = 1;
        for (int i = 0; i < count - 1; i++) {
            if (barChildIndex < children.size()) {
                var bar = children.get(barChildIndex);
                if (bar.getRectangle().containsPoint((int) mouseX, (int) mouseY)) {
                    return i;
                }
            }
            barChildIndex += 2;
        }
        return -1;
    }

    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        int barIdx = findBarIndex(event.x(), event.y());
        if (barIdx >= 0) {
            draggingBarIndex = barIdx;
            barDragStartMouse = vertical ? (float) event.y() : (float) event.x();
            barDragStartRatio = ratios[barIdx];
            return true;
        }
        return super.onMouseClicked(event, doubleClick);
    }

    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (draggingBarIndex >= 0) {
            float totalSize = vertical ? this.height : this.width;
            float totalBars = (count - 1) * BAR_SIZE;
            float usable = totalSize - totalBars;
            if (usable <= 0) return true;
            float currentMouse = vertical ? (float) event.y() : (float) event.x();
            float delta = currentMouse - barDragStartMouse;
            float newRatio = barDragStartRatio + delta / usable;
            setRatio(draggingBarIndex, newRatio);
            return true;
        }
        return super.onMouseDragged(event, dx, dy);
    }

    @Override
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        draggingBarIndex = -1;
        return super.onMouseReleased(event);
    }

    public class SplitBar extends Widget {

        private final int barIndex;

        public SplitBar(int barIndex) {
            this.barIndex = barIndex;
        }

        @Override
        public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            super.doRender(graphics, mouseX, mouseY, a);
            int color = (this.hovered || draggingBarIndex == barIndex) ? BAR_HOVER_COLOR : BAR_COLOR;
            graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, color);
        }

        @Override
        public void onFocusChanged(boolean focused) {
            if (!focused && draggingBarIndex == barIndex) draggingBarIndex = -1;
        }
    }
}

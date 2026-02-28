package com.xkball.xklib.ui.widget.layout;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.layout.GridElementParam;
import com.xkball.xklib.ui.layout.GridParam;
import com.xkball.xklib.ui.layout.ScreenAxis;
import com.xkball.xklib.ui.layout.SizeParam;
import com.xkball.xklib.ui.layout.SplitParam;
import com.xkball.xklib.ui.widget.AbstractWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("UnusedReturnValue")
public class SplitLayout extends GridLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(SplitLayout.class);

    private final ScreenAxis axis;
    private SizeParam firstSize;
    private SizeParam secondSize;
    private final SplitDivider divider;
    private int userChildCount = 0;

    public SplitLayout(SplitParam param) {
        super(fromSplitParam(param));
        this.axis = param.getAxis();
        this.firstSize = param.getFirstSize();
        this.secondSize = param.getSecondSize();
        this.divider = new SplitDivider();
        int dividerRow = axis == ScreenAxis.HORIZONTAL ? 0 : 1;
        int dividerCol = axis == ScreenAxis.HORIZONTAL ? 1 : 0;
        super.addChild(this.divider, new GridElementParam(dividerRow, dividerCol, 1, 1));
    }

    public static GridParam fromSplitParam(SplitParam param) {
        if (param.getAxis() == ScreenAxis.HORIZONTAL) {
            return new GridParam.Builder()
                    .addCol(param.getFirstSize())
                    .addCol("2px")
                    .addCol(param.getSecondSize())
                    .addRow("1")
                    .build();
        } else {
            return new GridParam.Builder()
                    .addRow(param.getFirstSize())
                    .addRow("2px")
                    .addRow(param.getSecondSize())
                    .addCol("1")
                    .build();
        }
    }

    public SplitLayout setFirst(AbstractWidget widget) {
        var children = getChildren();
        for (AbstractWidget child : children) {
            if (child == divider) continue;
            GridElementParam p = getLayoutParam(child);
            if (p != null && isFirstSlot(p)) {
                removeChild(child);
                userChildCount--;
                break;
            }
        }
        int row = 0;
        int col = 0;
        super.addChild(widget, new GridElementParam(row, col, 1, 1));
        userChildCount++;
        return this;
    }

    public SplitLayout setSecond(AbstractWidget widget) {
        var children = getChildren();
        for (AbstractWidget child : children) {
            if (child == divider) continue;
            GridElementParam p = getLayoutParam(child);
            if (p != null && isSecondSlot(p)) {
                removeChild(child);
                userChildCount--;
                break;
            }
        }
        int row = axis == ScreenAxis.HORIZONTAL ? 0 : 2;
        int col = axis == ScreenAxis.HORIZONTAL ? 2 : 0;
        super.addChild(widget, new GridElementParam(row, col, 1, 1));
        userChildCount++;
        return this;
    }

    @Override
    public GridLayout addChild(AbstractWidget widget, GridElementParam layoutParam) {
        if (widget == divider) {
            return super.addChild(widget, layoutParam);
        }
        if (userChildCount >= 2) {
            LOGGER.warn("SplitLayout add more than two children: {}", widget);
            return this;
        }
        userChildCount++;
        return super.addChild(widget, layoutParam);
    }

    private boolean isFirstSlot(GridElementParam p) {
        if (axis == ScreenAxis.HORIZONTAL) {
            return p.col() == 0;
        } else {
            return p.row() == 0;
        }
    }

    private boolean isSecondSlot(GridElementParam p) {
        if (axis == ScreenAxis.HORIZONTAL) {
            return p.col() == 2;
        } else {
            return p.row() == 2;
        }
    }

    private void updateGridParam() {
        GridParam.Builder builder = new GridParam.Builder();
        if (axis == ScreenAxis.HORIZONTAL) {
            builder.addCol(firstSize).addCol("2px").addCol(secondSize).addRow("1");
        } else {
            builder.addCol("1").addRow(firstSize).addRow("2px").addRow(secondSize);
        }
        setGridParam(builder.build());
    }

    private class SplitDivider extends AbstractWidget {

        private boolean dragging = false;
        private double dragStartPos;
        private int dragStartFirstPx;

        @Override
        public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
            int color = this.hovered || this.dragging ? 0xFFA0A0A0 : 0xFF606060;
            graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, color);
            super.render(graphics, mouseX, mouseY, a);
        }

        @Override
        protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
            if (event.button() == 0) {
                this.dragging = true;
                this.dragStartPos = axis == ScreenAxis.HORIZONTAL ? event.x() : event.y();
                this.dragStartFirstPx = axis == ScreenAxis.HORIZONTAL
                        ? SplitDivider.this.x - SplitLayout.this.contentX
                        : SplitDivider.this.y - SplitLayout.this.contentY;
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
            if (!this.dragging) return false;
            double currentPos = axis == ScreenAxis.HORIZONTAL ? event.x() : event.y();
            int delta = (int) (currentPos - this.dragStartPos);
            int total = axis == ScreenAxis.HORIZONTAL ? SplitLayout.this.contentWidth : SplitLayout.this.contentHeight;
            int minSize = 10;
            int dividerSize = 2;
            int newFirst = Math.max(minSize, Math.min(total - dividerSize - minSize, dragStartFirstPx + delta));
            int newSecond = total - dividerSize - newFirst;
            SplitLayout.this.firstSize = SizeParam.clamp("90%", "10%", newFirst + "px");
            SplitLayout.this.secondSize = SizeParam.clamp("90%", "10%", newSecond + "px");
            SplitLayout.this.updateGridParam();
            return true;
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

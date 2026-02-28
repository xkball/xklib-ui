package com.xkball.xklib.ui.widget.widgets;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.AbstractWidget;

import java.util.function.Consumer;

public class DragBox extends AbstractWidget {

    private static final int THUMB_WIDTH = 10;
    private static final int TRACK_COLOR = 0xFFCBD5E1;
    private static final int THUMB_COLOR = 0xFF7DD3FC;
    private static final int THUMB_HOVER_COLOR = 0xFF38BDF8;
    private static final int BORDER_COLOR = 0xFF94A3B8;

    private double minValue;
    private double maxValue;
    private double value;
    private boolean dragging = false;
    private Consumer<Double> onChange;

    public DragBox(double minValue, double maxValue, double value) {
        super();
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = clamp(value);
    }

    public DragBox(double minValue, double maxValue) {
        this(minValue, maxValue, minValue);
    }

    public double getMinValue() {
        return this.minValue;
    }

    public DragBox setMinValue(double minValue) {
        this.minValue = minValue;
        this.value = clamp(this.value);
        return this;
    }

    public double getMaxValue() {
        return this.maxValue;
    }

    public DragBox setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        this.value = clamp(this.value);
        return this;
    }

    public double getValue() {
        return this.value;
    }

    public DragBox setValue(double value) {
        this.value = clamp(value);
        return this;
    }

    public DragBox setOnChange(Consumer<Double> onChange) {
        this.onChange = onChange;
        return this;
    }

    private double clamp(double v) {
        return Math.max(this.minValue, Math.min(this.maxValue, v));
    }

    private int thumbX() {
        int trackWidth = this.contentWidth - THUMB_WIDTH;
        if (trackWidth <= 0 || this.maxValue == this.minValue) {
            return this.contentX;
        }
        double ratio = (this.value - this.minValue) / (this.maxValue - this.minValue);
        return this.contentX + (int) Math.round(ratio * trackWidth);
    }

    private boolean isOverThumb(double mouseX, double mouseY) {
        int tx = thumbX();
        return mouseX >= tx && mouseX < tx + THUMB_WIDTH && mouseY >= this.contentY && mouseY < this.contentY + this.contentHeight;
    }

    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        this.dragging = true;
        setValueFromX(event.x());
        return true;
    }

    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (!this.dragging) return false;
        setValueFromX(event.x());
        return true;
    }

    @Override
    public boolean mouseReleased(IMouseButtonEvent event) {
        if (this.dragging) {
            this.dragging = false;
            return false;
        }
        return super.mouseReleased(event);
    }

    private void setValueFromX(double mouseX) {
        int trackWidth = this.contentWidth - THUMB_WIDTH;
        if (trackWidth <= 0 || this.maxValue == this.minValue) return;
        double ratio = (mouseX - this.contentX - THUMB_WIDTH / 2.0) / trackWidth;
        double newValue = clamp(this.minValue + ratio * (this.maxValue - this.minValue));
        if (newValue != this.value) {
            this.value = newValue;
            if (this.onChange != null) {
                this.onChange.accept(this.value);
            }
        }
    }

    @Override
    public void render(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        graphics.fill(this.contentX, this.contentY, this.contentX + this.contentWidth, this.contentY + this.contentHeight, TRACK_COLOR);
        graphics.renderOutline(this.contentX, this.contentY, this.contentWidth, this.contentHeight, BORDER_COLOR);

        int tx = thumbX();
        boolean thumbHovered = this.dragging || isOverThumb(mouseX,mouseY);
        int thumbColor = thumbHovered ? THUMB_HOVER_COLOR : THUMB_COLOR;
        graphics.fill(tx, this.contentY, tx + THUMB_WIDTH, this.contentY + this.contentHeight, thumbColor);
        graphics.renderOutline(tx, this.contentY, THUMB_WIDTH, this.contentHeight, BORDER_COLOR);

        super.render(graphics, mouseX, mouseY, a);
    }
}

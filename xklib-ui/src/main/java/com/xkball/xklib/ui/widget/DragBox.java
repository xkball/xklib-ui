package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.ui.render.IGUIGraphics;

import java.util.function.Consumer;

public class DragBox extends Widget{
    private static final int THUMB_WIDTH = 10;
    private static final String SELF_CSS = """
            * {
                dragbox-track-color: 0xFFCBD5E1;
                dragbox-thumb-color: 0xFF7DD3FC;
                dragbox-thumb-hover-color: 0xFF38BDF8;
                dragbox-border-color: 0xFF94A3B8;
            }
            """;

    private int trackColor;
    private int thumbColor;
    private int thumbHoverColor;
    private int borderColor;
    
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

    @Override
    public String createCSSAsSelf() {
        return SELF_CSS;
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
    
    private float thumbX() {
        var trackWidth = this.width - THUMB_WIDTH;
        if (trackWidth <= 0 || this.maxValue == this.minValue) {
            return this.x;
        }
        double ratio = (this.value - this.minValue) / (this.maxValue - this.minValue);
        return this.x + Math.round(ratio * trackWidth);
    }
    
    private boolean isOverThumb(double mouseX, double mouseY) {
        var tx = thumbX();
        return mouseX >= tx && mouseX < tx + THUMB_WIDTH && mouseY >= this.y && mouseY < this.y + this.height;
    }
    
    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        this.dragging = true;
        setValueFromX(event.x());
        return true;
    }
    
    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if (!this.dragging || !this.isMouseOver(event.x(),event.y())) return false;
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
        var trackWidth = this.width - THUMB_WIDTH;
        if (trackWidth <= 0 || this.maxValue == this.minValue) return;
        double ratio = (mouseX - this.x - THUMB_WIDTH / 2.0) / trackWidth;
        double newValue = clamp(this.minValue + ratio * (this.maxValue - this.minValue));
        if (newValue != this.value) {
            this.value = newValue;
            if (this.onChange != null) {
                this.onChange.accept(this.value);
            }
        }
    }
    
    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        graphics.fill(this.x, this.y, this.x + this.width, this.y + this.height, this.trackColor);
        graphics.renderOutline(this.x, this.y, this.width, this.height, this.borderColor);
        
        var tx = thumbX();
        boolean thumbHovered = this.dragging || isOverThumb(mouseX,mouseY);
        int currentThumbColor = thumbHovered ? this.thumbHoverColor : this.thumbColor;
        graphics.fill(tx, this.y, tx + THUMB_WIDTH, this.y + this.height, currentThumbColor);
        graphics.renderOutline(tx, this.y, THUMB_WIDTH, this.height, this.borderColor);
    }

    public void setTrackColor(int trackColor) {
        this.trackColor = trackColor;
    }

    public void setThumbColor(int thumbColor) {
        this.thumbColor = thumbColor;
    }

    public void setThumbHoverColor(int thumbHoverColor) {
        this.thumbHoverColor = thumbHoverColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }
}

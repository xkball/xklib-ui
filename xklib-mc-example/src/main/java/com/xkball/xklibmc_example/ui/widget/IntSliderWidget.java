package com.xkball.xklibmc_example.ui.widget;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.widget.IInputWidget;
import com.xkball.xklib.api.gui.widget.ILayoutVariable;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklibmc.utils.VanillaUtils;

import java.util.ArrayList;
import java.util.List;

public class IntSliderWidget extends Widget implements IInputWidget<Integer> {

    private final int min;
    private final int max;
    private final List<ILayoutVariable<Integer>> bindings = new ArrayList<>();
    private int value;
    private boolean dragging;

    public IntSliderWidget(int min, int max, int value) {
        this.min = min;
        this.max = max;
        this.value = Math.clamp(value, min, max);
        this.inlineStyle("size: 180rpx 16rpx; flex-shrink: 0;");
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public void setValue(Integer value) {
        if(value == null) return;
        var next = Math.clamp(value, this.min, this.max);
        if(this.value == next) return;
        this.value = next;
        for(var binding : this.bindings) {
            binding.set(next);
        }
    }

    @Override
    public IntSliderWidget bind(ILayoutVariable<Integer> variable) {
        this.setValue(variable.get());
        this.bindings.add(variable);
        variable.addCallback(this::setValueFromBinding);
        return this;
    }

    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        if(event.button() != 0) return false;
        this.dragging = true;
        this.updateFromMouse((float) event.x());
        return true;
    }

    @Override
    protected boolean onMouseDragged(IMouseButtonEvent event, double dx, double dy) {
        if(event.button() != 0 || !this.dragging) return false;
        this.updateFromMouse((float) (event.x() + dx));
        return true;
    }

    @Override
    protected boolean onMouseReleased(IMouseButtonEvent event) {
        if(event.button() != 0) return false;
        this.dragging = false;
        return true;
    }

    @Override
    public void onFocusChanged(boolean focused) {
        if(!focused) this.dragging = false;
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);
        var trackY0 = this.y + this.height * 0.35f;
        var trackY1 = this.y + this.height * 0.65f;
        graphics.fillRounded(this.x, trackY0, this.x + this.width, trackY1, VanillaUtils.getColor(60, 66, 74, 220), 2);
        var ratio = (this.value - this.min) / (float) (this.max - this.min);
        var knobX = this.x + ratio * this.width;
        graphics.fillRounded(this.x, trackY0, knobX, trackY1, VanillaUtils.getColor(82, 151, 255, 240), 2);
        graphics.fillRounded(knobX - 4, this.y + 1, knobX + 4, this.y + this.height - 1, VanillaUtils.getColor(235, 238, 242, 255), 4);
        graphics.drawString(String.valueOf(this.value), this.x + this.width + 6, this.y + 3, -1);
    }

    private void updateFromMouse(float mouseX) {
        var ratio = Math.clamp((mouseX - this.x) / Math.max(1, this.width), 0, 1);
        this.setValue(Math.round(this.min + ratio * (this.max - this.min)));
    }

    private void setValueFromBinding(int value) {
        this.value = Math.clamp(value, this.min, this.max);
    }
}

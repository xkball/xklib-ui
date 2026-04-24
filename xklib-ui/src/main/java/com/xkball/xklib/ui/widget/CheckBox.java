package com.xkball.xklib.ui.widget;

import com.xkball.xklib.ap.annotation.GuiWidgetClass;
import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.widget.IInputWidget;
import com.xkball.xklib.api.gui.widget.ILayoutVariable;
import com.xkball.xklib.ui.render.IGUIGraphics;

import java.util.ArrayList;
import java.util.List;

@GuiWidgetClass
public class CheckBox extends Widget implements IInputWidget<Boolean> {

    private static final int THUMB_MARGIN = 2;
    private static final String SELF_CSS = """
            checkbox-track-color: 0xFFCBD5E1;
            checkbox-thumb-color: 0xFFFFFFFF;
            checkbox-on-color: 0x8022C55E;
            """;

    private boolean checked = false;
    private int trackColor;
    private int thumbColor;
    private int onOverlayColor;
    private final List<ILayoutVariable<Boolean>> bindings = new ArrayList<>();

    public CheckBox() {
        this.inlineStyle(SELF_CSS);
    }

    @Override
    public Boolean getValue() {
        return checked;
    }

    @Override
    public void setValue(Boolean value) {
        this.checked = value != null && value;
    }

    @Override
    public CheckBox bind(ILayoutVariable<Boolean> variable) {
        this.setValue(variable.get());
        this.bindings.add(variable);
        return this;
    }

    @Override
    protected boolean onMouseClicked(IMouseButtonEvent event, boolean doubleClick) {
        this.checked = !this.checked;
        for(var bind : bindings){
            bind.set(this.checked);
        }
        return true;
    }

    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        super.doRender(graphics, mouseX, mouseY, a);

        float x = this.x;
        float y = this.y;
        float w = this.width;
        float h = this.height;

        float radius = h / 2f;
        float thumbSize = h - THUMB_MARGIN * 2;
        float thumbY = y + THUMB_MARGIN;
        float thumbX = checked
                ? x + w - THUMB_MARGIN - thumbSize
                : x + THUMB_MARGIN;

        graphics.fillRounded(x, y, x + w, y + h, this.trackColor, radius);
        if (checked) {
            graphics.fillRounded(x, y, x + w, y + h, this.onOverlayColor, radius);
        }
        graphics.fillRounded(thumbX, thumbY, thumbX + thumbSize, thumbY + thumbSize, this.thumbColor, thumbSize / 2f);
    }

    public void setTrackColor(int trackColor) {
        this.trackColor = trackColor;
    }

    public void setThumbColor(int thumbColor) {
        this.thumbColor = thumbColor;
    }

    public void setOnOverlayColor(int onOverlayColor) {
        this.onOverlayColor = onOverlayColor;
    }
}

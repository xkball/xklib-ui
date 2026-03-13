package com.xkball.xklib.ui.widget;

import com.xkball.xklib.api.gui.input.IMouseButtonEvent;
import com.xkball.xklib.api.gui.widget.IInputWidget;
import com.xkball.xklib.api.gui.widget.ILayoutVariable;
import com.xkball.xklib.ui.render.IGUIGraphics;

import java.util.ArrayList;
import java.util.List;

public class CheckBox extends Widget implements IInputWidget<Boolean> {

    private static final int THUMB_MARGIN = 2;
    private static final int TRACK_COLOR = 0xFFCBD5E1;
    private static final int TRACK_BORDER_COLOR = 0xFF94A3B8;
    private static final int THUMB_COLOR = 0xFFFFFFFF;
    private static final int THUMB_BORDER_COLOR = 0xFF64748B;
    private static final int ON_OVERLAY_COLOR = 0x8022C55E;

    private boolean checked = false;
    private final List<ILayoutVariable<Boolean>> bindings = new ArrayList<>();

    @Override
    public Boolean getValue() {
        return checked;
    }

    @Override
    public void setValue(Boolean value) {
        this.checked = value != null && value;
    }

    @Override
    public void bind(ILayoutVariable<Boolean> variable) {
        this.bindings.add(variable);
        variable.set(this.checked);
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

        graphics.fillRounded(x, y, x + w, y + h, TRACK_COLOR, radius);
        if (checked) {
            graphics.fillRounded(x, y, x + w, y + h, ON_OVERLAY_COLOR, radius);
        }
        graphics.fillRounded(thumbX, thumbY, thumbX + thumbSize, thumbY + thumbSize, THUMB_COLOR, thumbSize / 2f);
    }
}

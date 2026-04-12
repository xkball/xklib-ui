package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.render.IGUIGraphics;
import dev.vfyjxf.taffy.geometry.TaffyRect;
import dev.vfyjxf.taffy.style.LengthPercentage;

public class BorderColorProperty implements IStyleProperty<Integer> {

    public static final String NAME = "border-color";

    private int color;

    public BorderColorProperty(int color) {
        this.color = color;
    }

    @Override
    public String propertyName() {
        return NAME;
    }

    @Override
    public String valueString() {
        return String.valueOf(this.color);
    }

    @Override
    public Integer value() {
        return this.color;
    }

    @Override
    public void setValue(Integer value) {
        if (value != null) {
            this.color = value;
        }
    }

    @Override
    public void apply(IStyleSheet sheet, IGuiWidget widget) {
    }

    @Override
    public boolean renderable() {
        return true;
    }

    @Override
    public void render(IGuiWidget widget, IGUIGraphics graphics, int mouseX, int mouseY, float a) {
        var layout = widget.getLayout();
        var b = layout.border();
        float left = b.left;
        float right = b.right;
        float top = b.top;
        float bottom = b.bottom;
        var padding = layout.padding();

        if (left == 0 && right == 0 && top == 0 && bottom == 0) {
            return;
        }
        
        float minX = widget.getX();
        float minY = widget.getY();
        float maxX = widget.getMaxX();
        float maxY = widget.getMaxY();

        if (top > 0) {
            graphics.fill(minX, minY - top - padding.top, maxX, minY - padding.top, this.color);
        }
        if (bottom > 0) {
            graphics.fill(minX, maxY + padding.bottom, maxX, maxY + padding.bottom + bottom, this.color);
        }
        if (left > 0) {
            graphics.fill(minX - padding.left - left, minY - top - padding.top, minX - padding.left, maxY + bottom + padding.bottom, this.color);
        }
        if (right > 0) {
            graphics.fill(maxX + padding.right, minY - top - padding.top, maxX + padding.right + right, maxY + bottom + padding.bottom, this.color);
        }
    }

    private static float toPx(LengthPercentage value) {
        if (value == null) {
            return 0;
        }
        if (value.getType() == LengthPercentage.Type.LENGTH) {
            return value.getValue();
        }
        return 0;
    }
}




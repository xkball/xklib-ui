package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklib.ui.render.IGUIGraphics;

public class BackgroundColorProperty implements IStyleProperty<Integer> {

    public static final String NAME = "background-color";

    private int color;

    public BackgroundColorProperty(int color) {
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
        graphics.fill(widget.getX(), widget.getY(), widget.getMaxX(), widget.getMaxY(), this.color);
    }
}


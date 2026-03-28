package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

import java.util.function.BiConsumer;

public class WidgetIntStyleProperty implements IStyleProperty<Integer> {

    private final String name;
    private final BiConsumer<IGuiWidget, Integer> setter;
    private int value;

    public WidgetIntStyleProperty(String name, Integer value, BiConsumer<IGuiWidget, Integer> setter) {
        this.name = name;
        this.value = value;
        this.setter = setter;
    }

    @Override
    public String propertyName() {
        return this.name;
    }

    @Override
    public String valueString() {
        return String.valueOf(this.value);
    }

    @Override
    public Integer value() {
        return this.value;
    }

    @Override
    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public void apply(IStyleSheet sheet, IGuiWidget widget) {
        this.setter.accept(widget, this.value);
    }
}


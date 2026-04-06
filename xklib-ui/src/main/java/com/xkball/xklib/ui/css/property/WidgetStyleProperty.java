package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

import java.util.function.BiConsumer;

public class WidgetStyleProperty<T> implements IStyleProperty<T> {

    private final String name;
    private final BiConsumer<IGuiWidget, T> setter;
    private T value;

    public WidgetStyleProperty(String name, T value, BiConsumer<IGuiWidget, T> setter) {
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
    public T value() {
        return this.value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public void apply(IStyleSheet sheet, IGuiWidget widget) {
        this.setter.accept(widget, this.value);
    }
}


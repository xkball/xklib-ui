package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

import java.util.function.BiConsumer;

public class WidgetStyleProperty<W, T> implements IStyleProperty<T> {

    private final String name;
    private final Class<W> widgetType;
    private final BiConsumer<W, T> setter;
    private T value;

    public WidgetStyleProperty(String name, T value, Class<W> widgetType, BiConsumer<W, T> setter) {
        this.name = name;
        this.value = value;
        this.widgetType = widgetType;
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
        if (!this.widgetType.isInstance(widget)) {
            return;
        }
        this.setter.accept(this.widgetType.cast(widget), this.value);
    }
}


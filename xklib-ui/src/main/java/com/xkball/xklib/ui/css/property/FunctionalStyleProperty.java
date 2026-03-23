package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import dev.vfyjxf.taffy.style.TaffyStyle;

import java.util.function.BiConsumer;

public class FunctionalStyleProperty<T> implements IStyleProperty<T> {

    private final String name;
    private final BiConsumer<TaffyStyle, T> setter;
    private T value;

    public FunctionalStyleProperty(String name, T value, BiConsumer<TaffyStyle, T> setter) {
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
        widget.setStyle(style -> this.setter.accept(style, this.value));
    }
}


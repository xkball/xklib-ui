package com.xkball.xklib.ui.css.property;

import com.xkball.xklib.api.gui.css.IStyleProperty;
import com.xkball.xklib.api.gui.css.IStyleSheet;
import com.xkball.xklib.api.gui.widget.IGuiWidget;

import java.util.function.BiConsumer;

public class WidgetBooleanStyleProperty<W> implements IStyleProperty<Boolean> {

    private final String name;
    private final Class<W> widgetType;
    private final BiConsumer<W, Boolean> setter;
    private boolean value;

    public WidgetBooleanStyleProperty(String name, Boolean value, Class<W> widgetType, BiConsumer<W, Boolean> setter) {
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
    public Boolean value() {
        return this.value;
    }

    @Override
    public void setValue(Boolean value) {
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


package com.xkball.xklib.ui.widget;

import java.util.List;
import java.util.function.Function;

public class ComboBox<T> extends ContainerWidget{
    
    private final List<T> options;
    private final Function<T, String> toDisplay;
    private final boolean nullable;
    
    private T selected;
    private final Label displayLabel;
    private ComboBoxOverlay overlay;
    
    public ComboBox(List<T> options, Function<T, String> toDisplay, boolean nullable, Label displayLabel) {
        this.options = options;
        this.toDisplay = toDisplay;
        this.nullable = nullable;
        this.displayLabel = displayLabel;
    }
    
    public class ComboBoxOverlay extends ContainerWidget{
    
    }
}

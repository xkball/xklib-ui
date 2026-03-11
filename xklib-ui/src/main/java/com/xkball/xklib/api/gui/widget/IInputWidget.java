package com.xkball.xklib.api.gui.widget;

import com.xkball.xklib.ui.layout.LayoutVariable;

public interface IInputWidget<T> {
    
    T getValue();
    
    void setValue(T value);
    
    void bind(LayoutVariable<T> variable);
}

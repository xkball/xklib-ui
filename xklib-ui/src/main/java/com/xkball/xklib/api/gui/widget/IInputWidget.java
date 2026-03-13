package com.xkball.xklib.api.gui.widget;

public interface IInputWidget<T> {
    
    T getValue();
    
    void setValue(T value);
    
    void bind(ILayoutVariable<T> variable);
}

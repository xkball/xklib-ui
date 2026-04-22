package com.xkball.xklib.api.gui.widget;

public interface IInputWidget<T> {
    
    T getValue();
    
    void setValue(T value);
    
    //绑定时应该读取初始值
    IGuiWidget bind(ILayoutVariable<T> variable);
}

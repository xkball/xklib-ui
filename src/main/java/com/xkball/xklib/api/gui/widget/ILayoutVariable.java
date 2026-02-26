package com.xkball.xklib.api.gui.widget;

public interface ILayoutVariable<T> {
    
    void set(T value);
    
    T get();
    
    void addCallback(Runnable runnable);
    
    void triggerCallbacks();
}

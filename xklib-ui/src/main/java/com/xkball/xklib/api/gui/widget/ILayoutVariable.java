package com.xkball.xklib.api.gui.widget;

import java.util.function.Consumer;

public interface ILayoutVariable<T> {
    
    void set(T value);
    
    T get();
    
    void addCallback(Consumer<T> runnable);
    
    void triggerCallbacks();
}

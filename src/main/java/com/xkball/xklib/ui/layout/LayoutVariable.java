package com.xkball.xklib.ui.layout;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.widget.ILayoutVariable;

import java.util.ArrayList;
import java.util.List;

public class LayoutVariable<T> implements ILayoutVariable<T> {
    
    private T value;
    private final List<Runnable> callbacks = new ArrayList<>();
    
    public LayoutVariable(T value) {
    
    }
    @Override
    public void set(T value) {
        synchronized (this){
            if(this.value.equals(value)) return;
            this.value = value;
            this.triggerCallbacks();
        }
    }
    
    @Override
    public void triggerCallbacks() {
        XKLib.gui.submitTreeUpdate(() -> callbacks.forEach(Runnable::run));
    }
    
    @Override
    public T get() {
        return value;
    }
    
    @Override
    public void addCallback(Runnable runnable) {
        this.callbacks.add(runnable);
    }
}

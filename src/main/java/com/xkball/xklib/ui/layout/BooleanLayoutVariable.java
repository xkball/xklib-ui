package com.xkball.xklib.ui.layout;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.widget.ILayoutVariable;

import java.util.ArrayList;
import java.util.List;

public class BooleanLayoutVariable implements ILayoutVariable<Boolean> {
    
    private boolean value;
    private final List<Runnable> callbacks = new ArrayList<>();
    
    public void setAsBoolean(boolean value){
        synchronized (this){
            if(this.value == value) return;
            this.value = value;
            this.triggerCallbacks();
        }
    }
    
    @Override
    public void set(Boolean value) {
        this.setAsBoolean(value);
    }
    
    @Override
    public Boolean get() {
        return value;
    }
    
    @Override
    public void triggerCallbacks() {
        XKLib.gui.submitTreeUpdate(() -> callbacks.forEach(Runnable::run));
    }
    
    @Override
    public void addCallback(Runnable runnable) {
        this.callbacks.add(runnable);
    }
}

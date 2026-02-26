package com.xkball.xklib.ui.layout;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.widget.ILayoutVariable;

import java.util.ArrayList;
import java.util.List;

public class IntLayoutVariable implements ILayoutVariable<Integer> {
    
    private int value;
    private final List<Runnable> callbacks = new ArrayList<>();
    
    public void setAsInt(int value){
        synchronized (this){
            if(this.value == value) return;
            this.value = value;
            this.triggerCallbacks();
        }
    }
    
    @Override
    public void set(Integer value) {
        this.setAsInt(value);
    }
    
    @Override
    public void triggerCallbacks() {
        XKLib.gui.submitTreeUpdate(() -> callbacks.forEach(Runnable::run));
    }
    
    @Override
    public Integer get() {
        return value;
    }
    
    public int getAsInt(){
        return value;
    }
    
    @Override
    public void addCallback(Runnable runnable) {
        this.callbacks.add(runnable);
    }
}

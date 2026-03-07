package com.xkball.xklib.ui.layout;

import com.xkball.xklib.api.gui.widget.ILayoutVariable;
import com.xkball.xklib.ui.system.GuiSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class IntLayoutVariable implements ILayoutVariable<Integer> {
    
    private int value;
    private final List<Consumer<Integer>> callbacks = new ArrayList<>();
    
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
        GuiSystem.INSTANCE.get().submitTreeUpdate(() -> callbacks.forEach(c -> c.accept(value)));
    }
    
    @Override
    public Integer get() {
        return value;
    }
    
    public int getAsInt(){
        return value;
    }
    
    @Override
    public void addCallback(Consumer<Integer> runnable) {
        this.callbacks.add(runnable);
    }
}

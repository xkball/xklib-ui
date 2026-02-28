package com.xkball.xklib.ui.layout;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.api.gui.widget.ILayoutVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FloatLayoutVariable implements ILayoutVariable<Float> {
    
    private float value;
    private final List<Consumer<Float>> callbacks = new ArrayList<>();
    
    public FloatLayoutVariable(float value){
        this.value = value;
    }
    public void setAsFloat(float value){
        synchronized (this){
            if(this.value == value) return;
            this.value = value;
            this.triggerCallbacks();
        }
    }
    
    @Override
    public void set(Float value) {
        this.setAsFloat(value);
    }
    
    @Override
    public Float get() {
        return value;
    }
    
    @Override
    public void triggerCallbacks() {
        XKLib.gui.submitTreeUpdate(() -> callbacks.forEach(c -> c.accept(value)));
    }
    
    @Override
    public void addCallback(Consumer<Float> runnable) {
        this.callbacks.add(runnable);
    }
}

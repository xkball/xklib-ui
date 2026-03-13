package com.xkball.xklib.ui.layout;

import com.xkball.xklib.api.gui.widget.ILayoutVariable;
import com.xkball.xklib.ui.system.GuiSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BooleanLayoutVariable implements ILayoutVariable<Boolean> {
    
    private boolean value;
    private final List<Consumer<Boolean>> callbacks = new ArrayList<>();
    
    public BooleanLayoutVariable(){
    
    }
    
    public BooleanLayoutVariable(boolean value){
        this.value = value;
    }
    
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
        GuiSystem.INSTANCE.get().submitTreeUpdate(() -> callbacks.forEach(c -> c.accept(value)));
    }
    
    @Override
    public void addCallback(Consumer<Boolean> runnable) {
        this.callbacks.add(runnable);
    }
}

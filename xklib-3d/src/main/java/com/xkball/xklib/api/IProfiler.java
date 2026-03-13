package com.xkball.xklib.api;

public interface IProfiler {
    
    void push(String label);
    
    void pop();
    
    long getTime(String label);
    
    void endTick();
    
    default void pushPop(String label){
        this.pop();
        this.push(label);
    }
}

package com.xkball.xklib.utils;

import java.util.function.Supplier;

public class Lazy<T> {
    
    private final Supplier<T> supplier;
    private T value;
    
    public static <T> Lazy<T> of(Supplier<T> supplier){
        return new Lazy<>(supplier);
    }
    
    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }
    
    public T get(){
        if(value == null){
            synchronized (this){
                if (value == null){
                    value = supplier.get();
                }
            }
        }
        return value;
    }
}

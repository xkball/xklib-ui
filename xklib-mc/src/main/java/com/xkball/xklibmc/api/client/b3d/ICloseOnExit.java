package com.xkball.xklibmc.api.client.b3d;


import com.xkball.xklibmc.client.b3d.ClientRenderObjects;

public interface ICloseOnExit<T extends ICloseOnExit<T>> extends AutoCloseable {
    
    @SuppressWarnings("unchecked")
    default T setCloseOnExit(){
        ClientRenderObjects.INSTANCE.addCloseOnExit(this);
        return (T) this;
    }
}

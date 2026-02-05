package com.xkball.xklib.resource;

import com.xkball.xklib.ui.XKLibUI;
import org.jetbrains.annotations.NotNull;

public record ResourceLocation(String namespace, String path) {
    
    public static ResourceLocation of(String path){
        return new ResourceLocation(XKLibUI.NAME, path);
    }
    
    @NotNull
    @Override
    public String toString() {
        return String.format("ResourceLocation[%s:%s]", namespace, path);
    }
}

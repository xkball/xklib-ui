package com.xkball.xklib.resource;

import com.xkball.xklib.XKLib;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record ResourceLocation(String namespace, String path) {
    
    public static ResourceLocation of(String path){
        return new ResourceLocation(XKLib.NAME, path);
    }
    
    @NotNull
    @Override
    public String toString() {
        return String.format("ResourceLocation[%s:%s]", namespace, path);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResourceLocation(String namespace1, String path1))) return false;
        return Objects.equals(path, path1) && Objects.equals(namespace, namespace1);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }
}

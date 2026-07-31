package com.xkball.xklibmc.client.b3d.texture;

import com.mojang.blaze3d.textures.GpuTexture;

import java.util.HashSet;
import java.util.Set;

public class B3dTextureList {
    
    public static final Set<GpuTexture> TEXTURES = new HashSet<>();
    
    public static void create(GpuTexture texture){
        TEXTURES.add(texture);
    }
    
    public static void delete(GpuTexture texture){
        TEXTURES.remove(texture);
    }
    
    public static Set<GpuTexture> getChecked(){
        var result = new HashSet<GpuTexture>();
        for(var texture : TEXTURES){
            if (!texture.isClosed()) {
                result.add(texture);
            }
        }
        return result;
    }
}

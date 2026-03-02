package com.xkball.xklib.x3d.api.resource;

import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.utils.Lazy;
import com.xkball.xklib.x3d.api.render.ITexture;
import com.xkball.xklib.x3d.api.render.ITextureAtlasSprite;

import java.util.ServiceLoader;

public interface ITextureManager {
    
    Lazy<ITextureManager> INSTANCE = Lazy.of(() -> {
        var loader = ServiceLoader.load(ITextureManager.class);
        return loader.findFirst().orElseThrow();
    });
    
    static ITextureManager getInstance(){
        return INSTANCE.get();
    }
    
    ITextureAtlasSprite getSprite(ResourceLocation location);
    
    ITexture getTexture(ResourceLocation location);
    
}

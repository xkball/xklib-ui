package com.xkball.xklib.x3d.api.resource;

import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.utils.Lazy;
import com.xkball.xklib.x3d.api.render.ITexture;
import com.xkball.xklib.x3d.api.render.ITextureAtlasSprite;

public interface ITextureManager {
    
    Lazy<ITextureManager> INSTANCE = Lazy.ofSPI(ITextureManager.class);
    
    static ITextureManager getInstance(){
        return INSTANCE.get();
    }
    
    ITextureAtlasSprite getSprite(ResourceLocation location);
    
    ITexture getTexture(ResourceLocation location);
    
}

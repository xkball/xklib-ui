package com.xkball.xklib.x3d.api.render;

import com.xkball.xklib.resource.ResourceLocation;

public interface ITextureAtlasSprite {
    
    ResourceLocation atlasLocation();
    
    ITexture texture();
    
    float getU0();
    
    float getU1();
    
    float getV0();
    
    float getV1();
    
    float getU(float offset);
    
    float getV(float offset);
    
    static ITextureAtlasSprite cast(Object o) {
        return (ITextureAtlasSprite)o;
    }
}

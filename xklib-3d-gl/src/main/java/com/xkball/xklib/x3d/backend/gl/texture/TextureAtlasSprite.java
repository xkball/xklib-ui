package com.xkball.xklib.x3d.backend.gl.texture;

import com.xkball.xklib.x3d.api.render.ITextureAtlasSprite;
import com.xkball.xklib.resource.ResourceLocation;

public record TextureAtlasSprite(ResourceLocation location,TextureAtlas texture, int x, int y, float u0, float v0, float u1, float v1) implements ITextureAtlasSprite {
    
    
    @Override
    public ResourceLocation atlasLocation() {
        return location;
    }
    
    @Override
    public float getU0() {
        return u0;
    }
    
    @Override
    public float getU1() {
        return u1;
    }
    
    @Override
    public float getV0() {
        return v0;
    }
    
    @Override
    public float getV1() {
        return v1;
    }
    
    @Override
    public float getU(float offset) {
        float diff = this.u1 - this.u0;
        return this.u0 + diff * offset;
    }
    
    @Override
    public float getV(float offset) {
        float diff = this.v1 - this.v0;
        return this.v0 + diff * offset;
    }
}

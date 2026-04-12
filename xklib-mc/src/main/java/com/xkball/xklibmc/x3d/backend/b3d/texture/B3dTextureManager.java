package com.xkball.xklibmc.x3d.backend.b3d.texture;

import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklib.x3d.api.render.ITexture;
import com.xkball.xklib.x3d.api.render.ITextureAtlasSprite;
import com.xkball.xklib.x3d.api.resource.ITextureManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.data.AtlasIds;

public class B3dTextureManager implements ITextureManager {
    
    private final TextureManager inner;
    private final AtlasManager innerAtlasManager;
    
    public B3dTextureManager(TextureManager inner, AtlasManager innerAtlasManager) {
        this.inner = inner;
        this.innerAtlasManager = innerAtlasManager;
    }
    
    @Override
    public ITextureAtlasSprite getSprite(ResourceLocation location) {
        var atlas = innerAtlasManager.getAtlasOrThrow(AtlasIds.GUI);
        return (ITextureAtlasSprite) atlas.getSprite(VanillaUtils.convertRL(location));
    }
    
    @Override
    public ITexture getTexture(ResourceLocation location) {
        return (ITexture) inner.getTexture(VanillaUtils.convertRL(location));
    }
    
}

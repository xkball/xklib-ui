package com.xkball.xklibmc.mixin;

import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklib.x3d.api.render.ITexture;
import com.xkball.xklib.x3d.api.render.ITextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextureAtlasSprite.class)
public abstract class MixinTextureAtlasSprite implements ITextureAtlasSprite {
    @Shadow
    @Final
    private Identifier atlasLocation;
    
    @Override
    public ResourceLocation atlasLocation() {
        return VanillaUtils.convertId(this.atlasLocation);
    }
    
    @Override
    public ITexture texture() {
        return null;
    }
}

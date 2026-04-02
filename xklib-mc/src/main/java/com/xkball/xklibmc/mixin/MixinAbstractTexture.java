package com.xkball.xklibmc.mixin;

import com.xkball.xklib.x3d.api.render.ITexture;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractTexture.class)
public class MixinAbstractTexture implements ITexture {
    
    @Override
    public int getId() {
        return 0;
    }
}

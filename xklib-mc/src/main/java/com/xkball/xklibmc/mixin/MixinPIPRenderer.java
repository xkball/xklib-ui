package com.xkball.xklibmc.mixin;

import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PictureInPictureRenderer.class)
public class MixinPIPRenderer {

    @ModifyConstant(method = "prepareTexturesAndProjection",constant = @Constant(intValue = 13))
    public int modifyColorTextureFlag(int constant){
        return 15;
    }
    
    @ModifyConstant(method = "prepareTexturesAndProjection",constant = @Constant(intValue = 9))
    public int modifyDepthTextureFlag(int constant){
        return 15;
    }
}

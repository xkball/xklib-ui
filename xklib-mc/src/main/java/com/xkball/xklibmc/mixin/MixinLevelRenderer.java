package com.xkball.xklibmc.mixin;

import com.xkball.xklibmc.client.b3d.ClientRenderObjects;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    
    @Inject(method = "endFrame",at = @At("RETURN"))
    public void onEndFrame(CallbackInfo ci){
        for(var ef : ClientRenderObjects.INSTANCE.endFrame){
            ef.endFrame();
        }
    }
}

package com.xkball.xklibmc.mixin;


import com.xkball.xklibmc.client.b3d.ClientRenderObjects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    
    @Inject(method = "renderLevel", at = @At("HEAD"))
    public void beforeRenderLevel(DeltaTracker deltaTracker, CallbackInfo ci){
        for(var up : ClientRenderObjects.INSTANCE.everyFrame){
            up.update();
        }
    }
}

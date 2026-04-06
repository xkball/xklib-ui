package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;

import com.xkball.xklibmc.client.b3d.ClientRenderObjects;
import org.lwjgl.opengl.GL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class MixinRenderSystem {

    @Inject(method = "initRenderer",at = @At("RETURN"))
    private static void afterInitRender(GpuDevice device, CallbackInfo ci) {
        ClientRenderObjects.init(GL.getCapabilities());
    }
}

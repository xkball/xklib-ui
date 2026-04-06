package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlRenderPass;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.xkball.xklibmc.api.client.mixin.IExtendedRenderPass;
import com.xkball.xklibmc.client.b3d.pipeline.ExtendedRenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mixin(GlRenderPass.class)
public class MixinGlRenderPass implements IExtendedRenderPass {
    
    @Shadow @Nullable protected GlRenderPipeline pipeline;
    @Unique
    protected final Map<String, GpuBufferSlice> xklib$ssbo = new HashMap<>();
    
    @Inject(method = "setPipeline", at = @At("RETURN"))
    public void afterSetPipeline(RenderPipeline pipeline, CallbackInfo ci){
        if(pipeline instanceof ExtendedRenderPipeline extendedRenderPipeline){
            extendedRenderPipeline.apply((RenderPassBackend) this);
        }
    }
    
    @Override
    public void xklib$setSSBO(String name, GpuBufferSlice ssbo) {
        xklib$ssbo.put(name, ssbo);
    }
    
    @Override
    public GlRenderPipeline xklib$getGLRenderPipeline() {
        return pipeline;
    }
    
    @Override
    public Map<String, GpuBufferSlice> xklib$getSSBOs() {
        return xklib$ssbo;
    }
}

package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.xkball.xklibmc.api.client.mixin.IExtendedRenderPass;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(RenderPass.class)
public interface MixinRenderPass extends IExtendedRenderPass {
    
    @Override
    default void xklib$setSSBO(String name, GpuBufferSlice ssbo) {}
    
    @Override
    default Map<String, GpuBufferSlice> xklib$getSSBOs(){
        return null;
    }
    
    @Override
    default GlRenderPipeline xklib$getGLRenderPipeline(){
        return null;
    }
}

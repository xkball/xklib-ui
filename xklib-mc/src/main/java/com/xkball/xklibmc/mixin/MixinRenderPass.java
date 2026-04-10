package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.xkball.xklibmc.api.client.mixin.IExtendedRenderPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(RenderPass.class)
public class MixinRenderPass implements IExtendedRenderPass {
    
    @Shadow
    @Final
    private RenderPassBackend backend;
    
    @Override
    public void xklib$setSSBO(String name, GpuBufferSlice ssbo) {
        if(this.backend instanceof IExtendedRenderPass ierp){
            ierp.xklib$setSSBO(name, ssbo);
        }
    }
    
    @Override
    public Map<String, GpuBufferSlice> xklib$getSSBOs(){
        if(this.backend instanceof IExtendedRenderPass ierp){
            ierp.xklib$getSSBOs();
        }
        return Map.of();
    }
    
    @Override
    public GlRenderPipeline xklib$getGLRenderPipeline(){
        if(this.backend instanceof IExtendedRenderPass ierp){
            return ierp.xklib$getGLRenderPipeline();
        }
        return null;
    }
    
    @Override
    public void xklib$multiDrawElementsIndirect(GpuBuffer command, int drawCount) {
        if(this.backend instanceof IExtendedRenderPass ierp){
            ierp.xklib$multiDrawElementsIndirect(command, drawCount);
        }
    }
}

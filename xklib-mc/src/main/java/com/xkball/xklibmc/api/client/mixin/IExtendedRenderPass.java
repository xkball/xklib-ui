package com.xkball.xklibmc.api.client.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlRenderPipeline;

import java.util.Map;

public interface IExtendedRenderPass {
    
    void xklib$setSSBO(String name, GpuBufferSlice ssbo);
    
    Map<String, GpuBufferSlice> xklib$getSSBOs();
    
    GlRenderPipeline xklib$getGLRenderPipeline();
    
    void xklib$multiDrawElementsIndirect(GpuBuffer command,int drawCount);
    
    static IExtendedRenderPass cast(Object obj){
        return (IExtendedRenderPass)obj;
    }
}

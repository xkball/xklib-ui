package com.xkball.xklibmc.utils;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

import java.util.Objects;

public class GLUtils {
    
    public static int getGLId(GpuTextureView view){
        return getGLId(view.texture());
    }
    
    public static int getGLId(GpuTexture texture){
        if(texture instanceof GlTexture glTexture) return glTexture.id;
        throw new IllegalStateException("Cannot get texture id from: " + texture);
    }
    
    public static void clear(RenderTarget target){
        clear(target, true);
    }
    
    public static void clear(RenderTarget target, boolean clearDepth){
        if(target.useDepth && clearDepth){
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(Objects.requireNonNull(target.getColorTexture()),0,Objects.requireNonNull(target.getDepthTexture()),1d);
        }
        else {
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(Objects.requireNonNull(target.getColorTexture()),0);
        }
    }
    
    public static void copyFrameBufferColorTo(RenderTarget from, RenderTarget to) {
            RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(Objects.requireNonNull(from.getColorTexture()), Objects.requireNonNull(to.getColorTexture()),0, 0, 0, 0, 0, from.width, from.height);
    }
    
    public static void copyFrameBufferDepthTo(RenderTarget from, RenderTarget to) {
        to.copyDepthFrom(from);
    }
    
//    public static long getNamedBufferAddrNV(int buffer){
//        var result = NVShaderBufferLoad.glGetNamedBufferParameterui64NV(buffer, NVShaderBufferLoad.GL_BUFFER_GPU_ADDRESS_NV);
//        NVShaderBufferLoad.glMakeNamedBufferResidentNV(buffer, GlConst.GL_READ_ONLY);
//        return result;
//    }
}

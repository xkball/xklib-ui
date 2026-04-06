package com.xkball.xklibmc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderSource;

import com.xkball.xklibmc.api.client.mixin.IExtendedGLProgram;
import com.xkball.xklibmc.client.b3d.pipeline.ExtendedRenderPipeline;
import com.xkball.xklibmc.client.b3d.uniform.SSBOIndexStorage;
import org.lwjgl.opengl.GL43;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlDevice.class)
public class MixinGLDevice {
    
    
    @Inject(method = "compileProgram", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/GlProgram;setupUniforms(Ljava/util/List;Ljava/util/List;)V", shift = At.Shift.AFTER))
    public void onCompilePipeline(RenderPipeline pipeline, ShaderSource p_461146_, CallbackInfoReturnable<GlRenderPipeline> cir, @Local GlProgram glprogram){
        if(pipeline instanceof ExtendedRenderPipeline extendedRenderPipeline){
            for(var ssboName : extendedRenderPipeline.SSBOs){
                var index = GL43.glGetProgramResourceIndex(glprogram.getProgramId(), GL43.GL_SHADER_STORAGE_BLOCK, ssboName);
                if(index != -1) IExtendedGLProgram.cast(glprogram).dysonCubeProgram$getSSBOByName().put(ssboName,new SSBOIndexStorage(index));
            }
        }
    }
}

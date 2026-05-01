package com.xkball.xklibmc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.GpuOutOfMemoryException;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDebugLabel;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderSource;

import com.mojang.blaze3d.systems.GpuDeviceBackend;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.xkball.xklibmc.api.client.mixin.IExtendedGLProgram;
import com.xkball.xklibmc.api.client.mixin.IExtendedGpuDevice;
import com.xkball.xklibmc.client.b3d.pipeline.ExtendedRenderPipeline;
import com.xkball.xklibmc.client.b3d.texture.GLSparseTexture;
import com.xkball.xklibmc.client.b3d.uniform.SSBOIndexStorage;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.ARBSparseTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL46;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlDevice.class)
public abstract class MixinGLDevice implements IExtendedGpuDevice {
    
    @Inject(method = "compileProgram", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/GlProgram;setupUniforms(Ljava/util/List;Ljava/util/List;)V", shift = At.Shift.AFTER))
    public void onCompilePipeline(RenderPipeline pipeline, ShaderSource p_461146_, CallbackInfoReturnable<GlRenderPipeline> cir, @Local GlProgram glprogram){
        if(pipeline instanceof ExtendedRenderPipeline extendedRenderPipeline){
            for(var ssboName : extendedRenderPipeline.SSBOs){
                var index = GL43.glGetProgramResourceIndex(glprogram.getProgramId(), GL43.GL_SHADER_STORAGE_BLOCK, ssboName);
                if(index != -1) IExtendedGLProgram.cast(glprogram).xklib$getSSBOByName().put(ssboName,new SSBOIndexStorage(index));
            }
        }
    }
    
    @Override
    public GpuTexture xklib$createSparseTexture(@Nullable String label, int usage, TextureFormat format, int width, int height, int depthOrLayers) {
        GlStateManager.clearGlErrors();
        int id = GlStateManager._genTexture();
        if (label == null) {
            label = String.valueOf(id);
        }
        GlStateManager._bindTexture(id);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, ARBSparseTexture.GL_TEXTURE_SPARSE_ARB, 1);
        var pageSizeX = GL42.glGetInternalformati(GL11.GL_TEXTURE_2D, GlConst.toGlInternalId(format), ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_X_ARB);
        var pageSizeY = GL42.glGetInternalformati(GL11.GL_TEXTURE_2D, GlConst.toGlInternalId(format), ARBSparseTexture.GL_VIRTUAL_PAGE_SIZE_Y_ARB);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LEVEL, 0);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_LOD, 0);
        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LOD, 0);
//        if (format.hasDepthAspect()) {
//            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL30.GL_TEXTURE_COMPARE_MODE, 0);
//        }
        GL42.glTexStorage2D(GL11.GL_TEXTURE_2D, 1, GlConst.toGlInternalId(format), width, height);
        
        int error = GlStateManager._getError();
        if (error == 1285) {
            throw new GpuOutOfMemoryException("Could not allocate texture of " + width + "x" + height + " for " + label);
        } else if (error != 0) {
            throw new IllegalStateException("OpenGL error " + error);
        } else {
            return new GLSparseTexture(usage, label, format, width, height, depthOrLayers, 1, id, pageSizeX, pageSizeY);
        }
    }
}

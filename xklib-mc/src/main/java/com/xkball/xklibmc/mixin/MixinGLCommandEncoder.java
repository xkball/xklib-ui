package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlBuffer;
import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlRenderPass;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.xkball.xklibmc.api.client.mixin.IExtendedCommandEncoder;
import com.xkball.xklibmc.api.client.mixin.IExtendedGLProgram;
import com.xkball.xklibmc.api.client.mixin.IExtendedRenderPass;
import com.xkball.xklibmc.client.b3d.pipeline.ExtendedRenderPipeline;
import com.xkball.xklibmc.client.b3d.texture.GLSparseTexture;
import com.xkball.xklibmc.utils.GLUtils;
import net.minecraft.server.packs.repository.Pack;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;

import static org.lwjgl.system.MemoryStack.stackGet;

@Mixin(GlCommandEncoder.class)
public abstract class MixinGLCommandEncoder implements IExtendedCommandEncoder {
    
    @Shadow
    protected abstract boolean trySetup(GlRenderPass renderPass, Collection<String> dynamicUniforms);
    
    @Shadow
    @Final
    private GlDevice device;
    @Unique
    private GpuTextureView dysonCubeProgram$fboColor;
    @Unique
    private boolean dysonCubeProgram$needClearDrawBuffers = false;
    
    @Inject(method = "createRenderPass(Ljava/util/function/Supplier;Lcom/mojang/blaze3d/textures/GpuTextureView;Ljava/util/OptionalInt;Lcom/mojang/blaze3d/textures/GpuTextureView;Ljava/util/OptionalDouble;)Lcom/mojang/blaze3d/systems/RenderPassBackend;",at = @At("HEAD"))
    public void onCreateRenderPass(Supplier<String> p_419957_, GpuTextureView color, OptionalInt p_410460_, @Nullable GpuTextureView p_423565_, OptionalDouble p_423486_, CallbackInfoReturnable<RenderPass> cir){
        this.dysonCubeProgram$fboColor = color;
    }
    
    @Inject(method = "writeToTexture(Lcom/mojang/blaze3d/textures/GpuTexture;Ljava/nio/ByteBuffer;Lcom/mojang/blaze3d/platform/NativeImage$Format;IIIIII)V",
            at = @At("HEAD"),
            cancellable = true)
    public void onWriteToTexture(GpuTexture destination, ByteBuffer source, NativeImage.Format format, int mipLevel, int depthOrLayer, int destX, int destY, int width, int height, CallbackInfo ci){
        if(destination instanceof GLSparseTexture texture){
            if(mipLevel != 0 || depthOrLayer != 0){
                throw new IllegalArgumentException("Invalid mip level or depth or layer");
            }
            texture.upload(destX,destY,width,height,format, source);
            ci.cancel();
        }
    }
    
    @Inject(method = "trySetup",at = @At("RETURN"))
    public void onTrySetup(GlRenderPass renderPass, Collection<String> uniforms, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValueZ()){
            var pass = IExtendedRenderPass.cast(renderPass);
            var glprogram = pass.xklib$getGLRenderPipeline().program();
            var program = IExtendedGLProgram.cast(glprogram);
            var pipeline = pass.xklib$getGLRenderPipeline();
            for(var entry : program.xklib$getSSBOByName().entrySet()){
                if(pass.xklib$getSSBOs().containsKey(entry.getKey())){
                    var index = entry.getValue().binding();
                    var buffer = pass.xklib$getSSBOs().get(entry.getKey());
                    GL43.glBindBufferRange(GL43.GL_SHADER_STORAGE_BUFFER,index,((GlBuffer)buffer.buffer()).handle,buffer.offset(),buffer.length());
                }
            }
            if(pipeline.info() instanceof ExtendedRenderPipeline ePipeline){
                var mrtBindings = ePipeline.multiTargetBindings;
                if(!mrtBindings.isEmpty()){
                    this.dysonCubeProgram$needClearDrawBuffers = true;
                    MemoryStack stack = stackGet();
                    int stackPointer = stack.getPointer();
                    try {
                        IntBuffer buf = stack.mallocInt(mrtBindings.size() + 1);
                        buf.put(GL30.GL_COLOR_ATTACHMENT0);
                        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, GLUtils.getGLId(this.dysonCubeProgram$fboColor), 0);
                        for(var entry : mrtBindings){
                            buf.put(GL30.GL_COLOR_ATTACHMENT0 + entry.getFirst());
                            var tex = GLUtils.getGLId(entry.getSecond().get());
                            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + entry.getFirst(), GL30.GL_TEXTURE_2D, tex, 0);
                        }
                        buf.flip();
                        GL30.glDrawBuffers(buf);
                    } finally {
                        stack.setPointer(stackPointer);
                    }
                    
                }
            }
        }
    }
    
    @Inject(method = "finishRenderPass",at = @At("HEAD"))
    public void onEndRenderPass(CallbackInfo ci){
        if(this.dysonCubeProgram$needClearDrawBuffers){
            this.dysonCubeProgram$needClearDrawBuffers = false;
            GL30.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);
        }
        this.dysonCubeProgram$fboColor = null;
    }
    
    @Override
    public void xklib$multiDrawElementsIndirect(GlRenderPass renderPass, GpuBuffer command, int drawCount) {
        if (this.trySetup(renderPass, Collections.emptyList())){
            var pipeline = renderPass.pipeline;
            this.device.vertexArrayCache().bindVertexArray(pipeline.info().getVertexFormat(), (GlBuffer)renderPass.vertexBuffers[0]);
            GlStateManager._glBindBuffer(GL43.GL_ELEMENT_ARRAY_BUFFER, ((GlBuffer)renderPass.indexBuffer).handle);
            GlStateManager._glBindBuffer(GL43.GL_DRAW_INDIRECT_BUFFER,((GlBuffer) command).handle);
            GL43.glMultiDrawElementsIndirect(
                    GlConst.toGl(pipeline.info().getVertexFormatMode()),
                    GlConst.toGl(renderPass.indexType),
                    MemoryUtil.NULL,
                    drawCount,
                    20);
            
        }
    }
}

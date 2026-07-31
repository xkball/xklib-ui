package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.xkball.xklibmc.client.b3d.texture.B3dTextureList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlTexture.class)
public class MixinGLTexture {
    
    @Inject(method = "<init>",at = @At("RETURN"))
    public void onInit(int usage, String label, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels, int id, CallbackInfo ci){
        B3dTextureList.create((GpuTexture)(Object) this);
    }
    
    @Inject(method = "close", at = @At("RETURN"))
    public void onClose(CallbackInfo ci){
        B3dTextureList.delete((GpuTexture)(Object) this);
    }
}

package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.opengl.GlProgram;
import com.xkball.xklibmc.api.client.mixin.IExtendedGLProgram;
import com.xkball.xklibmc.client.b3d.uniform.SSBOIndexStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(GlProgram.class)
public class MixinGLProgram implements IExtendedGLProgram {
    
    @Unique
    public final Map<String, SSBOIndexStorage> xklib$SSBOByName = new LinkedHashMap<>();
    
    @Override
    public Map<String, SSBOIndexStorage> dysonCubeProgram$getSSBOByName() {
        return xklib$SSBOByName;
    }
    
}

package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.opengl.GlDebugLabel;
import com.mojang.blaze3d.opengl.VertexArrayCache;
import org.lwjgl.opengl.GLCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Set;

@Mixin(VertexArrayCache.class)
public class MixinVertexArrayCache {
    
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static VertexArrayCache create(GLCapabilities capabilities, GlDebugLabel debugLabels, Set<String> enabledExtensions){
        return new VertexArrayCache.Emulated(debugLabels);
    }
}

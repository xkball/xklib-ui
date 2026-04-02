package com.xkball.xklibmc.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklib.x3d.api.render.IGpuBuffer;
import com.xkball.xklib.x3d.api.render.IRenderPipeline;
import com.xkball.xklib.x3d.api.render.IShaderProgram;
import com.xkball.xklib.x3d.api.render.ITexture;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import com.xkball.xklib.x3d.backend.vertex.VertexFormat;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(RenderPipeline.class)
public abstract class MixinRenderPipeline implements IRenderPipeline {
    @Shadow
    @Final
    private Identifier location;
    
    @Override
    public ResourceLocation location() {
        return VanillaUtils.convertId(this.location);
    }
    
    @Override
    public VertexFormat.Mode mode() {
        return null;
    }
    
    @Override
    public VertexFormat format() {
        return null;
    }
    
    @Override
    public IShaderProgram shader() {
        return null;
    }
    
    @Override
    public void draw(IGpuBuffer vbo) {
    
    }
    
    @Override
    public void draw(BufferBuilder builder) {
    
    }
    
    @Override
    public void bindSampler(int unit, Supplier<ITexture> texture) {
    
    }
}

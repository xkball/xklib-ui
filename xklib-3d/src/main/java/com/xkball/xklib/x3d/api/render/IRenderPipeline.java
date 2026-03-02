package com.xkball.xklib.x3d.api.render;

import com.xkball.xklib.annotation.NoImplInMinecraft;
import com.xkball.xklib.annotation.NullInMinecraft;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.x3d.backend.vertex.BufferBuilder;
import com.xkball.xklib.x3d.backend.vertex.VertexFormat;

import java.util.function.Supplier;

public interface IRenderPipeline {

    ResourceLocation location();
    
    @NullInMinecraft
    VertexFormat.Mode mode();
    
    @NullInMinecraft
    VertexFormat format();
    
    @NullInMinecraft
    IShaderProgram shader();
    
    @NoImplInMinecraft
    void draw(BufferBuilder builder);
    
    @NullInMinecraft
    void bindSampler(int unit, Supplier<ITexture> texture);
}

package com.xkball.xklib.api.render;

import com.xkball.xklib.api.annotation.NoImplInMinecraft;
import com.xkball.xklib.api.annotation.NullInMinecraft;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.backend.gl.shader.ShaderProgram;
import com.xkball.xklib.ui.backend.gl.vertex.BufferBuilder;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormat;

import java.util.function.Supplier;

public interface IRenderPipeline {

    ResourceLocation location();
    
    @NullInMinecraft
    VertexFormat.Mode mode();
    
    @NullInMinecraft
    VertexFormat format();
    
    @NullInMinecraft
    ShaderProgram shader();
    
    @NoImplInMinecraft
    void draw(BufferBuilder builder);
    
    @NullInMinecraft
    void bindSampler(int unit, Supplier<ITexture> texture);
}

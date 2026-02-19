package com.xkball.xklib.ui.backend.gl.pipeline;

import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormat;
import com.xkball.xklib.ui.backend.gl.vertex.VertexFormats;

public class RenderPipelines {
    
    public static final RenderPipeline POSITION_TEX_COLOR = RenderPipeline.builder(ResourceLocation.of("pos_tex_color"))
            .vertexShader(ResourceLocation.of("shaders/core/pos_tex_color.vsh"))
            .fragmentShader(ResourceLocation.of("shaders/core/pos_tex_color.fsh"))
            .format(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_TEX_COLOR)
            .sampler("uTexture", () -> null)
            .depthTest(false)
            .cullFace(false)
            .build();
    
    public static final RenderPipeline FONT = RenderPipeline.builder(ResourceLocation.of("font"))
            .vertexShader(ResourceLocation.of("shaders/core/font.vsh"))
            .fragmentShader(ResourceLocation.of("shaders/core/font.fsh"))
            .format(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_TEX_COLOR)
            .sampler("uTexture", () -> null)
            .depthTest(false)
            .cullFace(false)
            .blendFunction(BlendFunction.TRANSLUCENT)
            .build();
    
    public static final RenderPipeline GUI = RenderPipeline.builder(ResourceLocation.of("pos_tex_color"))
            .vertexShader(ResourceLocation.of("shaders/core/pos_color.vsh"))
            .fragmentShader(ResourceLocation.of("shaders/core/pos_color.fsh"))
            .format(VertexFormat.Mode.QUADS, VertexFormats.POSITION_COLOR)
            .depthTest(false)
            .cullFace(false)
            .build();
    
    public static final RenderPipeline GUI_TEXTURED = RenderPipeline.builder(ResourceLocation.of("gui_textured"))
            .vertexShader(ResourceLocation.of("shaders/core/pos_tex_color.vsh"))
            .fragmentShader(ResourceLocation.of("shaders/core/pos_tex_color.fsh"))
            .format(VertexFormat.Mode.QUADS, VertexFormats.POSITION_TEX_COLOR)
            .sampler("uTexture", () -> null)
            .depthTest(false)
            .cullFace(false)
            .build();

}

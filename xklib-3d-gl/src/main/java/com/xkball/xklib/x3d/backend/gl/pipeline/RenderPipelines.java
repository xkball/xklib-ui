package com.xkball.xklib.x3d.backend.gl.pipeline;

import com.xkball.xklib.XKLib;
import com.xkball.xklib.resource.ResourceLocation;
import com.xkball.xklib.x3d.backend.vertex.VertexFormat;
import com.xkball.xklib.x3d.backend.vertex.VertexFormats;
import org.joml.Vector2f;

public class RenderPipelines {
    
    public static final ThreadLocal<RenderPipeline> POSITION_TEX_COLOR = RenderPipeline.builder(ResourceLocation.of("pos_tex_color"))
            .vertexShader(ResourceLocation.of("shaders/core/pos_tex_color.vsh"))
            .fragmentShader(ResourceLocation.of("shaders/core/pos_tex_color.fsh"))
            .format(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_TEX_COLOR)
            .sampler("uTexture", () -> null)
            .depthTest(false)
            .cullFace(false)
            .buildThreadLocal();
    
    public static final ThreadLocal<RenderPipeline> FONT = RenderPipeline.builder(ResourceLocation.of("font"))
            .vertexShader(ResourceLocation.of("shaders/core/font.vsh"))
            .fragmentShader(ResourceLocation.of("shaders/core/font.fsh"))
            .format(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_TEX_COLOR)
            .sampler("uTexture", () -> null)
            .depthTest(true)
            .cullFace(false)
            .blendFunction(BlendFunction.TRANSLUCENT)
            .buildThreadLocal();
    
    public static final ThreadLocal<RenderPipeline> GUI = RenderPipeline.builder(ResourceLocation.of("pos_tex_color"))
            .vertexShader(ResourceLocation.of("shaders/core/pos_color.vsh"))
            .fragmentShader(ResourceLocation.of("shaders/core/pos_color.fsh"))
            .format(VertexFormat.Mode.QUADS, VertexFormats.POSITION_COLOR)
            .blendFunction(BlendFunction.TRANSLUCENT)
            .depthTest(true)
            .cullFace(false)
            .buildThreadLocal();
    
    public static final ThreadLocal<RenderPipeline> GUI_TEXTURED = RenderPipeline.builder(ResourceLocation.of("gui_textured"))
            .vertexShader(ResourceLocation.of("shaders/core/pos_tex_color.vsh"))
            .fragmentShader(ResourceLocation.of("shaders/core/pos_tex_color.fsh"))
            .format(VertexFormat.Mode.QUADS, VertexFormats.POSITION_TEX_COLOR)
            .sampler("uTexture", () -> null)
            .blendFunction(BlendFunction.TRANSLUCENT)
            .depthTest(true)
            .cullFace(false)
            .buildThreadLocal();
    
    public static final ThreadLocal<RenderPipeline> GUI_ROUNDED_RECT = RenderPipeline.builder(ResourceLocation.of("gui_rounded_rect"))
            .vertexShader(ResourceLocation.of("shaders/core/gui_rounded_rect.vsh"))
            .fragmentShader(ResourceLocation.of("shaders/core/gui_rounded_rect.fsh"))
            .format(VertexFormat.Mode.QUADS, VertexFormats.POSITION_TEX_UV2_COLOR_EXTRA)
            .depthTest(true)
            .cullFace(false)
            .buildThreadLocal();
    
    public static final ThreadLocal<RenderPipeline> LINE = RenderPipeline.builder(ResourceLocation.of("line"))
            .vertexShader(ResourceLocation.of("shaders/core/line.vsh"))
            .fragmentShader(ResourceLocation.of("shaders/core/pos_color.fsh"))
            .format(VertexFormat.Mode.TRIANGLES, VertexFormats.POSITION_TEX_COLOR)
            .uniform("screenSize",u -> {
                var window = XKLib.RENDER_CONTEXT.get().getWindow();
                u.set((float)window.getWidth(), (float)window.getHeight());
            })
            .blendFunction(BlendFunction.TRANSLUCENT)
            .depthTest(true)
            .cullFace(false)
            .buildThreadLocal();

}

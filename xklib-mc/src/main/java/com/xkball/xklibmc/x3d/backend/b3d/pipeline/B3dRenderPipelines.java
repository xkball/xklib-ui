package com.xkball.xklibmc.x3d.backend.b3d.pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc.x3d.backend.b3d.vertex.B3dVertexFormats;


public class B3dRenderPipelines {
    
    public static final RenderPipeline ROUNDED_RECT = RenderPipeline.builder()
            .withLocation(VanillaUtils.modRL("gui_rounded_rect"))
            .withVertexShader(VanillaUtils.modRL("core/gui_rounded_rect"))
            .withFragmentShader(VanillaUtils.modRL("/gui_rounded_rect"))
            .withVertexFormat(B3dVertexFormats.POSITION_TEX_UV2_COLOR_EXTRA, VertexFormat.Mode.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(false)
            .build();
    
    public static final RenderPipeline LINE = RenderPipeline.builder()
            .withLocation(VanillaUtils.modRL("line"))
            .withVertexShader(VanillaUtils.modRL("core/line"))
            .withFragmentShader(VanillaUtils.modRL("core/pos_color"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.TRIANGLES)
            .withUniform("screenSize", UniformType.UNIFORM_BUFFER)
//            .uniform("screenSize",u -> {
//                var window = XKLib.RENDER_CONTEXT.get().getWindow();
//                u.set((float)window.getWidth(), (float)window.getHeight());
//            })
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(false)
            .build();
}

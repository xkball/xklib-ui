package com.xkball.xklibmc.x3d.backend.b3d.pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.xklibmc.client.b3d.pipeline.ExtendedRenderPipeline;
import com.xkball.xklibmc.client.b3d.uniform.XKLibUniforms;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc.x3d.backend.b3d.vertex.B3dVertexFormats;


public class B3dRenderPipelines {
    
    public static final ExtendedRenderPipeline ROUNDED_RECT = ExtendedRenderPipeline.builder()
            .withLocation(VanillaUtils.modRL("gui_rounded_rect"))
            .withVertexShader(VanillaUtils.modRL("core/gui_rounded_rect"))
            .withFragmentShader(VanillaUtils.modRL("core/gui_rounded_rect"))
            .withVertexFormat(B3dVertexFormats.POSITION_TEX_UV2_COLOR_EXTRA, VertexFormat.Mode.QUADS)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withCull(false)
            .buildExtended();
    
    public static final ExtendedRenderPipeline LINE = ExtendedRenderPipeline.builder()
            .withLocation(VanillaUtils.modRL("line"))
            .withVertexShader(VanillaUtils.modRL("core/line"))
            .withFragmentShader(VanillaUtils.modRL("core/pos_color"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.TRIANGLES)
            .withUniform("ScreenSize", UniformType.UNIFORM_BUFFER)
            .bindUniform("ScreenSize", XKLibUniforms.SCREEN_SIZE)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(false)
            .buildExtended();
    
    public static final ExtendedRenderPipeline BLUR = ExtendedRenderPipeline.builder()
            .withLocation(VanillaUtils.modRL("blur"))
            .withVertexShader(VanillaUtils.modRL("core/blit"))
            .withFragmentShader(VanillaUtils.modRL("core/blur"))
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
            .withSampler("input")
            .withUniform("ScreenSize", UniformType.UNIFORM_BUFFER)
            .bindUniform("ScreenSize", XKLibUniforms.SCREEN_SIZE)
            .buildExtended();
}

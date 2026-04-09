package com.xkball.xklibmc.x3d.backend.b3d.pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.xklibmc.client.b3d.pipeline.ExtendedRenderPipeline;
import com.xkball.xklibmc.client.b3d.uniform.XKLibUniforms;
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
    
    public static final RenderPipeline LINE = ExtendedRenderPipeline.builder()
            .withLocation(VanillaUtils.modRL("line"))
            .withVertexShader(VanillaUtils.modRL("core/line"))
            .withFragmentShader(VanillaUtils.modRL("core/pos_color"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.TRIANGLES)
            .withUniform("screenSize", UniformType.UNIFORM_BUFFER)
            .bindUniform("screenSize", XKLibUniforms.SCREEN_SIZE)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(false)
            .build();
    
    public static final ExtendedRenderPipeline WORLD_TERRAIN_PIP = ExtendedRenderPipeline.builder()
            .withLocation(VanillaUtils.modRL("world_terrain_pip"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
            .withVertexShader(VanillaUtils.modRL("core/world_terrain_pip"))
            .withFragmentShader("core/position_color")
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withSSBO("ABlock")
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(true)
            .buildExtended();
}

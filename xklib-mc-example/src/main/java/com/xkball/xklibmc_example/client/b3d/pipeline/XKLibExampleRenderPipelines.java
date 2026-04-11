package com.xkball.xklibmc_example.client.b3d.pipeline;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.xklibmc.client.b3d.pipeline.ExtendedRenderPipeline;
import com.xkball.xklibmc.utils.VanillaUtils;

public class XKLibExampleRenderPipelines {
    
    public static final ExtendedRenderPipeline WORLD_TERRAIN_PIP = ExtendedRenderPipeline.builder()
            .withLocation(VanillaUtils.modRL("world_terrain_pip"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
            .withVertexShader(VanillaUtils.modRL("core/world_terrain_pip"))
            .withFragmentShader(VanillaUtils.modRL("core/world_terrain_pip"))
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withSSBO("ABlock")
//            .withSSBO("RenderCommand")
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(true)
            .buildExtended();
}

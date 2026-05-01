package com.xkball.xklibmc_example.client.b3d.pipeline;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.xklibmc.client.b3d.pipeline.ExtendedRenderPipeline;
import com.xkball.xklibmc.client.b3d.uniform.UpdatableUBO;
import com.xkball.xklibmc.utils.VanillaUtils;
import org.joml.Vector3f;

public class XKLibExampleRenderPipelines {
    
    public static final UpdatableUBO PHONE_LIGHT = new UpdatableUBO.UBOBuilder("PhongLight")
            .closeOnExit()
            .putVec3("lightPos", Vector3f::new)
            .putVec3("viewPos", Vector3f::new)
            .build();
    
    public static final ExtendedRenderPipeline WORLD_TERRAIN_PIP = ExtendedRenderPipeline.builder()
            .withLocation(VanillaUtils.modRL("world_terrain_pip"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.TRIANGLES)
            .withVertexShader(VanillaUtils.modRL("core/world_terrain_pip"))
            .withFragmentShader(VanillaUtils.modRL("core/world_terrain_pip"))
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("PhongLight", UniformType.UNIFORM_BUFFER)
            .bindUniform("PhongLight", PHONE_LIGHT)
            .withSSBO("ABlock")
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(true)
            .buildExtended();
    
    public static final ExtendedRenderPipeline WORLD_TERRAIN_PIP_FULL_MESH = ExtendedRenderPipeline.builder()
           .withLocation(VanillaUtils.modRL("world_terrain_pip_full_mesh"))
           .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.TRIANGLES)
           .withVertexShader(VanillaUtils.modRL("core/world_terrain_pip_full_mesh"))
           .withFragmentShader(VanillaUtils.modRL("core/world_terrain_pip_full_mesh"))
           .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
           .withUniform("Projection", UniformType.UNIFORM_BUFFER)
           .withUniform("PhongLight", UniformType.UNIFORM_BUFFER)
           .bindUniform("PhongLight", PHONE_LIGHT)
           .withDepthStencilState(DepthStencilState.DEFAULT)
           .withCull(true)
           .buildExtended();
    
    public static final ExtendedRenderPipeline WORLD_TERRAIN_PIP_LOD = ExtendedRenderPipeline.builder()
            .withLocation(VanillaUtils.modRL("world_terrain_pip_lod"))
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.TRIANGLES)
            .withVertexShader(VanillaUtils.modRL("core/world_terrain_pip_lod"))
            .withFragmentShader(VanillaUtils.modRL("core/world_terrain_pip_full_mesh"))
            .withSampler("colorTexture")
            .withSampler("heightTexture")
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("PhongLight", UniformType.UNIFORM_BUFFER)
            .bindUniform("PhongLight", PHONE_LIGHT)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(true)
            .buildExtended();

}

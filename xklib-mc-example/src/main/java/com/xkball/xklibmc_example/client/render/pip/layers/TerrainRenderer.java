package com.xkball.xklibmc_example.client.render.pip.layers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.xklibmc.api.client.mixin.IExtendedRenderPass;
import com.xkball.xklibmc.client.b3d.mesh.CachedMesh;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc_example.api.client.render.PictureInPictureRenderLayer;
import com.xkball.xklibmc_example.client.b3d.pipeline.XKLibExampleRenderPipelines;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;
import com.xkball.xklibmc_example.client.render.pip.WorldTerrainPipRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class TerrainRenderer implements PictureInPictureRenderLayer<WorldTerrainPipRenderer, WorldTerrainPipRenderer.WorldTerrainState> {
    
    private static final CachedMesh CUBE = new CachedMesh("cube", XKLibExampleRenderPipelines.WORLD_TERRAIN_PIP,TerrainRenderer::createCubeMesh,true).setCloseOnExit();
    
    @Override
    public String name() {
        return "terrain";
    }
    
    @Override
    public void render(WorldTerrainPipRenderer pip, WorldTerrainPipRenderer.WorldTerrainState renderState, PoseStack poseStack, GpuTextureView texture, GpuTextureView depth) {
        RenderSystem.getModelViewStack().pushMatrix();
        var modelView = RenderSystem.getModelViewStack().mul(poseStack.last().pose(), new Matrix4f());
        var frustum = new Frustum(modelView,WorldTerrainPipRenderer.projMatrix);
        var transformUBO = RenderSystem.getDynamicUniforms().writeTransform(modelView, new Vector4f(1,1,1,1), new Vector3f(), new Matrix4f());
        var pipeline = XKLibExampleRenderPipelines.WORLD_TERRAIN_PIP;
        try(var renderInfo = TerrainChunkManager.INSTANCE.generateRenderInfo(frustum,renderState.cameraOffset().add(renderState.cameraTarget()))){
            try (var renderpass = ClientUtils.getCommandEncoder().createRenderPass(() -> "world terrain pip rendering", texture, OptionalInt.empty(), depth, OptionalDouble.empty());){
                if(renderInfo.drawCount() != 0){
                    RenderSystem.bindDefaultUniforms(renderpass);
                    renderpass.setPipeline(pipeline);
                    renderpass.setUniform("DynamicTransforms", transformUBO);
                    renderpass.setVertexBuffer(0, CUBE.getVertexBuffer());
                    renderpass.setIndexBuffer(CUBE.getIndexBuffer(),CUBE.getIndexType());
                    IExtendedRenderPass.cast(renderpass).xklib$setSSBO("ABlock",TerrainChunkManager.INSTANCE.gpuBuffer.gpuBuffer.slice());
                    IExtendedRenderPass.cast(renderpass).xklib$multiDrawElementsIndirect(renderInfo.commandBuffer(), renderInfo.drawCount());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        RenderSystem.getModelViewStack().popMatrix();
    }
    
    private static void createCubeMesh(BufferBuilder builder){
        //down
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(-1);
        //up
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(-1);
        //north
        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1);
        //south
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1);
        //west
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(-1);
        //east
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 1.0f).setColor(-1);
        

    }
    
}

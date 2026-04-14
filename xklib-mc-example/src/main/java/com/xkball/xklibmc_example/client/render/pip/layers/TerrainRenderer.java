package com.xkball.xklibmc_example.client.render.pip.layers;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.xklibmc.api.client.mixin.IExtendedRenderPass;
import com.xkball.xklibmc.client.b3d.mesh.CachedMesh;
import com.xkball.xklibmc.client.b3d.postprocess.XKLibPostProcesses;
import com.xkball.xklibmc.client.b3d.uniform.XKLibUniforms;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc_example.api.client.render.PictureInPictureRenderLayer;
import com.xkball.xklibmc_example.client.b3d.pipeline.XKLibExampleRenderPipelines;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;
import com.xkball.xklibmc_example.client.render.pip.WorldTerrainPipRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GL46;

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
//        GL46.glClipControl(GL45.GL_LOWER_LEFT, GL45.GL_ZERO_TO_ONE);
//        var revZProj = renderState.calculateProjMatrix(true);
//        RenderSystem.setProjectionMatrix(WorldTerrainPipRenderer.proj.getBuffer(revZProj), ProjectionType.PERSPECTIVE);
        var modelView = RenderSystem.getModelViewStack().mul(poseStack.last().pose(), new Matrix4f());
        var frustum = new Frustum(modelView,WorldTerrainPipRenderer.projMatrix);
        var transformUBO = RenderSystem.getDynamicUniforms().writeTransform(modelView, new Vector4f(1,1,1,1), new Vector3f(), new Matrix4f());
        XKLibExampleRenderPipelines.PHONE_LIGHT.updateUnsafe(b ->
                b.putVec3(VanillaUtils.dirVec(45,renderState.yRot() + 2))
                 .putVec3(renderState.cameraPos()));
        try(var renderInfo = TerrainChunkManager.INSTANCE.gatherRenderInfo(frustum,renderState.cameraOffset().add(renderState.cameraTarget()))){
            if(!renderInfo.isEmpty()){
                try (var renderpass = ClientUtils.getCommandEncoder().createRenderPass(() -> "world terrain pip rendering", texture, OptionalInt.empty(), depth, OptionalDouble.empty())){
                    RenderSystem.bindDefaultUniforms(renderpass);
                    renderpass.setPipeline(XKLibExampleRenderPipelines.WORLD_TERRAIN_PIP);
                    renderpass.setUniform("DynamicTransforms", transformUBO);
                    renderpass.setVertexBuffer(0, CUBE.getVertexBuffer());
                    renderpass.setIndexBuffer(CUBE.getIndexBuffer(),CUBE.getIndexType());
                    IExtendedRenderPass.cast(renderpass).xklib$setSSBO("ABlock",renderInfo.blockBuffer().slice());
                    IExtendedRenderPass.cast(renderpass).xklib$multiDrawElementsIndirect(renderInfo.commandLod0(), renderInfo.drawCountLod0());
                    renderpass.setPipeline(XKLibExampleRenderPipelines.WORLD_TERRAIN_PIP_LOD1);
                    IExtendedRenderPass.cast(renderpass).xklib$multiDrawElementsIndirect(renderInfo.commandLod1(), renderInfo.drawCountLod1());
                }
            }
        }
        XKLibUniforms.INVERSE_PROJ_MAT.updateUnsafe(b -> {
            b.putMat4f(renderState.projMatrix().invert(new Matrix4f()));
            b.putMat4f(renderState.projMatrix());
        });
        XKLibPostProcesses.SSAO.apply(texture, depth);
//        GL46.glClipControl(GL45.GL_LOWER_LEFT, GL45.GL_NEGATIVE_ONE_TO_ONE);
//        RenderSystem.setProjectionMatrix(WorldTerrainPipRenderer.projBuffer, ProjectionType.PERSPECTIVE);
        RenderSystem.getModelViewStack().popMatrix();
    }
    
    private static void createCubeMesh(BufferBuilder builder){
        //down
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1).setNormal(0, -1, 0);
        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(-1).setNormal(0, -1, 0);
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1).setNormal(0, -1, 0);
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1).setNormal(0, -1, 0);
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1).setNormal(0, -1, 0);
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(-1).setNormal(0, -1, 0);
        //up
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1).setNormal(0, 1, 0);
        builder.addVertex(1.0f, 1.0f, 1.0f).setColor(-1).setNormal(0, 1, 0);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1).setNormal(0, 1, 0);
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1).setNormal(0, 1, 0);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1).setNormal(0, 1, 0);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(-1).setNormal(0, 1, 0);
        //north
        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(-1).setNormal(0, 0, -1);
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1).setNormal(0, 0, -1);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(-1).setNormal(0, 0, -1);
        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(-1).setNormal(0, 0, -1);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(-1).setNormal(0, 0, -1);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1).setNormal(0, 0, -1);
        //south
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(-1).setNormal(0, 0, 1);
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1).setNormal(0, 0, 1);
        builder.addVertex(1.0f, 1.0f, 1.0f).setColor(-1).setNormal(0, 0, 1);
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(-1).setNormal(0, 0, 1);
        builder.addVertex(1.0f, 1.0f, 1.0f).setColor(-1).setNormal(0, 0, 1);
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1).setNormal(0, 0, 1);
        //west
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1).setNormal(-1, 0, 0);
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(-1).setNormal(-1, 0, 0);
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1).setNormal(-1, 0, 0);
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1).setNormal(-1, 0, 0);
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1).setNormal(-1, 0, 0);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(-1).setNormal(-1, 0, 0);
        //east
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1).setNormal(1, 0, 0);
        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(-1).setNormal(1, 0, 0);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1).setNormal(1, 0, 0);
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1).setNormal(1, 0, 0);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1).setNormal(1, 0, 0);
        builder.addVertex(1.0f, 1.0f, 1.0f).setColor(-1).setNormal(1, 0, 0);
        

    }
    
}

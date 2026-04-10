package com.xkball.xklibmc.ui.pip;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.api.client.b3d.ISTD140Writer;
import com.xkball.xklibmc.api.client.mixin.IExtendedRenderPass;
import com.xkball.xklibmc.client.TextureSpriteAvgColorCache;
import com.xkball.xklibmc.client.b3d.mesh.CachedMesh;
import com.xkball.xklibmc.client.b3d.mesh.InstanceInfo;
import com.xkball.xklibmc.client.b3d.mesh.MeshBundle;
import com.xkball.xklibmc.client.b3d.mesh.MeshBundleWithRenderPipeline;
import com.xkball.xklibmc.client.terrain.TerrainChunkManager;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc.x3d.backend.b3d.pipeline.B3dRenderPipelines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

import java.util.OptionalDouble;
import java.util.OptionalInt;

@NonNullByDefault
public class WorldTerrainPipRenderer extends PictureInPictureRenderer<WorldTerrainPipRenderer.WorldTerrainState> {
    
    private static final CachedMesh CUBE = new CachedMesh("cube",B3dRenderPipelines.WORLD_TERRAIN_PIP,WorldTerrainPipRenderer::createCubeMesh,true).setCloseOnExit();
    private static final ProjectionMatrixBuffer proj = new ProjectionMatrixBuffer("world_terrain_pip_proj");
    private static final TerrainChunkManager terrainChunkManager = new TerrainChunkManager();
    
    public static void update(){
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        terrainChunkManager.clear();
        terrainChunkManager.submitUpdate(camera.blockPosition(),16);
    }
    
    public WorldTerrainPipRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }
    
    private static void createCubeMesh(BufferBuilder builder){
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1);

        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1);

        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(-1);

        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 1.0f).setColor(-1);

        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 1.0f).setColor(-1);
        builder.addVertex(1.0f, 1.0f, 0.0f).setColor(-1);
        builder.addVertex(0.0f, 1.0f, 0.0f).setColor(-1);

        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 0.0f, 0.0f).setColor(-1);
        builder.addVertex(1.0f, 0.0f, 1.0f).setColor(-1);
        builder.addVertex(0.0f, 0.0f, 1.0f).setColor(-1);
    }
    
    @Override
    public Class<WorldTerrainState> getRenderStateClass() {
        return WorldTerrainState.class;
    }
    
    @Override
    protected void renderToTexture(WorldTerrainState renderState, PoseStack poseStack_) {
        terrainChunkManager.runUpdateFor10ms(Minecraft.getInstance().level);
        var poseStack = new PoseStack();
        poseStack.pushPose();
        var aspect = ( renderState.x1-  renderState.x0) / ((float) renderState.y1 - (float) renderState.y0);
        var cp = renderState.cameraPos();
        var dir = renderState.dirVec();
        var projBuffer = proj.getBuffer(new Matrix4f().perspective((float) Math.toRadians(renderState.fov), aspect, 1, 8000)
                .lookAt(cp.x + dir.x, -cp.y + dir.y, cp.z + dir.z,
                        cp.x, -cp.y, cp.z,
                        0,1,0));
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(projBuffer, ProjectionType.PERSPECTIVE);
        ClientUtils.getCommandEncoder().clearColorTexture(RenderSystem.outputColorTextureOverride.texture(),0xff000000);
        var modelView = RenderSystem.getModelViewStack().mul(poseStack.last().pose(), new Matrix4f());
        var transformUBO = RenderSystem.getDynamicUniforms().writeTransform(modelView, new Vector4f(1,1,1,1), new Vector3f(), new Matrix4f());
        var pipeline = B3dRenderPipelines.WORLD_TERRAIN_PIP;
        try (var renderpass = ClientUtils.getCommandEncoder().createRenderPass(() -> "world terrain pip rendering",RenderSystem.outputColorTextureOverride, OptionalInt.empty(), RenderSystem.outputDepthTextureOverride, OptionalDouble.empty());
             var renderInfo = terrainChunkManager.generateRenderOffsetAndInstance(_ -> true,CUBE.getIndexCount())){
            RenderSystem.bindDefaultUniforms(renderpass);
            renderpass.setPipeline(pipeline);
            renderpass.setUniform("DynamicTransforms", transformUBO);
            renderpass.setVertexBuffer(0, CUBE.getVertexBuffer());
            renderpass.setIndexBuffer(CUBE.getIndexBuffer(),CUBE.getIndexType());
            IExtendedRenderPass.cast(renderpass).xklib$setSSBO("ABlock",terrainChunkManager.gpuBuffer.gpuBuffer.slice());
            IExtendedRenderPass.cast(renderpass).xklib$setSSBO("ChunkIndex",renderInfo.chunkIndexBuffer().slice());
            IExtendedRenderPass.cast(renderpass).xklib$multiDrawElementsIndirect(renderInfo.commandBuffer(), renderInfo.drawCount());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        RenderSystem.restoreProjectionMatrix();
        poseStack.popPose();
    }
    
    @Override
    protected String getTextureLabel() {
        return "world terrain";
    }
    
    public record WorldTerrainState(Vector3f cameraPos, float fov,float cameraLength, float xRot, float yRot, int x0, int x1, int y0, int y1, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {
        
        public Vector3f dirVec(){
            var x = (float) (Math.cos(Math.toRadians(xRot)) * Math.sin(Math.toRadians(yRot)));
            var y = (float) (Math.sin(Math.toRadians(xRot)));
            var z = (float) (Math.cos(Math.toRadians(xRot)) * Math.cos(Math.toRadians(yRot)));
            var result =  new Vector3f(x,y,z);
            result.normalize(cameraLength + 100);
            return result;
        }
    }
}

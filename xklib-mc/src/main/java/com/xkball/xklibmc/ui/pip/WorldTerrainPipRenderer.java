package com.xkball.xklibmc.ui.pip;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.api.client.mixin.IExtendedRenderPass;
import com.xkball.xklibmc.client.b3d.mesh.CachedMesh;
import com.xkball.xklibmc.client.b3d.uniform.XKLibUniforms;
import com.xkball.xklibmc.client.terrain.TerrainChunkManager;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc.x3d.backend.b3d.pipeline.B3dRenderPipelines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@NonNullByDefault
public class WorldTerrainPipRenderer extends PictureInPictureRenderer<WorldTerrainPipRenderer.WorldTerrainState> {
    
    private static final CachedMesh CUBE = new CachedMesh("cube",B3dRenderPipelines.WORLD_TERRAIN_PIP,WorldTerrainPipRenderer::createCubeMesh,true).setCloseOnExit();
    private static final ProjectionMatrixBuffer proj = new ProjectionMatrixBuffer("world_terrain_pip_proj");
    public static final TerrainChunkManager terrainChunkManager = new TerrainChunkManager();
    
    public static void update(){
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        terrainChunkManager.submitUpdate(camera.blockPosition(),32);
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
        var projMat = new Matrix4f().perspective((float) Math.toRadians(renderState.fov), aspect, 1, 8000)
                .lookAt(cp.x + dir.x, -cp.y + dir.y, cp.z + dir.z,
                        cp.x, -cp.y, cp.z,
                        0,1,0);
        var projBuffer = proj.getBuffer(projMat);
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(projBuffer, ProjectionType.PERSPECTIVE);
        ClientUtils.getCommandEncoder().clearColorTexture(RenderSystem.outputColorTextureOverride.texture(),0xff000000);
        poseStack.translate(-renderState.centerPos.getX(), 0, -renderState.centerPos.getZ());
        RenderSystem.getModelViewStack().pushMatrix();
        
        var modelView = RenderSystem.getModelViewStack().mul(poseStack.last().pose(), new Matrix4f());
        var frustum = new Frustum(modelView,projMat);
        var transformUBO = RenderSystem.getDynamicUniforms().writeTransform(modelView, new Vector4f(1,1,1,1), new Vector3f(), new Matrix4f());
        var pipeline = B3dRenderPipelines.WORLD_TERRAIN_PIP;
        try (var renderpass = ClientUtils.getCommandEncoder().createRenderPass(() -> "world terrain pip rendering",RenderSystem.outputColorTextureOverride, OptionalInt.empty(), RenderSystem.outputDepthTextureOverride, OptionalDouble.empty());
             var renderInfo = terrainChunkManager.generateRenderInfo(c -> frustum.isVisible(new AABB(c.getMinBlockX(),-64,c.getMinBlockZ(),c.getMaxBlockX(),384,c.getMaxBlockZ())),CUBE.getIndexCount())
        ){
            if(renderInfo.drawCount() != 0){
                RenderSystem.bindDefaultUniforms(renderpass);
                renderpass.setPipeline(pipeline);
                renderpass.setUniform("DynamicTransforms", transformUBO);
                renderpass.setVertexBuffer(0, CUBE.getVertexBuffer());
                renderpass.setIndexBuffer(CUBE.getIndexBuffer(),CUBE.getIndexType());
                IExtendedRenderPass.cast(renderpass).xklib$setSSBO("ABlock",terrainChunkManager.gpuBuffer.gpuBuffer.slice());
                IExtendedRenderPass.cast(renderpass).xklib$setSSBO("ChunkIndex",renderInfo.chunkIndexBuffer().slice());
//                for(var entry : terrainChunkManager.generateRenderOffsetAndInstance(c -> frustum.isVisible(new AABB(c.getMinBlockX(),-64,c.getMinBlockZ(),c.getMaxBlockX(),384,c.getMaxBlockZ()))).entrySet()){
//                    IExtendedRenderPass.cast(renderpass).xklib$setSSBO("ABlock",terrainChunkManager.gpuBuffer.gpuBuffer.slice(entry.getKey(), (long) TerrainChunkManager.BLOCK_SIZE * entry.getValue()));
//                    renderpass.drawIndexed(0,0, CUBE.getIndexCount(), entry.getValue());
//                }
                IExtendedRenderPass.cast(renderpass).xklib$multiDrawElementsIndirect(renderInfo.commandBuffer(), renderInfo.drawCount());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        RenderSystem.getModelViewStack().popMatrix();
//        ClientUtils.renderAxis(this.bufferSource,poseStack,10000);
        this.renderGird(renderState, poseStack);
        this.bufferSource.endBatch();
        RenderSystem.restoreProjectionMatrix();
        poseStack.popPose();
    }
    
    @Override
    protected String getTextureLabel() {
        return "world terrain";
    }
    
    private void renderGird(WorldTerrainState state, PoseStack poseStack){
        poseStack.pushPose();
        var buffer = bufferSource.getBuffer(RenderTypes.LINES);
        var texture = Objects.requireNonNull(RenderSystem.outputColorTextureOverride);
        XKLibUniforms.SCREEN_SIZE.startOverride(
                    b -> b.putVec2(texture.getWidth(0), texture.getHeight(0)));
        var step = 256;
        poseStack.translate(state.cameraPos.x - (state.cameraPos.x%step), 0, state.cameraPos.z - (state.cameraPos.z%step));
        
        var matrix = poseStack.last();
        var y = (float) state.centerPos.getY();
        var max = 8000;
        var min = -max;
//        var xSmallColor = 0xFF3A6FF0;
//        var zSmallColor = 0xFF2F63E6;
        var xColor = 0xFF173A8F;
        var zColor = 0xFF12337F;
        var normalX = 0;
        var normalY = 1;
        var normalZ = 0;
        for (int x = min; x <= max; x += step) {
            buffer.addVertex(matrix, x, y, min).setNormal(matrix, normalX, normalY, normalZ).setLineWidth(3f).setColor(xColor);
            buffer.addVertex(matrix, x, y, max).setNormal(matrix, normalX, normalY, normalZ).setLineWidth(3f).setColor(xColor);
        }

        for (int z = min; z <= max; z += step) {
            buffer.addVertex(matrix, min, y, z).setNormal(matrix, normalX, normalY, normalZ).setLineWidth(3f).setColor(zColor);
            buffer.addVertex(matrix, max, y, z).setNormal(matrix, normalX, normalY, normalZ).setLineWidth(3f).setColor(zColor);
        }
        this.bufferSource.endLastBatch();
        poseStack.popPose();
        XKLibUniforms.SCREEN_SIZE.endOverride();
    }
    
    public record WorldTerrainState(Vector3f cameraPos, BlockPos centerPos, float fov, float cameraLength, float xRot, float yRot, int x0, int x1, int y0, int y1, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {
        
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

package com.xkball.xklibmc_example.client.render.pip;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc_example.api.client.render.PictureInPictureRenderLayer;
import com.xkball.xklibmc_example.client.render.pip.layers.GridRenderer;
import com.xkball.xklibmc_example.client.render.pip.layers.PlayerOnMapRenderer;
import com.xkball.xklibmc_example.client.render.pip.layers.TerrainRenderer;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@NonNullByDefault
public class WorldTerrainPipRenderer extends PictureInPictureRenderer<WorldTerrainPipRenderer.WorldTerrainState> {
    
    public static final ProjectionMatrixBuffer proj = new ProjectionMatrixBuffer("world_terrain_pip_proj");
    public static Matrix4f projMatrix = new Matrix4f();
    @SuppressWarnings("NotNullFieldNotInitialized")
    public static GpuBufferSlice projBuffer;
    
    private static final Map<String, PictureInPictureRenderLayer<WorldTerrainPipRenderer,WorldTerrainState>> renderLayers = new LinkedHashMap<>();
    
    static {
        regRenderLayers(new TerrainRenderer());
        regRenderLayers(new GridRenderer());
        regRenderLayers(new PlayerOnMapRenderer());
    }
    
    public static void update(){
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        TerrainChunkManager.INSTANCE.submitUpdate(camera.blockPosition(),32);
    }
    
    public static void regRenderLayers(PictureInPictureRenderLayer<WorldTerrainPipRenderer,WorldTerrainState> renderLayer) {
        renderLayers.put(renderLayer.name(), renderLayer);
    }
    
    public WorldTerrainPipRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public Class<WorldTerrainState> getRenderStateClass() {
        return WorldTerrainState.class;
    }
    
    @Override
    protected void renderToTexture(WorldTerrainState renderState, PoseStack poseStack_) {
        var poseStack = new PoseStack();
        poseStack.pushPose();
        var aspect = ( renderState.x1-  renderState.x0) / ((float) renderState.y1 - (float) renderState.y0);
        var cp = renderState.cameraTarget();
        var cameraOffset = renderState.cameraOffset();
        projMatrix = new Matrix4f().perspective((float) Math.toRadians(renderState.fov), aspect, 1, 8000)
                .lookAt(cp.x + cameraOffset.x, cp.y + cameraOffset.y, cp.z + cameraOffset.z,
                        cp.x, cp.y, cp.z,
                        0,1,0);
        projBuffer = proj.getBuffer(projMatrix);
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(projBuffer, ProjectionType.PERSPECTIVE);
        ClientUtils.getCommandEncoder().clearColorTexture(RenderSystem.outputColorTextureOverride.texture(),0xff000000);
//        poseStack.translate(-renderState.centerPos().getX(), 0, -renderState.centerPos().getZ());
        
        for(var layer : renderState.enabledLayers){
            var renderer = renderLayers.get(layer);
            if(renderer != null){
                renderer.render(this, renderState, poseStack, RenderSystem.outputColorTextureOverride, RenderSystem.outputDepthTextureOverride);
            }
        }
        ClientUtils.renderAxis(this.bufferSource,poseStack,1000);
        this.bufferSource.endBatch();
        RenderSystem.restoreProjectionMatrix();
        poseStack.popPose();
    }
    
    @Override
    protected String getTextureLabel() {
        return "world terrain";
    }
    
    public MultiBufferSource.BufferSource getBufferSource() {
        return bufferSource;
    }
    
    public record WorldTerrainState(List<String> enabledLayers,
                                    Vector3f cameraTarget,
                                    BlockPos centerPos,
                                    float fov,
                                    float cameraLength,
                                    float xRot, float yRot,
                                    int x0, int x1, int y0, int y1,
                                    float scale,
                                    @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {
        
        public Vector3f dirVec(){
            var x = (float) (Math.cos(Math.toRadians(xRot)) * Math.sin(Math.toRadians(yRot)));
            var y = (float) (Math.sin(Math.toRadians(xRot)));
            var z = (float) (Math.cos(Math.toRadians(xRot)) * Math.cos(Math.toRadians(yRot)));
            return new Vector3f(x,y,z).normalize();
        }
        
        public Vector3f cameraOffset(){
            return dirVec().normalize(cameraLength + 100);
        }
    }
}

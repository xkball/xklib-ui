package com.xkball.xklibmc_example.client.render.pip;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.xklib.api.gui.widget.IGuiWidget;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc_example.api.client.render.PictureInPictureRenderLayer;
import com.xkball.xklibmc_example.client.render.pip.layers.CameraTargetRenderer;
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
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
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
    public CameraRenderState cameraRenderState = new CameraRenderState();
    
    private static final Map<String, PictureInPictureRenderLayer<WorldTerrainPipRenderer,WorldTerrainState>> renderLayers = new LinkedHashMap<>();
    
    static {
        regRenderLayers(new TerrainRenderer());
        regRenderLayers(new GridRenderer());
        regRenderLayers(new PlayerOnMapRenderer());
        regRenderLayers(new CameraTargetRenderer());
    }
    
    public static void update(){
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        var viewDistance = Minecraft.getInstance().options.renderDistance().get();
        TerrainChunkManager.INSTANCE.submitUpdate(camera.blockPosition(),viewDistance - 1, false);
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
        var cameraPos = renderState.cameraPos();
        projMatrix = renderState.projMatrix();
        projBuffer = proj.getBuffer(projMatrix);
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(projBuffer, ProjectionType.PERSPECTIVE);
        ClientUtils.getCommandEncoder().clearColorTexture(RenderSystem.outputColorTextureOverride.texture(),0xff000000);
//        poseStack.translate(-renderState.centerPos().getX(), 0, -renderState.centerPos().getZ());
        this.cameraRenderState.yRot = renderState.yRot;
        this.cameraRenderState.xRot = renderState.xRot;
        this.cameraRenderState.pos = new Vec3(cameraPos);
        this.cameraRenderState.blockPos = new BlockPos((int) cameraPos.x, (int) cameraPos.y, (int) cameraPos.z);
        this.cameraRenderState.projectionMatrix = projMatrix;
        for(var layer : renderState.enabledLayers){
            var renderer = renderLayers.get(layer);
            if(renderer != null){
                renderer.render(this, renderState, poseStack, RenderSystem.outputColorTextureOverride, RenderSystem.outputDepthTextureOverride);
            }
        }
//        ClientUtils.renderAxis(this.bufferSource,poseStack,1000);
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
    
    public static final class WorldTerrainState implements PictureInPictureRenderState {
        private final List<String> enabledLayers;
        private final Vector3f cameraTarget;
        private final BlockPos centerPos;
        private final float fov;
        private final float cameraLength;
        private final float xRot;
        private final float yRot;
        private final int x0;
        private final int x1;
        private final int y0;
        private final int y1;
        private final float scale;
        private final boolean cullNear;
        private final int lodDistance;
        private final @Nullable ScreenRectangle scissorArea;
        private final @Nullable ScreenRectangle bounds;
        private final Matrix4f projMatrix;
        
        public WorldTerrainState(List<String> enabledLayers,
                                 Vector3f cameraTarget,
                                 BlockPos centerPos,
                                 float fov,
                                 float cameraLength,
                                 float xRot,
                                 float yRot,
                                 int x0,
                                 int x1,
                                 int y0,
                                 int y1,
                                 float scale,
                                 boolean cullNear,
                                 int lodDistance,
                                 @Nullable ScreenRectangle scissorArea,
                                 @Nullable ScreenRectangle bounds) {
            this.enabledLayers = enabledLayers;
            this.cameraTarget = cameraTarget;
            this.centerPos = centerPos;
            this.fov = fov;
            this.cameraLength = cameraLength;
            this.xRot = xRot;
            this.yRot = yRot;
            this.x0 = x0;
            this.x1 = x1;
            this.y0 = y0;
            this.y1 = y1;
            this.scale = scale;
            this.cullNear = cullNear;
            this.lodDistance = lodDistance;
            this.scissorArea = scissorArea;
            this.bounds = bounds;
            this.projMatrix = this.calculateProjMatrix(false);
        }
        
        public Vector3f dirVec() {
            return VanillaUtils.dirVec(xRot,yRot);
        }
        
        public Vector3f cameraOffset() {
            return dirVec().normalize(cameraLength + 100);
        }
        
        public Vector3f cameraPos() {
            return cameraOffset().add(cameraTarget);
        }
        
        public Matrix4f calculateProjMatrix(boolean revZ) {
            var aspect = (x1 - x0) / ((float) y1 - (float) y0);
            var cameraPos = cameraPos();
            return new Matrix4f().perspective((float) Math.toRadians(fov), aspect, Math.max(1,cameraPos.y-1000), Math.max(cameraLength * 2, 8000), revZ)
                    .lookAt(cameraPos.x, cameraPos.y, cameraPos.z,
                            cameraTarget.x, cameraTarget.y, cameraTarget.z,
                            0, 1, 0);
        }
        
        public Vector2f projWorld2Screen(IGuiWidget widget, Vector3f worldPos) {
            var p = this.projMatrix.transform(new Vector4f(worldPos,1f));
            var x = p.x / p.w;
            var y = p.y / p.w;
            return new Vector2f((1 + x) / 2 * widget.getWidth() + widget.getX(), (1 - y) / 2 * widget.getHeight() + widget.getY());
        }
        
        public List<String> enabledLayers() {
            return enabledLayers;
        }
        
        public Vector3f cameraTarget() {
            return cameraTarget;
        }
        
        public BlockPos centerPos() {
            return centerPos;
        }
        
        public float fov() {
            return fov;
        }
        
        public float cameraLength() {
            return cameraLength;
        }
        
        public float xRot() {
            return xRot;
        }
        
        public float yRot() {
            return yRot;
        }
        
        @Override
        public int x0() {
            return x0;
        }
        
        @Override
        public int x1() {
            return x1;
        }
        
        @Override
        public int y0() {
            return y0;
        }
        
        @Override
        public int y1() {
            return y1;
        }
        
        @Override
        public float scale() {
            return scale;
        }
        
        public boolean cullNear() {
            return this.cullNear;
        }
        
        public int lodDistance() {
            return lodDistance;
        }
        
        @Override
        public @Nullable ScreenRectangle scissorArea() {
            return scissorArea;
        }
        
        @Override
        public @Nullable ScreenRectangle bounds() {
            return bounds;
        }
        
        public Matrix4f projMatrix() {
            return projMatrix;
        }
        
    }
}

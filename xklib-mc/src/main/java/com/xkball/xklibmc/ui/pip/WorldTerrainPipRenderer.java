package com.xkball.xklibmc.ui.pip;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.xklibmc.annotation.NonNullByDefault;
import com.xkball.xklibmc.api.client.b3d.ISTD140Writer;
import com.xkball.xklibmc.client.b3d.mesh.InstanceInfo;
import com.xkball.xklibmc.client.b3d.mesh.MeshBundle;
import com.xkball.xklibmc.client.b3d.mesh.MeshBundleWithRenderPipeline;
import com.xkball.xklibmc.utils.ClientUtils;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc.x3d.backend.b3d.pipeline.B3dRenderPipelines;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NonNullByDefault
public class WorldTerrainPipRenderer extends PictureInPictureRenderer<WorldTerrainPipRenderer.WorldTerrainState> {
    
    @SuppressWarnings("resource")
    private static final MeshBundle<RenderPipeline> CUBE = new MeshBundleWithRenderPipeline("cube", B3dRenderPipelines.WORLD_TERRAIN_PIP).setCloseOnExit();
    private static final ProjectionMatrixBuffer proj = new ProjectionMatrixBuffer("world_terrain_pip_proj");
    private static final TextureSpriteAvgColorCache cache = new TextureSpriteAvgColorCache();
    public static void update(ClientLevel level){
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        var list = compile(level,camera,512);
        var buffer = ISTD140Writer.batchBuildStd140Block(list);
        var instanceInfo = new InstanceInfo(list.size(),"ABlock",buffer.slice());
        CUBE.clear();
        var builder = ClientUtils.beginWithRenderPipeline(B3dRenderPipelines.WORLD_TERRAIN_PIP);
        createCubeMesh(builder);
        CUBE.appendImmediately(builder.buildOrThrow(),_ -> {},instanceInfo);
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
        CUBE.render(poseStack, RenderSystem.outputColorTextureOverride, RenderSystem.outputDepthTextureOverride);
        RenderSystem.restoreProjectionMatrix();
        poseStack.popPose();
    }
    
    @Override
    protected String getTextureLabel() {
        return "world terrain";
    }
    
    public record ABlock(BlockPos pos, int color) implements ISTD140Writer {
        
        @Override
        public void calculateSize(Std140SizeCalculator calculator) {
            calculator.putVec3().putInt();
        }
        
        @Override
        public void writeToBuffer(Std140Builder builder) {
            builder.putVec3(pos.getCenter().toVector3f());
            builder.putInt(color);
        }
    }
    
    public static List<ABlock> compile(ClientLevel level, Camera camera, int range){
        var directions = Direction.values();
        var mc = Minecraft.getInstance();
        var modelManager = mc.getModelManager().getBlockStateModelSet();
        var pos = camera.blockPosition().mutable();
        var x =  pos.getX();
        var z =  pos.getZ();
        var result = new ArrayList<ABlock>();
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                var px = x + dx;
                var pz = z + dz;
                var hMax = level.getHeight(Heightmap.Types.MOTION_BLOCKING,px,pz);
                var hMin = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px,pz) - 1;
                var h1 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px+1,pz);
                if(h1 > level.getMinY()) hMin = Math.min(hMin,h1);
                var h2 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px-1,pz);
                if(h2 > level.getMinY()) hMin = Math.min(hMin,h2);
                var h3 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px,pz+1);
                if(h3 > level.getMinY()) hMin = Math.min(hMin,h3);
                var h4 = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,px,pz-1);
                if(h4 > level.getMinY()) hMin = Math.min(hMin,h4);
                hMin = Math.clamp(hMin, level.getMinY(), hMax);
                for (int y = hMin; y < hMax; y++) {
                    pos.set(px, y, pz);
                    var bs = level.getBlockState(pos);
                    if (bs.isAir()) {
                        continue;
                    }
                    var allBlock = true;
                    for(var dir : directions){
                        var b = pos.move(dir);
                        if(!level.getBlockState(b).isSolidRender()){
                            allBlock = false;
                            break;
                        }
                        pos.move(dir,-1);
                    }
                    if(allBlock){
                        continue;
                    }
                    var model = modelManager.get(bs);
                    var sprite = model.particleMaterial(level, pos, bs).sprite();
                    var color = cache.getAvgColor(sprite);
                    color = VanillaUtils.mulColor(color, getBlockColor(level, pos, bs));
                    result.add(new ABlock(new BlockPos(dx,y,dz), color));
                }
            }
        }
        return result;
    }
    
    private static int getBlockColor(ClientLevel level, BlockPos pos, BlockState state){
        var mc = Minecraft.getInstance();
        var tintSource = mc.getBlockColors().getTintSources(state);
        if (tintSource.isEmpty()) return -1;
        var color = -1;
        for(var source : tintSource){
            color = VanillaUtils.mulColor(color,source.colorInWorld(state, level, pos));
        }
        return color;
    }
    
    private static class TextureSpriteAvgColorCache{
        
        public final Map<Identifier, Integer> colorCache = new HashMap<>();
        
        public int getAvgColor(TextureAtlasSprite sprite){
            var key = sprite.contents().name();
            var cached = colorCache.get(key);
            if (cached != null) {
                return cached;
            }
            var content = sprite.contents().getOriginalImage();
            long rSum = 0;
            long gSum = 0;
            long bSum = 0;
            long aSum = 0;
            long count = 0;
            for (int abgr : content.getPixelsABGR()) {
                int a = (abgr >>> 24) & 255;
                if (a == 0) {
                    continue;
                }
                int b = (abgr >>> 16) & 255;
                int g = (abgr >>> 8) & 255;
                int r = abgr & 255;
                aSum += a;
                rSum += r;
                gSum += g;
                bSum += b;
                count++;
            }
            int argb;
            if (count == 0) {
                argb = 0;
            } else {
                int a = (int) (aSum / count);
                int r = (int) (rSum / count);
                int g = (int) (gSum / count);
                int b = (int) (bSum / count);
                argb = (a << 24) | (r << 16) | (g << 8) | b;
            }
            colorCache.put(key, argb);
            return argb;
        }
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

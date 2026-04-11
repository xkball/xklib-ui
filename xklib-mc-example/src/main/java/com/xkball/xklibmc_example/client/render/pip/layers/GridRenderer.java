package com.xkball.xklibmc_example.client.render.pip.layers;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.xklibmc.client.b3d.uniform.XKLibUniforms;
import com.xkball.xklibmc_example.api.client.render.PictureInPictureRenderLayer;
import com.xkball.xklibmc_example.client.render.pip.WorldTerrainPipRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class GridRenderer implements PictureInPictureRenderLayer<WorldTerrainPipRenderer, WorldTerrainPipRenderer.WorldTerrainState> {
    
    @Override
    public String name() {
        return "grid";
    }
    
    @Override
    public void render(WorldTerrainPipRenderer pip, WorldTerrainPipRenderer.WorldTerrainState renderState, PoseStack poseStack, GpuTextureView texture, GpuTextureView depth) {
        poseStack.pushPose();
        var buffer = pip.getBufferSource().getBuffer(RenderTypes.LINES);
        XKLibUniforms.SCREEN_SIZE.startOverride(
                b -> b.putVec2(texture.getWidth(0), texture.getHeight(0)));
        var step = 256;
        poseStack.translate(renderState.cameraTarget().x - (renderState.cameraTarget().x%step), 0, renderState.cameraTarget().z - (renderState.cameraTarget().z%step));
        
        var matrix = poseStack.last();
        var y = (float) renderState.centerPos().getY();
        var max = 8000;
        var min = -max;
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
        pip.getBufferSource().endLastBatch();
        poseStack.popPose();
        XKLibUniforms.SCREEN_SIZE.endOverride();
    }
}

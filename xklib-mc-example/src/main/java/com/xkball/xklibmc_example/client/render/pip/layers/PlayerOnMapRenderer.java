package com.xkball.xklibmc_example.client.render.pip.layers;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.xklibmc_example.api.client.render.PictureInPictureRenderLayer;
import com.xkball.xklibmc_example.client.render.pip.WorldTerrainPipRenderer;
import net.minecraft.client.Minecraft;

public class PlayerOnMapRenderer implements PictureInPictureRenderLayer<WorldTerrainPipRenderer, WorldTerrainPipRenderer.WorldTerrainState> {
    
    @Override
    public String name() {
        return "player";
    }
    
    @Override
    public void render(WorldTerrainPipRenderer pip, WorldTerrainPipRenderer.WorldTerrainState renderState, PoseStack poseStack, GpuTextureView texture, GpuTextureView depth) {
        var featureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        var entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        
        featureRenderDispatcher.renderAllFeatures();
    }
}

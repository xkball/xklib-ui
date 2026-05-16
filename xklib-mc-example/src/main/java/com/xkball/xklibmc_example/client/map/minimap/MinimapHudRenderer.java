package com.xkball.xklibmc_example.client.map.minimap;

import com.xkball.xklibmc_example.client.map.WorldMapExtensionServiceImpl;
import com.xkball.xklibmc_example.client.render.pip.WorldTerrainPipRenderer;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.core.BlockPos;
import org.joml.Vector3f;

import java.util.ArrayList;

public final class MinimapHudRenderer {

    private static final int SIZE = 128;
    private static final int MARGIN = 8;
    private static final WorldMapExtensionServiceImpl HUD_SERVICE = new WorldMapExtensionServiceImpl("");

    private MinimapHudRenderer() {
    }

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        var mc = Minecraft.getInstance();
        if(mc.level == null || mc.player == null || mc.options.hideGui || mc.screen != null) return;
        var minimap = MinimapExtension.INSTANCE;
        if(minimap == null) return;
        var service = HUD_SERVICE.scope(MinimapExtension.EXTENSION_ID);
        minimap.renderRangeVariable().set(service.getIntState(MinimapExtension.KEY_RENDER_RANGE, minimap.renderRange()));
        minimap.highDetailRangeVariable().set(service.getIntState(MinimapExtension.KEY_HIGH_DETAIL_RANGE, minimap.highDetailRange()));
        minimap.rotateWithPlayerVariable().set(service.getBooleanState(MinimapExtension.KEY_ROTATE_WITH_PLAYER, minimap.rotateWithPlayer()));

        var window = mc.getWindow();
        var x0 = window.getGuiScaledWidth() - SIZE - MARGIN;
        var y0 = MARGIN;
        var x1 = x0 + SIZE;
        var y1 = y0 + SIZE;
        var player = mc.player;
        var blockPos = player.blockPosition();
        var target = new Vector3f((float) player.getX(), blockPos.getY(), (float) player.getZ());
        var storage = TerrainChunkManager.INSTANCE.getCurrentLevelChunkStorage();
        if(storage != null) {
            var h = storage.getHeight((int) target.x, (int) target.z);
            if(mc.level != null && h != mc.level.getMinY()) {
                target.y = h;
            }
        }
        var layers = new ArrayList<String>();
        if(HUD_SERVICE.getBooleanState("terrain", true)) layers.add("terrain");
        if(HUD_SERVICE.getBooleanState("grid", true)) layers.add("grid");
        if(HUD_SERVICE.getBooleanState("player", true)) layers.add("player");
        if(HUD_SERVICE.getBooleanState("camera_target", false)) layers.add("cameraTarget");
        layers.addAll(TerrainChunkManager.INSTANCE.worldMapExtensionRegistry.enabledLayers(HUD_SERVICE));
        var yRot = minimap.rotateWithPlayer() ? player.getYRot() : 0.0f;
        var state = new WorldTerrainPipRenderer.WorldTerrainState(
                layers,
                target,
                new BlockPos(blockPos.getX(), mc.level.getMinY(), blockPos.getZ()),
                60,
                0,
                89.0f,
                yRot,
                x0,
                x1,
                y0,
                y1,
                1.0f,
                HUD_SERVICE.getBooleanState("depress_sphere", false),
                HUD_SERVICE.getIntState("lod_distance", 512),
                true,
                minimap.renderRange(),
                minimap.highDetailRange(),
                null,
                new ScreenRectangle(x0, y0, SIZE, SIZE)
        );
        graphics.submitPictureInPictureRenderState(state);
        graphics.fill(x0 - 1, y0 - 1, x1 + 1, y0, 0xCCAAAAAA);
        graphics.fill(x0 - 1, y1, x1 + 1, y1 + 1, 0xCCAAAAAA);
        graphics.fill(x0 - 1, y0, x0, y1, 0xCCAAAAAA);
        graphics.fill(x1, y0, x1 + 1, y1, 0xCCAAAAAA);
    }
}

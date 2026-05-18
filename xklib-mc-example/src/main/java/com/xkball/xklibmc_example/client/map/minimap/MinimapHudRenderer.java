package com.xkball.xklibmc_example.client.map.minimap;

import com.xkball.xklib.XKLib;
import com.xkball.xklibmc.x3d.backend.b3d.B3dGuiGraphics;
import com.xkball.xklibmc.x3d.backend.b3d.B3dRenderContext;
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

        var window = mc.getWindow();
        var x0 = window.getGuiScaledWidth() - SIZE - MARGIN;
        var y0 = MARGIN;
        var x1 = x0 + SIZE;
        var y1 = y0 + SIZE;
        var player = mc.player;
        var blockPos = player.blockPosition();
        var target = new Vector3f((float) player.getX(), (float) player.getY(), (float) player.getZ());
        var layers = new ArrayList<String>();
        if(HUD_SERVICE.getBooleanState("terrain", true)) layers.add("terrain");
        if(HUD_SERVICE.getBooleanState("grid", true)) layers.add("grid");
        if(HUD_SERVICE.getBooleanState("player", true)) layers.add("player");
        if(HUD_SERVICE.getBooleanState("camera_target", false)) layers.add("cameraTarget");
        layers.addAll(TerrainChunkManager.INSTANCE.worldMapExtensionRegistry.enabledLayers(HUD_SERVICE));
        var yRot = minimap.rotateWithPlayer() ? MinimapPlayerMarker.mapYawForPlayerUp(player.getYRot()) : 0.0f;
        var state = new WorldTerrainPipRenderer.WorldTerrainState(
                layers,
                target,
                blockPos,
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
        MinimapPlayerMarker.render(guiGraphics(graphics), x0, y0, x1, y1, player.getYRot(), minimap.rotateWithPlayer());
    }

    private static B3dGuiGraphics guiGraphics(GuiGraphicsExtractor graphics) {
        if(XKLib.RENDER_CONTEXT.get() == null) {
            XKLib.RENDER_CONTEXT.set(new B3dRenderContext());
        }
        var guiGraphics = (B3dGuiGraphics) XKLib.RENDER_CONTEXT.get().getGUIGraphics();
        guiGraphics.setInner(graphics);
        return guiGraphics;
    }
}

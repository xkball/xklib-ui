package com.xkball.xklibmc_example.client.map.minimap;

import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.layout.IntLayoutVariable;
import com.xkball.xklibmc_example.api.client.map.WorldMapExtensionService;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;
import com.xkball.xklibmc_example.ui.widget.WorldTerrainWidgetInner;
import net.minecraft.client.Minecraft;

public final class MinimapWidgets {

    private MinimapWidgets() {
    }

    public static WorldTerrainWidgetInner createMinimapWidget(WorldMapExtensionService service, MinimapExtension minimap, boolean interactive) {
        var terrain = new BooleanLayoutVariable(service.getBooleanState("terrain", true));
        var grid = new BooleanLayoutVariable(service.getBooleanState("grid", true));
        var player = new BooleanLayoutVariable(service.getBooleanState("player", true));
        var debug = new BooleanLayoutVariable(false);
        var cameraTarget = new BooleanLayoutVariable(service.getBooleanState("camera_target", false));
        var depressSphere = new BooleanLayoutVariable(service.getBooleanState("depress_sphere", false));
        var yMode = new IntLayoutVariable(service.getIntState("y_mode", 1));
        var level = Minecraft.getInstance().level;
        var fixY = new IntLayoutVariable(level == null ? 64 : service.getIntState("fix_y", level.getSeaLevel()));
        var lodDistance = new IntLayoutVariable(service.getIntState("lod_distance", 512));
        var viewDistance = new IntLayoutVariable(service.getIntState("view_distance", TerrainChunkManager.INSTANCE.viewDistance));
        var widget = new WorldTerrainWidgetInner(
                terrain,
                grid,
                player,
                cameraTarget,
                depressSphere,
                debug,
                yMode,
                fixY,
                lodDistance,
                viewDistance,
                WorldTerrainWidgetInner.MapMode.MINIMAP,
                minimap.renderRangeVariable(),
                minimap.highDetailRangeVariable(),
                minimap.rotateWithPlayerVariable()
        );
        widget.setExtensionService(service);
        return widget;
    }
}

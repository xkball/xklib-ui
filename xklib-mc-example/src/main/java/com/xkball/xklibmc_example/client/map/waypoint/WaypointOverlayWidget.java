package com.xkball.xklibmc_example.client.map.waypoint;

import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.render.IGUIGraphics;
import com.xkball.xklib.ui.widget.container.AbsoluteContainer;
import com.xkball.xklibmc_example.api.client.map.WorldMapExtensionService;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class WaypointOverlayWidget extends AbsoluteContainer {

    private final WorldMapExtensionService service;
    private final BiConsumer<Waypoint, Boolean> openHandler;

    public WaypointOverlayWidget(WorldMapExtensionService service, BooleanLayoutVariable visible, Supplier<WaypointStorage> storage, Supplier<@Nullable Waypoint> temporary, BiConsumer<Waypoint, Boolean> openHandler) {
        this.service = service;
        this.openHandler = openHandler;
        this.autoReorder = false;
        if (!visible.get()) {
            return;
        }
        for (var waypoint : storage.get().waypoints()) {
            if (!waypoint.hidden()) {
                this.addWaypointIcon(waypoint, false);
            }
        }
        var temporaryWaypoint = temporary.get();
        if (temporaryWaypoint != null) {
            this.addWaypointIcon(temporaryWaypoint, true);
        }
    }

    @Override
    public void doRender(IGUIGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.updatePositions();
        super.doRender(graphics, mouseX, mouseY, partialTick);
    }

    public void updatePositions() {
        for (var child : this.children) {
            if (child instanceof WaypointIconWidget icon) {
                this.updateIconPosition(icon);
            }
        }
    }

    private void addWaypointIcon(Waypoint waypoint, boolean temporary) {
        var icon = new WaypointIconWidget(waypoint, temporary, () -> this.openHandler.accept(waypoint, temporary));
        this.addChild(icon);
    }

    private void updateIconPosition(WaypointIconWidget icon) {
        var pos = icon.waypoint().pos();
        var screen = this.service.projWorld2Screen(new Vector3f(pos.getX(), pos.getY(), pos.getZ()));
        if (screen != null) {
            icon.setAbsoluteSize(screen.x - this.getX(), screen.y - this.getY() - 16);
        }
    }
}

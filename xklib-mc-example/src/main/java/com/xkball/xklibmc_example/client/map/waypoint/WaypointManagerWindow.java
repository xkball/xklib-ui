package com.xkball.xklibmc_example.client.map.waypoint;

import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;

import java.util.function.Consumer;

public class WaypointManagerWindow extends ContainerWidget {

    private final WaypointStorage storage;
    private final Consumer<Waypoint> openDetail;
    private final Runnable changed;

    public WaypointManagerWindow(WaypointStorage storage, Consumer<Waypoint> openDetail, Runnable changed) {
        this.storage = storage;
        this.openDetail = openDetail;
        this.changed = changed;
        this.inlineStyle("""
                flex-direction: column;
                size: 100% 100%;
                overflow-y: scroll;
                """)
                .asRootStyle("""
                        Label {
                            text-color: -1;
                            flex-shrink: 0;
                        }
                        Button {
                            height: 16rpx;
                            margin-right: 1rpx;
                            text-align: center;
                            text-scale: expand-width;
                            button-shape: round-rect;
                            button-bg-color: rgb(229,233,239);
                            text-drop-shadow: false;
                            text-extra-width: 2rpx;
                        }
                        """);
        this.rebuild();
    }

    private void rebuild() {
        this.clearChildren();
        this.addChild(this.header());
        for (var waypoint : this.storage.waypoints()) {
            this.addChild(this.row(waypoint));
        }
    }

    private Widget header() {
        return new ContainerWidget()
                .inlineStyle("flex-direction: row; height: 16rpx; flex-shrink: 0;")
                .addChild(new Label("Name").inlineStyle("width: 90rpx;"))
                .addChild(new Label("Pos").inlineStyle("width: 130rpx;"))
                .addChild(new Label("Color").inlineStyle("width: 70rpx;"))
                .addChild(new Label("Actions").inlineStyle("width: 130rpx;"));
    }

    private Widget row(Waypoint waypoint) {
        var teleport = new Button("Tp", () -> WaypointActions.teleport(waypoint));
        teleport.setEnabled(WaypointActions.canTeleport());
        return new ContainerWidget()
                .inlineStyle("flex-direction: row; height: 18rpx; flex-shrink: 0;")
                .addChild(new Button(waypoint.name(), () -> this.openDetail.accept(waypoint)).inlineStyle("width: 90rpx;"))
                .addChild(new Label(waypoint.pos().toShortString()).inlineStyle("width: 130rpx;"))
                .addChild(new Label(String.format("#%06X", waypoint.color() & 0xFFFFFF)).inlineStyle("width: 70rpx;"))
                .addChild(teleport.inlineStyle("width: 32rpx;"))
                .addChild(new Button(waypoint.hidden() ? "Show" : "Hide", () -> {
                    waypoint.setHidden(!waypoint.hidden());
                    this.storage.markDirty();
                    this.changed.run();
                    this.rebuild();
                }).inlineStyle("width: 50rpx;"))
                .addChild(new Button("Del", () -> {
                    this.storage.remove(waypoint);
                    this.changed.run();
                    this.rebuild();
                }).inlineStyle("width: 32rpx;"));
    }
}

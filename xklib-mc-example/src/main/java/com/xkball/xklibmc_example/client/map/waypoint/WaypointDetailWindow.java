package com.xkball.xklibmc_example.client.map.waypoint;

import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklibmc.ui.widget.NumberInputWidget;
import com.xkball.xklibmc.ui.widget.ObjectInputWidget;
import net.minecraft.core.BlockPos;

import java.util.UUID;

public class WaypointDetailWindow extends ContainerWidget {

    private final boolean temporary;
    private final Runnable changed;
    private final Runnable removeTemporary;
    private boolean temporaryResolved;

    public WaypointDetailWindow(WaypointStorage storage, Waypoint waypoint, boolean temporary, Runnable changed, Runnable removeTemporary) {
        this.temporary = temporary;
        this.changed = changed;
        this.removeTemporary = removeTemporary;
        this.inlineStyle("""
                flex-direction: column;
                size: 100% 100%;
                overflow-y: scroll;
                scrollbar-width: 8;
                """)
                .asRootStyle("""
                        Label {
                            text-color: -1;
                            flex-shrink: 0;
                        }
                        .action_btn {
                            size: 100% 12rpx;
                            margin-bottom: 2rpx;
                            text-align: center;
                            text-scale: expand-width;
                            button-shape: round-rect;
                            button-bg-color: rgb(229,233,239);
                            text-drop-shadow: false;
                            text-extra-width: 2rpx;
                        }
                        ObjectInputWidget {
                            size: 100% 12rpx;
                            margin-bottom: 2rpx;
                            flex-shrink: 0;
                        }
                        NumberInputWidget {
                            size: 100% 12rpx;
                            margin-bottom: 2rpx;
                            flex-shrink: 0;
                        }
                        """);
        var name = ObjectInputWidget.ofString();
        name.setAsString(waypoint.name());
        var x = NumberInputWidget.ofInt(-30000000, 30000000, 1);
        var y = NumberInputWidget.ofInt(-2048, 2048, 1);
        var z = NumberInputWidget.ofInt(-30000000, 30000000, 1);
        x.setValue(waypoint.pos().getX());
        y.setValue(waypoint.pos().getY());
        z.setValue(waypoint.pos().getZ());
        var color = ObjectInputWidget.ofString();
        color.setAsString(String.format("#%06X", waypoint.color() & 0xFFFFFF));
        name.setCallback(_ -> {
            waypoint.setName(name.getAsString());
            this.markDirtyIfFormal(storage, temporary);
            changed.run();
        });
        var posCallback = (Runnable) () -> {
            waypoint.setPos(new BlockPos(x.getValue(), y.getValue(), z.getValue()));
            this.markDirtyIfFormal(storage, temporary);
            changed.run();
        };
        x.setCallback(_ -> posCallback.run());
        y.setCallback(_ -> posCallback.run());
        z.setCallback(_ -> posCallback.run());
        color.setCallback(_ -> {
            var parsed = this.parseColor(color.getAsString(), waypoint.color());
            waypoint.setColor(parsed);
            this.markDirtyIfFormal(storage, temporary);
            changed.run();
        });
        var editor = new ContainerWidget()
                .inlineStyle("flex-direction: column; size: 90% auto; margin: 3rpx;")
                .addChild(new Label("Name").inlineStyle("height: 8rpx;"))
                .addChild(name)
                .addChild(new Label("X").inlineStyle("height: 8rpx;"))
                .addChild(x)
                .addChild(new Label("Y").inlineStyle("height: 8rpx;"))
                .addChild(y)
                .addChild(new Label("Z").inlineStyle("height: 8rpx;"))
                .addChild(z)
                .addChild(new Label("Color").inlineStyle("height: 8rpx;"))
                .addChild(color);
        var actions = new ContainerWidget().inlineStyle("flex-direction: column; size: 100% auto; margin: 3rpx;");
        var teleport = new Button("Teleport", () -> WaypointActions.teleport(waypoint)).setCSSClassName("action_btn");
        teleport.setEnabled(WaypointActions.canTeleport());
        actions.addChild(teleport);
        actions.addChild(new Button("Share", () -> WaypointActions.share(waypoint)).setCSSClassName("action_btn"));
        if (!temporary) {
            actions.addChild(new Button(waypoint.hidden() ? "Show" : "Hide", () -> {
                waypoint.setHidden(!waypoint.hidden());
                storage.markDirty();
                changed.run();
            }).setCSSClassName("action_btn"));
        }
        actions.addChild(new Button("Delete", () -> {
            if (temporary) {
                this.temporaryResolved = true;
                removeTemporary.run();
            } else {
                storage.remove(waypoint);
            }
            changed.run();
        }).setCSSClassName("action_btn"));
        if (temporary) {
            actions.addChild(new Button("Save", () -> {
                var formal = new Waypoint(UUID.randomUUID(), waypoint.name(), waypoint.pos(), waypoint.color(), false);
                storage.add(formal);
                this.temporaryResolved = true;
                removeTemporary.run();
                changed.run();
            }).setCSSClassName("action_btn"));
        }
        this.addChild(editor);
        this.addChild(actions);
    }

    @Override
    public void onRemove() {
        if (this.temporary && !this.temporaryResolved) {
            this.temporaryResolved = true;
            this.removeTemporary.run();
            this.changed.run();
        }
        super.onRemove();
    }

    private void markDirtyIfFormal(WaypointStorage storage, boolean temporary) {
        if (!temporary) {
            storage.markDirty();
        }
    }

    private int parseColor(String value, int fallback) {
        var normalized = value.trim();
        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1);
        }
        if (normalized.startsWith("0x") || normalized.startsWith("0X")) {
            normalized = normalized.substring(2);
        }
        try {
            return 0xFF000000 | Integer.parseUnsignedInt(normalized, 16);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}

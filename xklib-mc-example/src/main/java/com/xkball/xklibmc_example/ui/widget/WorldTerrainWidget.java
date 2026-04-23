package com.xkball.xklibmc_example.ui.widget;

import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.layout.IntLayoutVariable;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.IconButton;
import com.xkball.xklib.ui.widget.IconCheckBox;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.Widget;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklibmc.ui.widget.NumberInputWidget;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;
import net.minecraft.client.Minecraft;

public class WorldTerrainWidget extends ContainerWidget {
    
    public final BooleanLayoutVariable terrain = new BooleanLayoutVariable(true);
    public final BooleanLayoutVariable grid = new BooleanLayoutVariable(true);
    public final BooleanLayoutVariable player = new BooleanLayoutVariable(true);
    public final BooleanLayoutVariable debug = new BooleanLayoutVariable(false);
    public final BooleanLayoutVariable cameraTarget = new BooleanLayoutVariable(false);
    public final BooleanLayoutVariable depress_sphere = new BooleanLayoutVariable(false);
    public final IntLayoutVariable yMode = new IntLayoutVariable(1);
    public final IntLayoutVariable fixY = new IntLayoutVariable();
    public final IntLayoutVariable lodDistance = new IntLayoutVariable(500);
    public final IntLayoutVariable viewDistance = new  IntLayoutVariable(4096);
    public final WorldTerrainWidgetInner inner;
    
    public WorldTerrainWidget() {
        var level = Minecraft.getInstance().level;
        var minY = level == null ? -64 : level.getMinY();
        var maxY = level == null ? 384 : level.getMaxY();
        fixY.set(level == null ? 64 : level.getSeaLevel());
        this.inner = new WorldTerrainWidgetInner(terrain, grid, player, cameraTarget, depress_sphere, debug, yMode, fixY, lodDistance, viewDistance);
        this.inlineStyle("""
                        flex-direction: column;
                        size: 100% 100%;
                        """)
                .asRootStyle("""
                        IconCheckBox {
                            size: 14rpx 14rpx;
                            margin-top: 1rpx;
                            margin-left: 1rpx;
                            iconcheckbox-bg-color: 0xAA666666;
                            flex-shrink: 0;
                        }
                        .update_button {
                            size: content 14rpx;
                            margin-top: 1rpx;
                            margin-left: 2rpx;
                            text-align: center;
                            text-scale: expand-width;
                            button-shape: round-rect;
                            button-bg-color: rgb(229,233,239);
                            text-drop-shadow: false;
                        }
                        .property_label {
                            size: content 14rpx;
                            margin-top: 1rpx;
                            margin-left: 2rpx;
                            text-scale: expand-width;
                            text-color: -1;
                        }
                        .splitter {
                            size: 2px 100%;
                            background-color: 0xEEAAAAAA;
                            margin-left: 1rpx;
                            margin-right: 1rpx;
                            flex-shrink: 0;
                        }
                        NumberInputWidget {
                            size: 50rpx 14rpx;
                            margin-top: 1rpx;
                            margin-left: 1rpx;
                            margin-right: 1rpx;
                            flex-shrink: 0;
                        }
                        """)
                .addChild(
                        new ContainerWidget()
                                .inlineStyle("""
                                        height: 18rpx;
                                        border-top: 1rpx;
                                        border-bottom: 1rpx;
                                        border-color: 0xEEAAAAAA;
                                        scrollbar-width: 0;
                                        overflow-x: scroll;
                                        """)
                                .addChild(new Button("Force Update",() -> {
                                    var player = Minecraft.getInstance().player;
                                    var viewDistance = Minecraft.getInstance().options.renderDistance().get();
                                    if(player == null) return;
                                    TerrainChunkManager.INSTANCE.submitUpdate(player.blockPosition(),viewDistance - 1, true);
                                }).setCSSClassName("update_button").withTooltip(IComponent.literal("Update chunks in view distance.")))
                                .addChild(new Button("Request Geomatics",() -> {
                                    //TODO
                                }).setCSSClassName("update_button").withTooltip(IComponent.literal("Request Geomatics from Server(Requires permission from the server).")))
                                .addChild(new Label("LOD Distance:").setCSSClassName("property_label").withTooltip(IComponent.literal("In blocks.")))
                                .addChild(NumberInputWidget.ofInt(1,114514,16).bind(lodDistance))
                                .addChild(new Label("View Distance:").setCSSClassName("property_label").withTooltip(IComponent.literal("In blocks.")))
                                .addChild(NumberInputWidget.ofInt(256,1145141919,16).bind(viewDistance))
                )
                .addChild(
                        new ContainerWidget()
                                .inlineStyle("""
                                        height: 17rpx;
                                        border-bottom: 1rpx;
                                        border-color: 0xEEAAAAAA;
                                        scrollbar-width: 0;
                                        overflow-x: scroll;
                                        """)
                                .addChild(new IconButton(VanillaUtils.modrl("icon/locate"), inner::reLocateCamera)
                                        .inlineStyle("""
                                                size: 14rpx 14rpx;
                                                margin-top: 1rpx;
                                                margin-left: 1rpx;
                                                flex-shrink: 0;
                                                """)
                                        .withTooltip(IComponent.literal("Focus camera on yourself.")))
                                .addChild(new Widget().setCSSClassName("splitter"))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/tracked_y")).bindInGroup(0,yMode).withTooltip(IComponent.literal("camera target y tracks to terrain")))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/fixed_y")).bindInGroup(1,yMode).withTooltip(IComponent.literal("camera target y is fixed")))
                                .addChild(NumberInputWidget.ofInt(minY, maxY,1).bind(fixY))
                                .addChild(new Widget().setCSSClassName("splitter"))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/terrain")).bind(terrain).withTooltip(IComponent.literal("show terrain")))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/grid")).bind(grid).withTooltip(IComponent.literal("show grid")))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/player")).bind(player).withTooltip(IComponent.literal("show player")))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/locate_camera")).bind(cameraTarget).withTooltip(IComponent.literal("show camera target")))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/depress_sphere")).bind(depress_sphere).withTooltip(IComponent.literal("cull too near chunks")))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/debug")).bind(debug).withTooltip(IComponent.literal("show debug info")))
                )
                .addChild(inner.inlineStyle("height: 100%-35rpx;"));
    }
    
}

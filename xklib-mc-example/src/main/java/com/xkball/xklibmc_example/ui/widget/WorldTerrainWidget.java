package com.xkball.xklibmc_example.ui.widget;

import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.widget.Button;
import com.xkball.xklib.ui.widget.IconCheckBox;
import com.xkball.xklib.ui.widget.Label;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklibmc.ui.widget.NumberInputWidget;
import com.xkball.xklibmc.ui.widget.WidgetWrapper;
import com.xkball.xklibmc.utils.VanillaUtils;
import com.xkball.xklibmc_example.client.terrain.TerrainChunkManager;
import net.minecraft.client.Minecraft;

public class WorldTerrainWidget extends ContainerWidget {
    
    public final BooleanLayoutVariable terrain = new BooleanLayoutVariable(true);
    public final BooleanLayoutVariable grid =  new BooleanLayoutVariable(true);
    public final BooleanLayoutVariable player =  new BooleanLayoutVariable(true);
    public final BooleanLayoutVariable debug =  new BooleanLayoutVariable(false);
    
    public WorldTerrainWidget() {
        this.inlineStyle("""
                        display: grid;
                        size: 100% 100%;
                        grid-template-columns: 100%;
                        grid-template-rows: 18rpx 17rpx 1fr;
                        """)
                .asRootStyle("""
                        IconCheckBox {
                            size: 14rpx 14rpx;
                            margin-top: 1rpx;
                            margin-left: 1rpx;
                            iconcheckbox-bg-color: 0xAA666666;
                        }
                        .update_button {
                            size: auto 14rpx;
                            margin-top: 1rpx;
                            margin-left: 2rpx;
                            text-align: center;
                            text-scale: expand-width;
                            button-shape: round-rect;
                            button-bg-color: rgb(229,233,239);
                            text-drop-shadow: false;
                        }
                        .property_label {
                            size: auto 14rpx;
                            margin-top: 1rpx;
                            margin-left: 2rpx;
                            text-scale: expand-width;
                            text-color: -1;
                        }
                        """)
                .addChild(
                        new ContainerWidget()
                                .inlineStyle("""
                                        border-top: 1rpx;
                                        border-bottom: 1rpx;
                                        border-color: 0xEEAAAAAA;
                                        """)
                                .addChild(new Button("Force Update",() -> {
                                    var player = Minecraft.getInstance().player;
                                    var viewDistance = Minecraft.getInstance().options.renderDistance().get();
                                    if(player == null) return;
                                    TerrainChunkManager.INSTANCE.submitUpdate(player.blockPosition(),viewDistance - 1, true);
                                })
                                        .setCSSClassName("update_button")
                                        .withTooltip(IComponent.literal("Update chunks in view distance.")))
                                .addChild(new Button("Request Geomatics",() -> {
                                    //TODO
                                })
                                        .setCSSClassName("update_button")
                                        .withTooltip(IComponent.literal("Request Geomatics from Server(Requires permission from the server).")))
                                .addChild(new Label("LOD Distance:")
                                        .setCSSClassName("property_label")
                                        .withTooltip(IComponent.literal("In blocks.")))
                                .addChild(NumberInputWidget.ofInt(1,114514,16)
                                        .inlineStyle("""
                                                size: 50rpx 14rpx;
                                                margin-top: 1rpx;
                                                margin-left: 1rpx;
                                                """))
                                .addChild(new Label("View Distance:")
                                        .setCSSClassName("property_label")
                                        .withTooltip(IComponent.literal("In blocks.")))
                                .addChild(NumberInputWidget.ofInt(256,1145141919,16)
                                        .inlineStyle("""
                                                size: 50rpx 14rpx;
                                                margin-top: 1rpx;
                                                margin-left: 1rpx;
                                                """))
                )
                .addChild(
                        new ContainerWidget()
                                .inlineStyle("""
                                        border-bottom: 1rpx;
                                        border-color: 0xEEAAAAAA;
                                        """)
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/terrain")).bind(terrain).withTooltip(IComponent.literal("show terrain")))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/grid")).bind(grid).withTooltip(IComponent.literal("show grid")))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/player")).bind(player).withTooltip(IComponent.literal("show player")))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/debug")).bind(debug).withTooltip(IComponent.literal("show debug info")))
                )
                .addChild(
                        new WorldTerrainWidgetInner(terrain, grid, player, debug)
                );
    }
    
}

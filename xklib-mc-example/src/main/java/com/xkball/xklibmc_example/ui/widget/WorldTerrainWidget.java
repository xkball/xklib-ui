package com.xkball.xklibmc_example.ui.widget;

import com.xkball.xklib.ui.layout.BooleanLayoutVariable;
import com.xkball.xklib.ui.widget.IconCheckBox;
import com.xkball.xklib.ui.widget.container.ContainerWidget;
import com.xkball.xklibmc.utils.VanillaUtils;

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
                        """)
                .addChild(
                        new ContainerWidget()
                                .inlineStyle("""
                                        border-top: 1rpx;
                                        border-bottom: 1rpx;
                                        border-color: 0xEEAAAAAA;
                                        """)
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/terrain")).bind(terrain))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/grid")).bind(grid))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/player")).bind(player))
                                .addChild(new IconCheckBox(VanillaUtils.modrl("icon/debug")).bind(debug))
                )
                .addChild(
                        new ContainerWidget()
                                .inlineStyle("""
                                        border-bottom: 1rpx;
                                        border-color: 0xEEAAAAAA;
                                        """)
                )
                .addChild(
                        new WorldTerrainWidgetInner(terrain, grid, player, debug)
                );
    }
    
}

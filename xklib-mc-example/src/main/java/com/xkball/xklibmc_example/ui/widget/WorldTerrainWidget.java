package com.xkball.xklibmc_example.ui.widget;

import com.xkball.xklib.ui.widget.container.ContainerWidget;

public class WorldTerrainWidget extends ContainerWidget {
    
    public WorldTerrainWidget() {
        this.inlineStyle("""
                        display: grid;
                        size: 100% 100%;
                        grid-template-columns: 100%;
                        grid-template-rows: 16rpx 16rpx 1fr;
                        """)
                .addChild(
                        new ContainerWidget()
                                .inlineStyle("""
                                        
                                        """)
                )
                .addChild(
                        new ContainerWidget()
                                .inlineStyle("""
                                        
                                        """)
                )
                .addChild(
                        new WorldTerrainWidgetInner().inlineStyle("""
                                
                                """)
                );
    }
    
    @Override
    public boolean mouseMoved(double mouseX, double mouseY) {
        return super.mouseMoved(mouseX, mouseY);
    }
}

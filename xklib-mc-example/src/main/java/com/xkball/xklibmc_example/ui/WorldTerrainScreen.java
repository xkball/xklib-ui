package com.xkball.xklibmc_example.ui;

import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.widget.container.WindowedContainer;
import com.xkball.xklibmc.ui.XKLibBaseScreen;
import com.xkball.xklibmc_example.ui.widget.WorldTerrainWidget;
import net.minecraft.network.chat.Component;

public class WorldTerrainScreen extends XKLibBaseScreen {

    private final WindowedContainer windowLayer = new WindowedContainer();
    private final WorldTerrainWidget worldTerrainWidget = new WorldTerrainWidget(this.windowLayer);

    public WorldTerrainScreen() {
        super(Component.empty());
        this.windowLayer.inlineStyle("size: 100% 100%;");
        this.addScreenLayer(XKLibBaseScreen.frame(IComponent.literal("x3d map"), this.worldTerrainWidget));
        this.addScreenLayer(this.windowLayer);
    }

    @Override
    public void removed() {
        this.worldTerrainWidget.closeMapExtensions();
        super.removed();
    }
}

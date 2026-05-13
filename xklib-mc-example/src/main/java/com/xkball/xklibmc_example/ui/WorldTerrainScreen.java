package com.xkball.xklibmc_example.ui;

import com.xkball.xklib.ui.render.IComponent;
import com.xkball.xklib.ui.widget.container.WindowedContainer;
import com.xkball.xklibmc.ui.XKLibBaseScreen;
import com.xkball.xklibmc_example.client.compatibility.CompatibilityExtension;
import com.xkball.xklibmc_example.client.map.WorldMapExtensionServiceImpl;
import com.xkball.xklibmc_example.ui.widget.WorldTerrainWidget;
import net.minecraft.network.chat.Component;

public class WorldTerrainScreen extends XKLibBaseScreen {

    private final WindowedContainer windowLayer;
    private final WorldTerrainWidget worldTerrainWidget;
    private final WorldMapExtensionServiceImpl extensionService;

    public WorldTerrainScreen() {
        super(Component.empty());
        this.windowLayer = new WindowedContainer();
        this.windowLayer.inlineStyle("size: 100% 100%;");

        this.worldTerrainWidget = new WorldTerrainWidget(this.windowLayer);
        this.addScreenLayer(XKLibBaseScreen.frame(IComponent.literal("x3d map"), this.worldTerrainWidget));
        this.addScreenLayer(this.windowLayer);

        this.extensionService = new WorldMapExtensionServiceImpl(this.worldTerrainWidget, "");
        this.worldTerrainWidget.initExtensions(this.extensionService);

        CompatibilityExtension.showWarningIfNeeded(this.extensionService);
    }

    @Override
    public void removed() {
        this.worldTerrainWidget.closeMapExtensions();
        super.removed();
    }
}
